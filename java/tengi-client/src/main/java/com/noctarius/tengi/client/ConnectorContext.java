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

import com.noctarius.tengi.client.impl.ConnectCallback;
import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ConnectorFactory;
import com.noctarius.tengi.client.impl.TransportHandler;
import com.noctarius.tengi.client.impl.config.ClientConfiguration;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.exception.ConnectionFailedException;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.channel.EventLoopGroup;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

class ConnectorContext {

    private final Configuration configuration;
    private final Serializer serializer;
    private final TransportHandler transportHandler;
    private final HandshakeHandler handshakeHandler;
    private final EventLoopGroup clientGroup;

    ConnectorContext(Configuration configuration, Serializer serializer, EventLoopGroup clientGroup) {

        this.configuration = configuration;
        this.serializer = serializer;
        this.clientGroup = clientGroup;
        this.transportHandler = getConnectorHandler(configuration);
        this.handshakeHandler = createHandshakeHandler(configuration);
    }

    CompletableFuture<Connection> connect(InetAddress address) {
        CompletableFuture<Connection> future = new CompletableFuture<>();
        ConnectCallback connectCallback = connectFuture(future);

        Iterator<Transport> transportIterator = configuration.getTransports().iterator();
        connect0(address, connectCallback, transportIterator);

        return future;
    }

    private void connect0(InetAddress address, ConnectCallback connectCallback, Iterator<Transport> transportIterator) {
        if (!transportIterator.hasNext()) {
            // Reached end of configured transports
            connectCallback.on(new ConnectionFailedException("No transport was able to connect"));
            return;
        }

        Transport transport = transportIterator.next();
        int port = configuration.getTransportPort(transport);

        // Safe cast do to check in ClientImpl
        Connector connector = ((ConnectorFactory) transport).create(address, port, serializer, handshakeHandler, clientGroup);

        // Start connection try
        connector.connect(connectCallback(connector, address, connectCallback, transportIterator));
    }

    private HandshakeHandler createHandshakeHandler(Configuration configuration) {
        HandshakeHandler handshakeHandler = configuration.getHandshakeHandler();
        if (handshakeHandler == null) {
            handshakeHandler = (connectionId, handshake) -> null;
        }
        return handshakeHandler;
    }

    private TransportHandler getConnectorHandler(Configuration configuration) {
        if (configuration instanceof ClientConfiguration) {
            return ((ClientConfiguration) configuration).getTransportHandler();
        }
        return null;
    }

    private ConnectCallback connectCallback(Connector connector, InetAddress address, ConnectCallback connectCallback,
                                            Iterator<Transport> transportIterator) {

        return (connection, throwable) -> {
            if (transportHandler != null) {
                transportHandler.onConnector(connector, throwable == null && connection != null, throwable);
            }
            if (throwable != null || connection == null) {
                connect0(address, connectCallback, transportIterator);
            } else {
                connectCallback.on(connection);
            }
        };
    }

    private ConnectCallback connectFuture(CompletableFuture<Connection> future) {
        return (connection, throwable) -> {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                future.complete(connection);
            }
        };
    }

}
