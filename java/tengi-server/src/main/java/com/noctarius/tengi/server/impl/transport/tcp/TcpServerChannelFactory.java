/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.server.impl.transport.tcp;

import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.NettyServerChannel;
import com.noctarius.tengi.server.impl.transport.negotiation.NegotiationChannelHandler;
import com.noctarius.tengi.server.spi.transport.Endpoint;
import com.noctarius.tengi.server.spi.transport.ServerChannel;
import com.noctarius.tengi.server.spi.transport.ServerChannelFactory;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executor;

public class TcpServerChannelFactory
        implements ServerChannelFactory {

    @Override
    public ServerChannel newServerChannel(Endpoint endpoint, Executor executor, ConnectionManager connectionManager,
                                          Serializer serializer)
            throws Throwable {

        int port = endpoint.getPort();
        TransportLayer transportLayer = endpoint.getTransportLayer();

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(8, executor);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024) //
                 .group(eventLoopGroup, eventLoopGroup) //
                 .channel(NioServerSocketChannel.class) //
                 .childHandler(new TcpChannelInitializer(connectionManager, serializer, transportLayer, port));

        ChannelFuture future = bootstrap.bind(port).sync();
        if (future.cause() != null) {
            throw future.cause();
        }
        return new NettyServerChannel(future.channel(), eventLoopGroup, eventLoopGroup, port, transportLayer);
    }

    private static class TcpChannelInitializer
            extends ChannelInitializer<SocketChannel> {

        private final ConnectionManager connectionManager;
        private final TransportLayer transportLayer;
        private final Serializer serializer;
        private final int port;

        private TcpChannelInitializer(ConnectionManager connectionManager, Serializer serializer, TransportLayer transportLayer,
                                      int port) {

            this.connectionManager = connectionManager;
            this.transportLayer = transportLayer;
            this.serializer = serializer;
            this.port = port;
        }

        @Override
        protected void initChannel(SocketChannel channel)
                throws Exception {

            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new NegotiationChannelHandler(port, transportLayer, connectionManager, serializer));
        }
    }
}
