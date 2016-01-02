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
package com.noctarius.tengi.client;

import com.noctarius.tengi.client.impl.ConnectorFactory;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.exception.IllegalTransportException;
import com.noctarius.tengi.core.impl.FutureUtil;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.listener.ClosedListener;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.statemachine.StateMachine;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

class ClientImpl
        implements Client, ClosedListener {

    private static final Logger LOGGER = LoggerManager.getLogger(ClientImpl.class);

    private final StateMachine<ClientState> clientState = createStateMachine();

    private final List<Connection> connections = new CopyOnWriteArrayList<>();

    private final EventLoopGroup clientGroup;
    private final Configuration configuration;
    private final Serializer serializer;

    ClientImpl(Configuration configuration) {
        Validate.notNull("configuration", configuration);

        // Validate all configured transports are also ConnectorFactory
        checkTransports(configuration.getTransports());

        this.clientGroup = new NioEventLoopGroup(5, new DefaultThreadFactory("channel-client-"));
        this.serializer = createSerializer(configuration);
        this.configuration = configuration;
    }

    @Override
    public CompletableFuture<Connection> connect(String host, ConnectedListener connectedListener)
            throws UnknownHostException {

        Validate.notNull("host", host);
        Validate.notNull("connectedListener", connectedListener);

        if (clientState.currentState() == ClientState.Stopped) {
            throw new IllegalStateException("Client is shutdown");
        }

        InetAddress address = InetAddress.getByName(host);
        return connect(address, connectedListener);
    }

    @Override
    public CompletableFuture<Connection> connect(InetAddress address, ConnectedListener connectedListener) {
        Validate.notNull("address", address);
        Validate.notNull("connectedListener", connectedListener);

        if (clientState.currentState() == ClientState.Stopped) {
            throw new IllegalStateException("Client is shutdown");
        }

        LOGGER.info("tengi client is connecting, transport priority: %s", configuration.getTransports());
        ConnectorContext connectorContext = new ConnectorContext(configuration, serializer, clientGroup);

        CompletableFuture<Connection> connectFuture = connectorContext.connect(address);
        return connectFuture.thenApply((connection) -> {
            registerConnection(connection);
            if (clientState.transit(ClientState.Started)) {
                connectedListener.onConnection(connection);
            }
            return connection;
        });
    }

    @Override
    public CompletableFuture<Client> stop() {
        return FutureUtil.executeAsync(() -> {
            if (clientState.transit(ClientState.Shutdown)) {
                if (!clientGroup.isTerminated()) {
                    clientGroup.shutdownGracefully();
                }

                for (Connection connection : connections) {
                    connection.close();
                }

                clientState.transit(ClientState.Stopped);
            }
            return ClientImpl.this;
        });
    }

    @Override
    public void onClose(Connection connection) {
        connections.remove(connection);
    }

    private void registerConnection(Connection connection) {
        connections.add(connection);
        connection.addConnectionListener(this);
    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

    private StateMachine<ClientState> createStateMachine() {
        StateMachine.Builder<ClientState> builder = StateMachine.newBuilder();
        builder.addTransition(ClientState.Prepared, ClientState.Started);
        builder.addTransition(ClientState.Started, ClientState.Shutdown);
        builder.addTransition(ClientState.Shutdown, ClientState.Stopped);
        return builder.build(ClientState.Prepared, false);
    }

    private void checkTransports(List<Transport> transports) {
        for (Transport transport : transports) {
            if (!(transport instanceof ConnectorFactory)) {
                throw new IllegalTransportException("Illegal Transport configured: " + transport);
            }
        }
    }
}
