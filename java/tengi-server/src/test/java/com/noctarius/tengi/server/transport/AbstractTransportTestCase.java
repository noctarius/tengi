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
package com.noctarius.tengi.server.transport;

import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.config.ConfigurationBuilder;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.server.server.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public abstract class AbstractTransportTestCase {

    protected static void practice(Initializer initializer, Runner runner, boolean ssl, Transport... serverTransports)
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(serverTransports).build();
        Server server = Server.create(configuration);
        server.start(AbstractTransportTestCase::onConnection);

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap().group(group) //
                    .channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel)
                                throws Exception {

                            ChannelPipeline pipeline = channel.pipeline();

                            if (ssl) {
                                SslContext sslContext = SslContextBuilder //
                                        .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

                                pipeline.addLast(sslContext.newHandler(channel.alloc(), "localhost", 8080));
                            }

                            initializer.initChannel(pipeline);
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8080);
            Channel channel = future.sync().channel();
            runner.run(channel);

        } finally {
            group.shutdownGracefully();
            server.stop();
        }
    }

    protected static <T> SimpleChannelInboundHandler<T> inboundHandler(ChannelReader<T> channelReader) {
        return new SimpleChannelInboundHandler<T>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, T object)
                    throws Exception {

                channelReader.channelRead(ctx, object);
            }
        };
    }

    private static void onConnection(Connection connection) {
        connection.addMessageListener(AbstractTransportTestCase::onMessage);
    }

    private static void onMessage(Connection connection, Message message) {
        try {
            connection.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static interface Initializer {
        void initChannel(ChannelPipeline pipeline)
                throws Exception;
    }

    protected static interface Runner {
        void run(Channel channel)
                throws Exception;
    }

    protected static interface ChannelReader<T> {
        void channelRead(ChannelHandlerContext ctx, T object)
                throws Exception;
    }
}
