/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.tengi.server;

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.impl.CompletableFutureUtil;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.impl.VersionUtil;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.EventManager;
import com.noctarius.tengi.server.impl.transport.negotiation.TcpBinaryNegotiator;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
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

    private static final Logger LOGGER = LoggerManager.getLogger(ServerImpl.class);

    private final ConnectionManager connectionManager;

    private final EventManager eventManager;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final Serializer serializer;

    private volatile Channel serverChannel;

    ServerImpl(Configuration configuration)
            throws Exception {

        Validate.notNull("configuration", configuration);

        LOGGER.info("tengi Server [version: %s, build-date: %s] is starting", //
                VersionUtil.VERSION, VersionUtil.BUILD_DATE);

        this.bossGroup = createEventLoopGroup(5, "boss");
        this.workerGroup = createEventLoopGroup(5, "worker");
        this.serializer = createSerializer(configuration);
        this.connectionManager = new ConnectionManager(createSslContext(), serializer);
        this.eventManager = new EventManager();
    }

    @Override
    public CompletableFuture<Channel> start(ConnectedListener connectedListener) {
        Validate.notNull("connectedListener", connectedListener);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024).group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                 .childHandler(new ProtocolNegotiator(connectionManager, serializer));

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
            if (serverChannel != null) {
                serverChannel.close().sync();
            }

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            connectionManager.stop();
            eventManager.stop();

            return serverChannel;
        });
    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

    private EventLoopGroup createEventLoopGroup(int threadCount, String name) {
        LOGGER.debug("Creating event threadpool '%s' with %s threads", name, threadCount);
        return new NioEventLoopGroup(threadCount, new DefaultThreadFactory("channel-" + name + "-"));
    }

    private SslContext createSslContext()
            throws Exception {

        SelfSignedCertificate certificate = new SelfSignedCertificate("localhost");
        return SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
    }

    private static class ProtocolNegotiator
            extends ChannelInitializer<SocketChannel> {

        private final ConnectionManager connectionManager;
        private final Serializer serializer;

        private ProtocolNegotiator(ConnectionManager connectionManager, Serializer serializer) {
            this.connectionManager = connectionManager;
            this.serializer = serializer;
        }

        @Override
        protected void initChannel(SocketChannel channel)
                throws Exception {

            channel.pipeline().addLast(new TcpBinaryNegotiator(true, true, connectionManager, serializer));
        }
    }
}
