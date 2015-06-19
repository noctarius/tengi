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
package com.noctarius.tengi.server;

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.core.exception.IllegalTransportException;
import com.noctarius.tengi.core.impl.FutureUtil;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.impl.VersionUtil;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.EventManager;
import com.noctarius.tengi.server.impl.transport.negotiation.TcpBinaryNegotiator;
import com.noctarius.tengi.server.impl.transport.negotiation.UdpBinaryNegotiator;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

class ServerImpl
        implements Server {

    private static final Logger LOGGER = LoggerManager.getLogger(ServerImpl.class);

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
            bindChannels();

            connectionManager.registerConnectedListener(connectedListener);
            connectionManager.start();
            eventManager.start();

            return ServerImpl.this;
        });
    }

    @Override
    public CompletableFuture<Server> stop() {
        return FutureUtil.executeAsync(() -> {
            for (Channel channel : channels) {
                channel.close().sync();
            }

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

            connectionManager.stop();
            eventManager.stop();

            return ServerImpl.this;
        });
    }

    private void bindChannels()
            throws Throwable {

        EnumMap<TransportLayer, int[]> transportLayers = collectTransportLayers(configuration);
        for (Map.Entry<TransportLayer, int[]> entry : transportLayers.entrySet()) {
            TransportLayer transportLayer = entry.getKey();
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

    private EnumMap<TransportLayer, int[]> collectTransportLayers(Configuration configuration) {
        Set<Integer> tcpPorts = configuration.getTransports().stream() //
                .filter((transport) -> transport.getTransportLayer() == TransportLayer.TCP) //
                .map(configuration::getTransportPort).collect(Collectors.toSet());

        Set<Integer> udpPorts = configuration.getTransports().stream() //
                .filter((transport) -> transport.getTransportLayer() == TransportLayer.UDP) //
                .map(configuration::getTransportPort).collect(Collectors.toSet());

        boolean match = udpPorts.stream().anyMatch(tcpPorts::contains);

        if (match) {
            throw new IllegalTransportException("TCP and UDP ports can't match up");
        }

        EnumMap<TransportLayer, int[]> portMapping = new EnumMap<>(TransportLayer.class);
        portMapping.put(TransportLayer.TCP, tcpPorts.stream().mapToInt((v) -> v).toArray());
        portMapping.put(TransportLayer.UDP, udpPorts.stream().mapToInt((v) -> v).toArray());

        return portMapping;
    }

    private Channel createChannel(TransportLayer transportLayer, int port)
            throws Throwable {

        switch (transportLayer) {
            case TCP:
                return createTcpChannel(port);

            case UDP:
                return createUdpChannel(port);

            default:
                throw new IllegalTransportException("Transport not yet supported");
        }
    }

    private Channel createUdpChannel(int port)
            throws Throwable {

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_BROADCAST, false).group(workerGroup) //
                .handler(new UdpProtocolNegotiator(connectionManager, serializer, port));

        ChannelFuture future = bootstrap.bind(port).sync();
        if (future.cause() != null) {
            throw future.cause();
        }
        return future.channel();
    }

    private Channel createTcpChannel(int port)
            throws Throwable {

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024).group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                 .childHandler(new TcpProtocolNegotiator(connectionManager, serializer, port));

        ChannelFuture future = bootstrap.bind(port).sync();
        if (future.cause() != null) {
            throw future.cause();
        }
        return future.channel();
    }

    private Serializer createSerializer(Configuration configuration) {
        return Serializer.create(configuration.getMarshallers());
    }

    private static class TcpProtocolNegotiator
            extends ChannelInitializer<SocketChannel> {

        private final ConnectionManager connectionManager;
        private final Serializer serializer;
        private final int port;

        private TcpProtocolNegotiator(ConnectionManager connectionManager, Serializer serializer, int port) {
            this.connectionManager = connectionManager;
            this.serializer = serializer;
            this.port = port;
        }

        @Override
        protected void initChannel(SocketChannel channel)
                throws Exception {

            channel.pipeline().addLast(new TcpBinaryNegotiator(port, true, true, connectionManager, serializer));
        }
    }

    private static class UdpProtocolNegotiator
            extends ChannelInitializer<SocketChannel> {

        private final ConnectionManager connectionManager;
        private final Serializer serializer;
        private final int port;

        private UdpProtocolNegotiator(ConnectionManager connectionManager, Serializer serializer, int port) {
            this.connectionManager = connectionManager;
            this.serializer = serializer;
            this.port = port;
        }

        @Override
        protected void initChannel(SocketChannel channel)
                throws Exception {

            channel.pipeline().addLast(new UdpBinaryNegotiator(port, connectionManager, serializer));
        }
    }

}
