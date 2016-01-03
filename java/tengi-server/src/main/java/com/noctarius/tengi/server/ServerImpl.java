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
import com.noctarius.tengi.core.exception.IllegalTransportException;
import com.noctarius.tengi.core.impl.FutureUtil;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.impl.VersionUtil;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.EventManager;
import com.noctarius.tengi.server.spi.transport.ServerChannelFactory;
import com.noctarius.tengi.server.spi.transport.ServerTransportLayer;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.statemachine.StateMachine;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

class ServerImpl
        implements Server {

    private static final Logger LOGGER = LoggerManager.getLogger(ServerImpl.class);

    private final StateMachine<ServerState> serverState = createStateMachine();

    private final ConnectionManager connectionManager;

    private final EventManager eventManager;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final Configuration configuration;
    private final Serializer serializer;

    private final List<Channel> channels = new CopyOnWriteArrayList<>();

    ServerImpl(Configuration configuration)
            throws Exception {

        Validate.notNull("configuration", configuration);

        LOGGER.info("tengi Server [version: %s, build-date: %s] is starting", //
                VersionUtil.VERSION, VersionUtil.BUILD_DATE);

        this.configuration = configuration;
        this.bossGroup = createEventLoopGroup(5, "boss");
        this.workerGroup = createEventLoopGroup(5, "worker");
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

                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();

                connectionManager.stop();
                eventManager.stop();

                serverState.transit(ServerState.Stopped);
            }
            return ServerImpl.this;
        });
    }

    private void bindChannels()
            throws Throwable {

        Map<ServerTransportLayer, int[]> transportLayers = collectTransportLayers(configuration);
        for (Map.Entry<ServerTransportLayer, int[]> entry : transportLayers.entrySet()) {
            ServerTransportLayer transportLayer = entry.getKey();
            int[] ports = entry.getValue();
            for (int port : ports) {
                channels.add(createChannel(transportLayer, port));
            }
        }
    }

    private EventLoopGroup createEventLoopGroup(int threadCount, String name) {
        LOGGER.debug("Creating event threadpool '%s' with %s threads", name, threadCount);
        return new NioEventLoopGroup(threadCount, new DefaultThreadFactory("channel-" + name + "-"));
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

    private Map<ServerTransportLayer, int[]> collectTransportLayers(Configuration configuration) {
        Set<Integer> tcpPorts = configuration.getTransports().stream() //
                                             .filter((transport) -> transport.getTransportLayer() == TransportLayers.TCP) //
                                             .map(configuration::getTransportPort).collect(Collectors.toSet());

        Set<Integer> udpPorts = configuration.getTransports().stream() //
                                             .filter((transport) -> transport.getTransportLayer() == TransportLayers.UDP) //
                                             .map(configuration::getTransportPort).collect(Collectors.toSet());

        boolean match = udpPorts.stream().anyMatch(tcpPorts::contains);

        if (match) {
            throw new IllegalTransportException("TCP and UDP ports can't match up");
        }

        Map<ServerTransportLayer, int[]> portMapping = new HashMap<>();
        portMapping.put(TransportLayers.TCP, tcpPorts.stream().mapToInt((v) -> v).toArray());
        portMapping.put(TransportLayers.UDP, udpPorts.stream().mapToInt((v) -> v).toArray());

        return portMapping;
    }

    private Channel createChannel(ServerTransportLayer transportLayer, int port)
            throws Throwable {

        ServerChannelFactory channelFactory = transportLayer.serverChannelFactory();
        return channelFactory.newServerChannel(transportLayer, port, bossGroup, workerGroup, connectionManager, serializer);
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
