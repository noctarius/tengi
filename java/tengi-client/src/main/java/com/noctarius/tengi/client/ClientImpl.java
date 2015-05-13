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

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.SystemException;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.listener.ConnectionConnectedListener;
import com.noctarius.tengi.logging.Logger;
import com.noctarius.tengi.logging.LoggerManager;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.utils.CompletableFutureUtil;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class ClientImpl
        implements Client, MessagePublisher {

    private static final Logger LOGGER = LoggerManager.getLogger(ClientImpl.class);

    private final AtomicBoolean disconnected = new AtomicBoolean(false);

    private final EventLoopGroup clientGroup;
    private final Configuration configuration;
    private final Connector[] connectors;
    private final Serializer serializer;

    private volatile Connector connector;

    ClientImpl(Configuration configuration) {
        this.clientGroup = new NioEventLoopGroup(5, new DefaultThreadFactory("channel-client-"));
        this.serializer = createSerializer(configuration);
        this.connectors = checkTransports(configuration.getTransports());
        this.configuration = configuration;
    }

    @Override
    public CompletableFuture<Client> connect(String host, ConnectionConnectedListener connectedListener)
            throws UnknownHostException {

        InetAddress address = InetAddress.getByName(host);
        return connect(address, connectedListener);
    }

    @Override
    public CompletableFuture<Client> connect(InetAddress address, ConnectionConnectedListener connectedListener) {
        if (!disconnected.get()) {
            LOGGER.info("tengi client is connecting, transport priority: %s", configuration.getTransports());
            CompletableFuture<Client> future = new CompletableFuture<>();
            connect(address, future, connectedListener, 0);
            return future;
        }

        throw new SystemException("Client is already destroyed");
    }

    @Override
    public CompletableFuture<Client> disconnect() {
        return CompletableFutureUtil.executeAsync(() -> {
            if (disconnected.compareAndSet(false, true)) {
                Connector connector = this.connector;
                if (connector != null) {
                    connector.destroy();
                }
                clientGroup.shutdownGracefully();
            }
            return ClientImpl.this;
        });
    }

    @Override
    public void publishMessage(Channel channel, Identifier connectionId, Message message) {

    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

    private Connector[] checkTransports(Set<Transport> transports) {
        Connector[] connectors = new Connector[transports.size()];
        int pos = 0;
        for (Transport transport : transports) {
            if (transport instanceof ConnectorFactory) {
                connectors[pos++] = ((ConnectorFactory) transport).create();
            } else {
                throw new SystemException("Illegal Transport configured: " + transport);
            }
        }
        return connectors;
    }

    private void connect(InetAddress address, CompletableFuture<Client> future, //
                         ConnectionConnectedListener connectedListener, int index) {

        if (index >= connectors.length) {
            future.completeExceptionally(new SystemException("No transport was able to connect"));
        }
        connectTransport(address, future, connectedListener, index);
    }

    private void connectTransport(InetAddress address, CompletableFuture<Client> future, //
                                  ConnectionConnectedListener connectedListener, int index) {

        Connector connector = connectors[index];
        int port = configuration.getTransportPort(connector);
        CompletableFuture<Connection> connectFuture = connector.connect(address, port, this, serializer, clientGroup);
        connectFuture.thenAccept(transportHandler(address, future, connectedListener, index));
    }

    private Consumer<? super Connection> transportHandler(InetAddress address, CompletableFuture<Client> future, //
                                                          ConnectionConnectedListener connectedListener, int index) {

        return (connection) -> {
            if (connection == null) {
                connect(address, future, connectedListener, index + 1);
            } else {
                this.connector = connectors[index];
                connectedListener.onConnectionAccept(connection);
                future.complete(ClientImpl.this);
            }
        };
    }

}
