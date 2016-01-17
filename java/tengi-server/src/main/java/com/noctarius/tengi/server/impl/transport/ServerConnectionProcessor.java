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
package com.noctarius.tengi.server.impl.transport;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.noctarius.tengi.server.impl.ServerUtil.CONNECTION_ID;
import static com.noctarius.tengi.server.impl.ServerUtil.connectionAttribute;

public abstract class ServerConnectionProcessor<T>
        extends SimpleChannelInboundHandler<T> {

    private final ConnectionManager connectionManager;
    private final Serializer serializer;
    private final Transport transport;

    protected ServerConnectionProcessor(ConnectionManager connectionManager, Serializer serializer, Transport transport) {
        this.connectionManager = connectionManager;
        this.serializer = serializer;
        this.transport = transport;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        ctx.channel().close();
        Identifier connectionId = connectionAttribute(ctx, CONNECTION_ID);
        getConnectionManager().exceptionally(connectionId, cause);
    }

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, T msg)
            throws Exception {

        try (AutoClosableDecoder decoder = decode(ctx, msg)) {
            if (decoder == null) {
                ctx.close();
                return;
            }

            boolean loggedIn = decoder.readBoolean();

            if (!loggedIn) {
                handleHandshakeRequest(ctx, decoder);
                return;
            }

            Identifier connectionId = decoder.readObject();
            connectionAttribute(ctx, CONNECTION_ID, connectionId);
            Message message = decoder.readObject();
            connectionManager.publishMessage(ctx.channel(), connectionId, message);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {

        ctx.flush();
    }

    protected Serializer getSerializer() {
        return serializer;
    }

    protected Transport getTransport() {
        return transport;
    }

    protected ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    protected abstract AutoClosableDecoder decode(ChannelHandlerContext ctx, T msg)
            throws Exception;

    protected abstract ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId);

    private void handleHandshakeRequest(ChannelHandlerContext ctx, AutoClosableDecoder decoder)
            throws Exception {

        Object request = decoder.readObject();
        if (!(request instanceof Handshake)) {
            ctx.close();
            return;
        }

        Identifier connectionId = Identifier.randomIdentifier();
        HandshakeHandler handshakeHandler = connectionManager.getHandshakeHandler();
        Handshake handshakeResponse = handshakeHandler.handleHandshake(connectionId, (Handshake) request);
        if (handshakeResponse == null) {
            ctx.close();
            return;
        }
        if (handshakeResponse == request) {
            ctx.close();
            throw new IllegalStateException("Handshake could not be accepted, illegal verification");
        }

        connectionAttribute(ctx, CONNECTION_ID, connectionId);
        ConnectionContext connectionContext = createConnectionContext(ctx, connectionId);
        Connection connection = connectionManager.assignConnection(connectionId, connectionContext, transport);
        connectionContext.writeSocket(ctx.channel(), connection, createHandshakeResponse(ctx, handshakeResponse));
    }

    private MemoryBuffer createHandshakeResponse(ChannelHandlerContext ctx, Handshake handshakeResponse)
            throws Exception {

        ByteBuf buffer = ctx.alloc().buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeObject("response", handshakeResponse);
        }
        return memoryBuffer;
    }

}
