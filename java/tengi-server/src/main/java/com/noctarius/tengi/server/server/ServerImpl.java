package com.noctarius.tengi.server.server;

import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.listener.ConnectionConnectedListener;
import com.noctarius.tengi.server.transport.impl.negotiation.TcpBinaryNegotiator;
import com.noctarius.tengi.utils.CompletableFutureUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.CompletableFuture;

class ServerImpl
        implements Server {

    private final Configuration configuration;
    private final ConnectionManager connectionManager;

    private final EventManager eventManager;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private volatile Channel serverChannel;

    ServerImpl(Configuration configuration)
            throws Exception {

        this.configuration = configuration;
        this.bossGroup = new NioEventLoopGroup(5, new DefaultThreadFactory("channel-boss-"));
        this.workerGroup = new NioEventLoopGroup(5, new DefaultThreadFactory("channel-worker-"));
        this.connectionManager = new ConnectionManager(createSslContext());
        this.eventManager = new EventManager();
    }

    @Override
    public CompletableFuture<Channel> start(ConnectionConnectedListener connectedListener) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024).group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                 .childHandler(new ProtocolNegotiator(connectionManager));

        ChannelFuture future = bootstrap.bind(8080);
        return CompletableFutureUtil.executeAsync(() -> {
            Channel serverChannel = future.sync().channel();

            connectionManager.registerConnectedListener(connectedListener);
            connectionManager.start();
            eventManager.start();

            return (this.serverChannel = serverChannel);
        });
    }

    @Override
    public CompletableFuture<Channel> stop() {
        return CompletableFutureUtil.executeAsync(() -> {
            serverChannel.closeFuture().sync();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            connectionManager.stop();
            eventManager.stop();

            return serverChannel;
        });
    }

    private SslContext createSslContext()
            throws Exception {

        SelfSignedCertificate certificate = new SelfSignedCertificate("localhost");
        return SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
    }

    private static class ProtocolNegotiator
            extends ChannelInitializer<SocketChannel> {

        private final ConnectionManager connectionManager;

        private ProtocolNegotiator(ConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        @Override
        protected void initChannel(SocketChannel channel)
                throws Exception {

            channel.pipeline().addLast(new TcpBinaryNegotiator(true, true, connectionManager));
        }
    }
}
