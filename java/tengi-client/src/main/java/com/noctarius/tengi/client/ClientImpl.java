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

import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ConnectorFactory;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.connection.handshake.HandshakeHandler;
import com.noctarius.tengi.core.exception.ConnectionFailedException;
import com.noctarius.tengi.core.exception.IllegalTransportException;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

class ClientImpl
        implements Client {

    private static final Logger LOGGER = LoggerManager.getLogger(ClientImpl.class);

    private final EventLoopGroup clientGroup;
    private final Configuration configuration;
    private final Serializer serializer;
    private final HandshakeHandler handshakeHandler;

    ClientImpl(Configuration configuration) {
        Validate.notNull("configuration", configuration);

        this.clientGroup = new NioEventLoopGroup(5, new DefaultThreadFactory("channel-client-"));
        this.serializer = createSerializer(configuration);
        this.configuration = configuration;
        this.handshakeHandler = createHandshakeHandler(configuration);
        checkTransports(configuration.getTransports());
    }

    @Override
    public CompletableFuture<Connection> connect(String host, ConnectedListener connectedListener)
            throws UnknownHostException {

        Validate.notNull("host", host);
        Validate.notNull("connectedListener", connectedListener);

        InetAddress address = InetAddress.getByName(host);
        return connect(address, connectedListener);
    }

    @Override
    public CompletableFuture<Connection> connect(InetAddress address, ConnectedListener connectedListener) {
        Validate.notNull("address", address);
        Validate.notNull("connectedListener", connectedListener);

        Connector[] connectors = createConnectors(address, configuration.getTransports());
        LOGGER.info("tengi client is connecting, transport priority: %s", configuration.getTransports());
        CompletableFuture<Connection> future = new CompletableFuture<>();
        connect(address, future, connectedListener, connectors, 0);
        return future;
    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

    private void checkTransports(List<Transport> transports) {
        for (Transport transport : transports) {
            if (!(transport instanceof ConnectorFactory)) {
                throw new IllegalTransportException("Illegal Transport configured: " + transport);
            }
        }
    }

    private Connector[] createConnectors(InetAddress address, List<Transport> transports) {
        Connector[] connectors = new Connector[transports.size()];
        int pos = 0;
        for (Transport transport : transports) {
            int port = configuration.getTransportPort(transport);
            connectors[pos++] = ((ConnectorFactory) transport).create(address, port, serializer, handshakeHandler, clientGroup);
        }
        return connectors;
    }

    private void connect(InetAddress address, CompletableFuture<Connection> future, //
                         ConnectedListener connectedListener, Connector[] connectors, int index) {

        if (index >= connectors.length) {
            future.completeExceptionally(new ConnectionFailedException("No transport was able to connect"));
        }
        connectTransport(address, future, connectedListener, connectors, index);
    }

    private void connectTransport(InetAddress address, CompletableFuture<Connection> future, //
                                  ConnectedListener connectedListener, Connector[] connectors, int index) {

        Connector connector = connectors[index];
        CompletableFuture<Connection> connectFuture = connector.connect();
        connectFuture.thenAccept(transportHandler(address, future, connectedListener, connectors, index));
    }

    private HandshakeHandler createHandshakeHandler(Configuration configuration) {
        HandshakeHandler handshakeHandler = configuration.getHandshakeHandler();
        if (handshakeHandler == null) {
            handshakeHandler = (connectionId, handshake) -> null;
        }
        return handshakeHandler;
    }

    private Consumer<? super Connection> transportHandler(InetAddress address, CompletableFuture<Connection> future, //
                                                          ConnectedListener connectedListener, Connector[] connectors,
                                                          int index) {

        return (connection) -> {
            if (connection == null) {
                connect(address, future, connectedListener, connectors, index + 1);
            } else {
                if (connectedListener != null) {
                    connectedListener.onConnection(connection);
                }
                future.complete(connection);
            }
        };
    }

}
