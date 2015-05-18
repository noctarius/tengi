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
package com.noctarius.tengi.server.impl;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.exception.NoSuchConnectionException;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.spi.connection.packets.LongPollingRequest;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.ConcurrentSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager
        implements Service {

    private final Set<ConnectedListener> connectedListeners = new ConcurrentSet<>();
    private final Map<Identifier, ClientConnection> connections = new ConcurrentHashMap<>();

    private final SslContext sslContext;
    private final Serializer serializer;

    public ConnectionManager(SslContext sslContext, Serializer serializer) {
        this.sslContext = sslContext;
        this.serializer = serializer;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public void registerConnectedListener(ConnectedListener connectedListener) {
        connectedListeners.add(connectedListener);
    }

    public Connection assignConnection(Identifier connectionId, ConnectionContext connectionContext, Transport transport) {
        Connection connection = connections.computeIfAbsent(connectionId,
                (key) -> new ClientConnection(connectionContext, connectionId, transport, serializer));

        connectedListeners.forEach((listener) -> listener.onConnection(connection));
        return connection;
    }

    public void publishMessage(Channel channel, Identifier connectionId, Message message) {
        ClientConnection connection = connections.get(connectionId);
        if (connection == null) {
            throw new NoSuchConnectionException("ConnectionId '" + connectionId.toString() + "' is not registered");
        }

        if (!connection.getTransport().isStreaming() && message.getBody() instanceof LongPollingRequest) {
            LongPollingRequest request = message.getBody();
            connection.getConnectionContext().processLongPollingRequest(channel, connection, request);

        } else {
            connection.publishMessage(message);
        }
    }

    public void exceptionally(Identifier connectionId, Throwable throwable) {
        ClientConnection connection = connections.get(connectionId);
        if (connection != null) {
            connection.exceptionally(throwable);
        }
    }

}
