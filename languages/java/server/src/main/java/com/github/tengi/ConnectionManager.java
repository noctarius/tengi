package com.github.tengi;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.nio.NioUdtProvider;

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

import org.eclipse.jetty.npn.NextProtoNego;

import com.github.tengi.transport.protocol.NGNServerProvider;
import com.github.tengi.transport.protocol.TengiUnificatedProtocolNegotiator;
import com.github.tengi.transport.protocol.handler.TengiUdpRequestHandler;
import com.github.tengi.utils.NamedThreadFactory;

public class ConnectionManager
{

    private final EnumSet<TransportType> SUPPORTED_TRANSPORT_TYPES = EnumSet.of( TransportType.NioTcpSocket,
                                                                                 TransportType.NioUdpSocket,
                                                                                 TransportType.SPDY,
                                                                                 TransportType.WebSocket,
                                                                                 TransportType.HttpLongPolling,
                                                                                 TransportType.HttpPolling );

    private final Map<UniqueId, Connection> connections = new HashMap<UniqueId, Connection>();

    private final ChannelGroup channelGroup = new DefaultChannelGroup();

    private final AtomicBoolean shutdown = new AtomicBoolean();

    private final ServerBootstrap tcpServerBootstrap;

    private final Bootstrap udpServerBootstrap;

    private final EventLoopGroup tcpAcceptorGroup;

    private final EventLoopGroup tcpProcessorGroup;

    private final EventLoopGroup udpProcessorGroup;

    private final SSLContext sslContext;

    private final SSLEngine sslEngine;

    public ConnectionManager()
        throws NoSuchAlgorithmException
    {
        sslContext = SSLContext.getDefault();
        sslEngine = sslContext.createSSLEngine();
        NextProtoNego.put( sslEngine, new NGNServerProvider() );

        tcpAcceptorGroup = buildEventLoopGroup( 4, "TCP-Acceptor" );
        tcpProcessorGroup = buildEventLoopGroup( 20, "TCP-Processor" );

        udpProcessorGroup = buildEventLoopGroup( 10, "UDP-Processor", NioUdtProvider.MESSAGE_PROVIDER );

        tcpServerBootstrap = buildTcpSocket( tcpAcceptorGroup, tcpProcessorGroup );
        udpServerBootstrap = buildUdpSocket( udpProcessorGroup );
    }

    public void bind( int port, InetAddress[] addresses )
    {
        for ( InetAddress address : addresses )
        {
            ChannelFuture future = tcpServerBootstrap.bind( address, port );
            channelGroup.add( future.channel() );

            future = udpServerBootstrap.bind( address, port );
            channelGroup.add( future.channel() );
        }
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
        ThreadFactory threadFactory = new NamedThreadFactory( threadPrefix );
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
        bootstrap.group( udpProcessorGroup ).channelFactory( NioUdtProvider.MESSAGE_ACCEPTOR );
        bootstrap.option( ChannelOption.SO_BACKLOG, 10 ).handler( new TengiUdpRequestHandler( this ) );
        return bootstrap;
    }
}
