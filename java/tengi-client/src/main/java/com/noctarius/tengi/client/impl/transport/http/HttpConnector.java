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
package com.noctarius.tengi.client.impl.transport.http;

import com.noctarius.tengi.client.impl.ClientUtil;
import com.noctarius.tengi.client.impl.ServerConnection;
import com.noctarius.tengi.client.impl.transport.AbstractClientConnector;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.core.connection.handshake.HandshakeHandler;
import com.noctarius.tengi.core.exception.ConnectionDestroyedException;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.impl.TransportConstants;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.connection.packets.PollingRequest;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HttpConnector
        extends AbstractClientConnector<HttpRequest> {

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private final AtomicReference<Channel> downstream = new AtomicReference<>(null);

    private final Bootstrap bootstrap;

    private final InetAddress address;
    private final int port;
    private final Serializer serializer;
    private final Protocol protocol;
    private final EventLoopGroup clientGroup;
    private final HandshakeHandler handshakeHandler;

    private volatile ByteBufAllocator allocator;

    public HttpConnector(InetAddress address, int port, Serializer serializer, HandshakeHandler handshakeHandler,
                         EventLoopGroup clientGroup) {

        this.address = address;
        this.port = port;
        this.serializer = serializer;
        this.protocol = serializer.getProtocol();
        this.handshakeHandler = handshakeHandler;
        this.clientGroup = clientGroup;
        this.bootstrap = createBootstrap();
    }

    @Override
    public CompletableFuture<Connection> connect() {
        CompletableFuture<Connection> connectorFuture = new CompletableFuture<>();

        Bootstrap bootstrap = createBootstrap();
        bootstrap.attr(ClientUtil.CONNECT_FUTURE, connectorFuture);

        ChannelFuture channelFuture = bootstrap.connect(address, port);
        channelFuture.addListener(connectionListener(connectorFuture, this::handshakeRequest));

        return connectorFuture.thenApply(this::activateLongPolling);
    }

    @Override
    public HandshakeHandler handshakeHandler() {
        return handshakeHandler;
    }

    @Override
    public ByteBufAllocator allocator() {
        return allocator;
    }

    @Override
    public void write(HttpRequest message)
            throws Exception {

        if (destroyed.get()) {
            throw new ConnectionDestroyedException("Connection already destroyed");
        }

        ChannelFuture channelFuture = bootstrap.connect(address, port);
        Channel channel = channelFuture.sync().channel();
        channel.writeAndFlush(message).sync();
        channel.close().sync();
    }

    @Override
    public void destroy()
            throws Exception {

        if (destroyed.compareAndSet(false, true)) {
            Channel channel = downstream.get();
            if (channel != null) {
                channel.close().sync();
            }
        }
    }

    @Override
    public String getName() {
        return TransportConstants.TRANSPORT_NAME_HTTP;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public int getDefaultPort() {
        return TransportConstants.DEFAULT_PORT_TCP;
    }

    @Override
    public TransportLayer getTransportLayer() {
        return TransportLayer.TCP;
    }

    private HttpConnectionProcessor buildProcessor(Serializer serializer) {
        return new HttpConnectionProcessor(serializer, HttpConnector.this);
    }

    private Connection activateLongPolling(Connection connection) {
        if (connection != null) {
            ServerConnection serverConnection = (ServerConnection) connection;
            Bootstrap bootstrap = this.bootstrap.attr(ClientUtil.CONNECTION, serverConnection);
            ChannelFuture channelFuture = bootstrap.connect(address, port);
            channelFuture.addListener(fireLongPollingRequest(serverConnection));
        }
        return connection;
    }

    private ChannelFutureListener fireLongPollingRequest(ServerConnection connection) {
        return (channelFuture) -> {
            Channel channel = channelFuture.channel();
            downstream.set(channel);

            channel.closeFuture().addListener(reconnectLongPolling(connection));

            PollingRequest pollingRequest = new PollingRequest();
            ByteBuf buffer = channel.alloc().directBuffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                encoder.writeBoolean("loggedIn", true);
                encoder.writeObject("connectionId", connection.getConnectionId());
                encoder.writeObject("pollingRequest", Message.create(pollingRequest));
            }
            channel.writeAndFlush(buildHttpRequest(buffer, protocol.getMimeType()));
        };
    }

    private ChannelFutureListener reconnectLongPolling(ServerConnection connection) {
        return (cf) -> {
            if (destroyed.get()) {
                return;
            }

            ChannelFuture channelFuture = bootstrap.connect(address, port);
            channelFuture.addListener(fireLongPollingRequest(connection));
        };
    }

    private void handshakeRequest(Channel channel)
            throws Exception {

        this.allocator = channel.alloc();

        // Send Handshake
        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", false);
            encoder.writeObject("handshake", new Handshake());
        }
        channel.writeAndFlush(buildHttpRequest(buffer, protocol.getMimeType()));
    }

    private Bootstrap createBootstrap() {
        return new Bootstrap().channel(NioSocketChannel.class) //
                .group(clientGroup).option(ChannelOption.TCP_NODELAY, true) //
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel)
                            throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("codec", new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(1048576));
                        pipeline.addLast(buildProcessor(serializer));
                    }
                });
    }

    static HttpRequest buildHttpRequest(ByteBuf buffer, String mimeType) {
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/channel", buffer);
        HttpHeaders headers = request.headers();
        headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        headers.set(HttpHeaderNames.CONTENT_TYPE, mimeType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, buffer.writerIndex());
        headers.set(HttpHeaderNames.USER_AGENT, TransportConstants.TRANSPORT_NAME_HTTP);
        return request;
    }

}
