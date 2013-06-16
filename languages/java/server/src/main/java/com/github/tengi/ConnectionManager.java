package com.github.tengi;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.InetAddress;
import java.nio.channels.spi.SelectorProvider;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.eclipse.jetty.npn.NextProtoNego;

import com.github.tengi.transport.protocol.NGNServerProvider;
import com.github.tengi.transport.protocol.TengiUnificatedProtocolNegotiator;
import com.github.tengi.transport.protocol.handler.TengiUdpRequestHandler;

public class ConnectionManager
{

    private static final Logger LOGGER = Logger.getLogger( ConnectionManager.class );

    private static final EnumSet<TransportType> SUPPORTED_TRANSPORT_TYPES = EnumSet.of( TransportType.NioTcpSocket,
                                                                                        TransportType.NioUdpSocket,
                                                                                        TransportType.SPDY,
                                                                                        TransportType.WebSocket,
                                                                                        TransportType.HttpLongPolling,
                                                                                        TransportType.HttpPolling );

    private final Map<UniqueId, Connection> connections = new HashMap<UniqueId, Connection>();

    private final EventExecutorGroup eventExecutorGroup =
        new DefaultEventExecutorGroup( 20, new DefaultThreadFactory( "groupEventExecutor" ) );

    private final ChannelGroup channelGroup = new DefaultChannelGroup( eventExecutorGroup.next() );

    private final AtomicBoolean shutdown = new AtomicBoolean();

    private final ConnectionConfiguration configuration;

    private final ConnectionListener connectionListener;

    private final ServerBootstrap tcpServerBootstrap;

    private final Bootstrap udpServerBootstrap;

    private final EventLoopGroup tcpAcceptorGroup;

    private final EventLoopGroup tcpProcessorGroup;

    private final EventLoopGroup udpProcessorGroup;

    private final SSLContext sslContext;

    private final SSLEngine sslEngine;

    public ConnectionManager( ConnectionConfiguration configuration, ConnectionListener connectionListener )
    {
        this.connectionListener = connectionListener;
        this.configuration = configuration;

        try
        {
            this.sslContext = SSLContext.getDefault();
            this.sslEngine = sslContext.createSSLEngine();
            NextProtoNego.put( sslEngine, new NGNServerProvider() );
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new RuntimeException( "Could not startup NGN service", e );
        }

        this.tcpAcceptorGroup = buildEventLoopGroup( 4, "TCP-Acceptor" );
        this.tcpProcessorGroup = buildEventLoopGroup( 20, "TCP-Processor" );

        this.udpProcessorGroup = buildEventLoopGroup( 10, "UDP-Processor" );

        this.tcpServerBootstrap = buildTcpSocket( tcpAcceptorGroup, tcpProcessorGroup );
        this.udpServerBootstrap = buildUdpSocket( udpProcessorGroup );
    }

    public ConnectionManager( ConnectionConfiguration configuration, ConnectionListener connectionListener,
                              SSLContext sslContext, SSLEngine sslEngine )
    {
        this.connectionListener = connectionListener;
        this.configuration = configuration;

        this.sslContext = sslContext;
        this.sslEngine = sslEngine;
        NextProtoNego.put( sslEngine, new NGNServerProvider() );

        this.tcpAcceptorGroup = buildEventLoopGroup( 4, "TCP-Acceptor" );
        this.tcpProcessorGroup = buildEventLoopGroup( 20, "TCP-Processor" );

        this.udpProcessorGroup = buildEventLoopGroup( 10, "UDP-Processor" );

        this.tcpServerBootstrap = buildTcpSocket( tcpAcceptorGroup, tcpProcessorGroup );
        this.udpServerBootstrap = buildUdpSocket( udpProcessorGroup );
    }

    public void bind()
    {
        for ( InetAddress address : configuration.getAddresses() )
        {
            bind0( configuration.getPort(), address );
        }
    }

    private void bind0( int port, InetAddress address )
    {
        ChannelFutureListener listener = new ChannelFutureListener()
        {

            @Override
            public void operationComplete( ChannelFuture future )
                throws Exception
            {
                Channel channel = future.channel();
                channelGroup.add( channel );
                LOGGER.info( "Registered " + channel.toString() );
            }
        };

        ChannelFuture tcpFuture = tcpServerBootstrap.bind( address, port );
        tcpFuture.addListener( listener );

        ChannelFuture udpFuture = udpServerBootstrap.bind( address, port );
        udpFuture.addListener( listener );
    }

    public Connection getConnectionByConnectionId( UniqueId connectionId )
    {
        return connections.get( connectionId );
    }

    public Connection registerConnection( UniqueId connectionId, Channel channel, TransportType transportType )
    {
        if ( shutdown.get() )
        {
            return null;
        }

        return null; // TODO
    }

    public void registerConnection( UniqueId connectionId, Connection connection )
    {
        if ( shutdown.get() )
        {
            return;
        }

        connections.put( connectionId, connection );
    }

    public void deregisterConnection( UniqueId connectionId )
    {
        connections.remove( connectionId );
    }

    public void deregisterConnection( Connection connection )
    {
        Iterator<Entry<UniqueId, Connection>> iterator = connections.entrySet().iterator();
        while ( iterator.hasNext() )
        {
            Entry<UniqueId, Connection> entry = iterator.next();
            if ( entry.getValue() == connection )
            {
                iterator.remove();
            }
        }
    }

    public void shutdown()
    {
        if ( !shutdown.compareAndSet( false, true ) )
        {
            return;
        }

        channelGroup.close().syncUninterruptibly();

        for ( Connection connection : connections.values() )
        {
            connection.close();
        }
    }

    public SSLEngine getSSLEngine()
    {
        return sslEngine;
    }

    public EnumSet<TransportType> getSupportedTransportTypes()
    {
        return SUPPORTED_TRANSPORT_TYPES;
    }

    private EventLoopGroup buildEventLoopGroup( int threads, String threadPrefix )
    {
        return buildEventLoopGroup( threads, threadPrefix, SelectorProvider.provider() );
    }

    private EventLoopGroup buildEventLoopGroup( int threads, String threadPrefix, SelectorProvider selectorProvider )
    {
        ThreadFactory threadFactory = new DefaultThreadFactory( threadPrefix );
        return new NioEventLoopGroup( threads, threadFactory, selectorProvider );
    }

    private ServerBootstrap buildTcpSocket( EventLoopGroup acceptorGroup, EventLoopGroup processorGroup )
    {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group( acceptorGroup, processorGroup ).channel( NioServerSocketChannel.class );
        bootstrap.option( ChannelOption.SO_REUSEADDR, true ).option( ChannelOption.SO_BACKLOG, 20 );
        bootstrap.childOption( ChannelOption.TCP_NODELAY, true ).childOption( ChannelOption.SO_LINGER, 0 );
        bootstrap.childOption( ChannelOption.SO_KEEPALIVE, true );
        bootstrap.childHandler( new ChannelInitializer<NioServerSocketChannel>()
        {
            @Override
            protected void initChannel( NioServerSocketChannel channel )
                throws Exception
            {
                channel.pipeline().addLast( new TengiUnificatedProtocolNegotiator( true, true, ConnectionManager.this ) );
            }
        } );
        return bootstrap;
    }

    private Bootstrap buildUdpSocket( EventLoopGroup udpProcessorGroup )
    {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group( udpProcessorGroup ).channel( NioDatagramChannel.class );
        bootstrap.handler( new TengiUdpRequestHandler( this ) );
        return bootstrap;
    }
}
