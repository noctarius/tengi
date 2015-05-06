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
package com.noctarius.tengi.client;

import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.listener.ConnectionConnectedListener;
import com.noctarius.tengi.logging.Logger;
import com.noctarius.tengi.logging.LoggerManager;
import com.noctarius.tengi.serialization.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

class ClientImpl
        implements Client {

    private static final Logger LOGGER = LoggerManager.getLogger(ClientImpl.class);

    private final EventLoopGroup clientGroup;
    private final Configuration configuration;

    private final Serializer serializer;

    ClientImpl(Configuration configuration) {
        this.clientGroup = new NioEventLoopGroup(5, new DefaultThreadFactory("channel-client-"));
        this.serializer = createSerializer(configuration);
        this.configuration = configuration;
    }

    @Override
    public CompletableFuture<Client> connect(String host, ConnectionConnectedListener connectedListener) {
        LOGGER.info("tengi client is connecting, transport priority: %s", configuration.getTransports());

        Bootstrap bootstrap = new Bootstrap().group(clientGroup) //
                .channel(NioSocketChannel.class);

        return null;
    }

    @Override
    public CompletableFuture<Client> connect(InetAddress address, ConnectionConnectedListener connectedListener) {
        return null;
    }

    @Override
    public CompletableFuture<Client> disconnect() {
        return null;
    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

}
