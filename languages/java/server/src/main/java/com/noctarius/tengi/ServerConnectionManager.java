package com.noctarius.tengi;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.net.InetSocketAddress;
import java.nio.channels.Channel;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.noctarius.tengi.TransportType;

public class ServerConnectionManager
{

    private final EnumMap<TransportType, Object> transports = new EnumMap<>( TransportType.class );

    private final Executor bossExecutor = Executors.newFixedThreadPool( 10 );

    private final Executor workerExecutor = Executors.newFixedThreadPool( 10 );

    public ServerConnectionManager( EnumSet<TransportType> configureTransports )
        throws InterruptedException
    {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group( new NioEventLoopGroup( 10 ), new NioEventLoopGroup( 10 ) );
        bootstrap.childHandler( new ChannelInitializer<SocketChannel>()
        {

            @Override
            protected void initChannel( SocketChannel channel )
                throws Exception
            {
                // TODO
                // Auto-generated
                // method stub

            }
        } ).childOption( ChannelOption.TCP_NODELAY, true ).childOption( ChannelOption.SO_KEEPALIVE, true );

        ChannelFuture future = bootstrap.bind( new InetSocketAddress( 80 ) ).sync();
    }

    public void bind()
    {

    }

    public void shutdown()
    {

    }

}
