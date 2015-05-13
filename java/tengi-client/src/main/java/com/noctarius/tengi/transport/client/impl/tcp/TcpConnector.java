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
package com.noctarius.tengi.transport.client.impl.tcp;

import com.noctarius.tengi.TransportLayer;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.client.Connector;
import com.noctarius.tengi.client.MessagePublisher;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.TransportConstants;
import com.noctarius.tengi.connection.impl.HandshakeRequest;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpConnector
        implements Connector {

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private volatile Channel channel;

    @Override
    public String getName() {
        return TransportConstants.TRANSPORT_NAME_TCP;
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
        return TransportLayer.TCP;
    }

    @Override
    public CompletableFuture<Connection> connect(InetAddress address, int port, MessagePublisher messagePublisher,
                                                 Serializer serializer, EventLoopGroup clientGroup) {

        CompletableFuture<Connection> connectorFuture = new CompletableFuture<>();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class).group(clientGroup).option(ChannelOption.TCP_NODELAY, true) //
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel)
                            throws Exception {

                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(buildProcessor(serializer, messagePublisher, connectorFuture));
                    }
                });

        ChannelFuture channelFuture = bootstrap.connect(address, port);
        channelFuture.addListener(connectionListener(connectorFuture, serializer));

        return connectorFuture;
    }

    @Override
    public Channel getUpstreamChannel() {
        return channel;
    }

    @Override
    public Channel getDownstreamChannel() {
        return channel;
    }

    @Override
    public Collection<Channel> getCommunicationChannels() {
        return Collections.singleton(channel);
    }

    @Override
    public void destroy()
            throws Exception {

        if (destroyed.compareAndSet(false, true)) {
            Channel channel = this.channel;
            if (channel != null) {
                channel.disconnect().sync();
            }
        }
    }

    private TcpConnectionProcessor buildProcessor(Serializer serializer, MessagePublisher messagePublisher,
                                                  CompletableFuture<Connection> connectorFuture) {

        return new TcpConnectionProcessor(serializer, messagePublisher, connectorFuture, TcpConnector.this);
    }

    private ChannelFutureListener connectionListener(CompletableFuture<Connection> future, Serializer serializer) {
        return (channelFuture) -> {
            if (!channelFuture.isSuccess()) {
                future.complete(null);

            } else {
                // Send Handshake
                Channel channel = (this.channel = channelFuture.channel());

                ByteBuf buffer = Unpooled.buffer();
                MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
                try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                    encoder.writeBytes("magic", DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER);
                    encoder.writeBoolean("loggedIn", false);
                    encoder.writeObject("handshake", new HandshakeRequest());
                }

                channel.writeAndFlush(buffer);
            }
        };
    }

}
