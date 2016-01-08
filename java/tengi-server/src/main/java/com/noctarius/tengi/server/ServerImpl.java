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
package com.noctarius.tengi.server;

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.impl.FutureUtil;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.impl.VersionUtil;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.EventManager;
import com.noctarius.tengi.server.spi.transport.ServerChannel;
import com.noctarius.tengi.server.spi.transport.ServerChannelFactory;
import com.noctarius.tengi.server.spi.transport.ServerTransportLayer;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.statemachine.StateMachine;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

class ServerImpl
        implements Server {

    private static final Logger LOGGER = LoggerManager.getLogger(ServerImpl.class);

    private final StateMachine<ServerState> serverState = createStateMachine();

    private final Map<Endpoint, ServerChannel> coordinates = new HashMap<>();

    private final ConnectionManager connectionManager;

    private final EventManager eventManager;

    private final ExecutorService executor;

    private final Configuration configuration;
    private final Serializer serializer;

    private final List<Channel> channels = new CopyOnWriteArrayList<>();

    ServerImpl(Configuration configuration)
            throws Exception {

        Validate.notNull("configuration", configuration);

        LOGGER.info("tengi Server [version: %s, build-date: %s] is starting", //
                VersionUtil.VERSION, VersionUtil.BUILD_DATE);

        this.configuration = configuration;
        this.executor = Executors.newFixedThreadPool(16);
        this.serializer = createSerializer(configuration);
        HandshakeHandler handshakeHandler = createHandshakeHandler(configuration);
        this.connectionManager = new ConnectionManager(configuration, createSslContext(), serializer, handshakeHandler);
        this.eventManager = new EventManager();
    }

    @Override
    public CompletableFuture<Server> start(ConnectedListener connectedListener) {
        Validate.notNull("connectedListener", connectedListener);

        return FutureUtil.executeAsync(() -> {
            if (serverState.transit(ServerState.Started)) {
                bindChannels();

                connectionManager.registerConnectedListener(connectedListener);
                connectionManager.start();
                eventManager.start();
            }
            return ServerImpl.this;
        });
    }

    @Override
    public CompletableFuture<Server> stop() {
        return FutureUtil.executeAsync(() -> {
            if (serverState.transit(ServerState.Shutdown)) {
                for (Channel channel : channels) {
                    channel.close().sync();
                }

                coordinates.values().forEach(this::stopEventLoopGroups);
                executor.shutdown();

                connectionManager.stop();
                eventManager.stop();

                serverState.transit(ServerState.Stopped);
            }
            return ServerImpl.this;
        });
    }

    private void stopEventLoopGroups(ServerChannel serverChannel) {
        EventLoopGroup bossGroup = serverChannel.bossGroup();
        EventLoopGroup workerGroup = serverChannel.workerGroup();

        bossGroup.shutdownGracefully();
        if (bossGroup != workerGroup) {
            workerGroup.shutdownGracefully();
        }
    }

    private void bindChannels()
            throws Throwable {

        Map<Endpoint, Set<Transport>> transportLayers = collectTransportLayers(configuration);
        for (Map.Entry<Endpoint, Set<Transport>> entry : transportLayers.entrySet()) {
            Endpoint endpoint = entry.getKey();
            Set<Transport> transports = entry.getValue();
            ServerTransportLayer transportLayer = (ServerTransportLayer) endpoint.getTransportLayer();
            channels.add(createChannel(transportLayer, endpoint.getPort(), transports));
        }
    }

    private SslContext createSslContext()
            throws Exception {

        SelfSignedCertificate certificate = new SelfSignedCertificate("localhost");
        return SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
    }

    private HandshakeHandler createHandshakeHandler(Configuration configuration) {
        HandshakeHandler handshakeHandler = configuration.getHandshakeHandler();
        if (handshakeHandler == null) {
            handshakeHandler = (connectionId, handshake) -> new Handshake();
        }
        return handshakeHandler;
    }

    private Map<Endpoint, Set<Transport>> collectTransportLayers(Configuration configuration) {
        Function<Transport, Endpoint> coordinater = (transport) -> {
            int port = configuration.getTransportPort(transport);
            return new Endpoint(port, transport.getTransportLayer());
        };

        Map<Endpoint, Set<Transport>> transports = configuration.getTransports().stream().filter(t -> t
                .getTransportLayer() instanceof ServerTransportLayer).collect(
                Collectors.groupingBy(coordinater, IdentityHashMap::new, Collectors.mapping(t -> t, Collectors.toSet())));

        // TODO: Check multiple socket types, same port number

        return transports;
    }

    private Channel createChannel(ServerTransportLayer transportLayer, int port, Set<Transport> transports)
            throws Throwable {

        ServerChannel serverChannel = createServerChannel(transportLayer, port, transports);

        coordinates.put(new Endpoint(port, transportLayer), serverChannel);
        return serverChannel.channel();
    }

    private ServerChannel createServerChannel(ServerTransportLayer transportLayer, int port, Set<Transport> transports)
            throws Throwable {

        ServerChannelFactory channelFactory = transportLayer.serverChannelFactory();
        return channelFactory.newServerChannel(transportLayer, port, executor, connectionManager, serializer);
    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

    private StateMachine<ServerState> createStateMachine() {
        StateMachine.Builder<ServerState> builder = StateMachine.newBuilder();
        builder.addTransition(ServerState.Prepared, ServerState.Started);
        builder.addTransition(ServerState.Started, ServerState.Shutdown);
        builder.addTransition(ServerState.Shutdown, ServerState.Stopped);
        return builder.build(ServerState.Prepared, false);
    }
}
