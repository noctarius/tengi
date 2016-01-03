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
package com.noctarius.tengi.client.impl.transport.websocket;

import com.noctarius.tengi.client.TransportLayers;
import com.noctarius.tengi.client.impl.ClientUtil;
import com.noctarius.tengi.client.impl.ConnectCallback;
import com.noctarius.tengi.client.impl.ServerConnection;
import com.noctarius.tengi.client.impl.transport.AbstractClientConnector;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.spi.connection.AbstractConnection;
import com.noctarius.tengi.spi.connection.impl.TransportConstants;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.InetAddress;
import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.noctarius.tengi.client.impl.ClientUtil.CONNECTION;
import static com.noctarius.tengi.client.impl.ClientUtil.CONNECT_FUTURE;
import static com.noctarius.tengi.client.impl.ClientUtil.connectionAttribute;

public class WebsocketConnector
        extends AbstractClientConnector<WebSocketFrame> {

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private final InetAddress address;
    private final int port;
    private final Serializer serializer;
    private final EventLoopGroup clientGroup;
    private final HandshakeHandler handshakeHandler;

    private volatile Channel channel;

    public WebsocketConnector(InetAddress address, int port, Serializer serializer, HandshakeHandler handshakeHandler,
                              EventLoopGroup clientGroup) {

        this.address = address;
        this.port = port;
        this.serializer = serializer;
        this.handshakeHandler = handshakeHandler;
        this.clientGroup = clientGroup;
    }

    @Override
    public void connect(ConnectCallback connectCallback) {
        WebSocketClientHandshaker handshaker = createHandshaker();
        Bootstrap bootstrap = createBootstrap(handshaker);
        bootstrap.attr(ClientUtil.CONNECT_FUTURE, connectCallback);

        ChannelFuture channelFuture = bootstrap.connect(address, port);
        channelFuture.addListener(connectionListener(connectCallback, (c) -> channel = c, this::handleChannelClose));
    }

    @Override
    public HandshakeHandler handshakeHandler() {
        return handshakeHandler;
    }

    @Override
    public ByteBufAllocator allocator() {
        return channel.alloc();
    }

    @Override
    public void write(WebSocketFrame message)
            throws Exception {

        channel.writeAndFlush(message).sync();
    }

    @Override
    public void destroy(Connection connection)
            throws Exception {

        if (destroyed.compareAndSet(false, true)) {
            Channel channel = this.channel;
            if (channel != null) {
                channel.disconnect().sync();
            }
            if (connection instanceof AbstractConnection) {
                ((AbstractConnection) connection).notifyClose();
            }
        }
    }

    @Override
    public String getName() {
        return TransportConstants.TRANSPORT_NAME_WEBSOCKET;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public int getDefaultPort() {
        return TransportConstants.DEFAULT_PORT_TCP;
    }

    @Override
    public TransportLayer getTransportLayer() {
        return TransportLayers.TCP;
    }

    private WebSocketClientHandshaker createHandshaker() {
        return WebSocketClientHandshakerFactory
                .newHandshaker(createURI(), WebSocketVersion.V13, null, false, new DefaultHttpHeaders());
    }

    private URI createURI() {
        String url = "ws://" + address.getHostAddress() + ":" + port + TransportConstants.WEBSOCKET_RELATIVE_PATH;
        return URI.create(url);
    }

    private void handleChannelClose(ChannelFuture channelFuture) {
        Channel channel = channelFuture.channel();
        ConnectCallback connectCallback = connectionAttribute(channel, CONNECT_FUTURE);
        if (connectCallback != null) {
            connectCallback.on(null, null);
        } else {
            ServerConnection connection = connectionAttribute(channel, CONNECTION);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private Bootstrap createBootstrap(WebSocketClientHandshaker handshaker) {
        return new Bootstrap().channel(NioSocketChannel.class) //
                .group(clientGroup).option(ChannelOption.TCP_NODELAY, true) //
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel)
                            throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        pipeline.addLast(new WebsocketHandshaker(handshaker, serializer, WebsocketConnector.this));
                    }
                });
    }

}
