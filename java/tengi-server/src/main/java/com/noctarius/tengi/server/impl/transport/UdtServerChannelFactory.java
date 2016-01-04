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
package com.noctarius.tengi.server.impl.transport;

import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.udt.UdtConnectionProcessor;
import com.noctarius.tengi.server.spi.transport.ServerChannel;
import com.noctarius.tengi.server.spi.transport.ServerChannelFactory;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;

import java.util.concurrent.Executor;

public class UdtServerChannelFactory
        implements ServerChannelFactory {

    @Override
    public ServerChannel newServerChannel(TransportLayer transportLayer, int port, Executor executor,
                                          ConnectionManager connectionManager, Serializer serializer)
            throws Throwable {

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(8, executor, NioUdtProvider.BYTE_PROVIDER);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024) //
                 .group(eventLoopGroup, eventLoopGroup) //
                 .channelFactory(NioUdtProvider.BYTE_ACCEPTOR) //
                 .childHandler(new UdtChannelInitializer(connectionManager, serializer));

        ChannelFuture future = bootstrap.bind(port).sync();
        if (future.cause() != null) {
            throw future.cause();
        }
        return new SimpleServerChannel(future.channel(), eventLoopGroup, eventLoopGroup, port, transportLayer);
    }

    private static class UdtChannelInitializer
            extends ChannelInitializer<UdtChannel> {

        private final ConnectionManager connectionManager;
        private final Serializer serializer;

        private UdtChannelInitializer(ConnectionManager connectionManager, Serializer serializer) {
            this.connectionManager = connectionManager;
            this.serializer = serializer;
        }

        @Override
        protected void initChannel(UdtChannel channel)
                throws Exception {

            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast("udt-connection-processor", new UdtConnectionProcessor(connectionManager, serializer));
        }
    }
}
