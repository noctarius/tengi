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
package com.noctarius.tengi.server.transport.server.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.connection.impl.HandshakeRequest;
import com.noctarius.tengi.connection.impl.HandshakeResponse;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.server.server.ConnectionManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
    protected final void channelRead0(ChannelHandlerContext ctx, T msg)
            throws Exception {

        try (AutoClosableDecoder decoder = decode(ctx, msg)) {
            if (decoder == null) {
                ctx.close();
                return;
            }

            boolean loggedIn = decoder.readBoolean();

            if (!loggedIn) {
                Object request = decoder.readObject();
                if (!(request instanceof HandshakeRequest)) {
                    ctx.close();
                    return;
                }
                Identifier connectionId = Identifier.randomIdentifier();
                ConnectionContext connectionContext = createConnectionContext(ctx, connectionId);
                Connection connection = connectionManager.assignConnection(connectionId, connectionContext, transport);
                connectionContext.writeSocket(ctx.channel(), connection, createHandshakeResponse(ctx));
                return;

            }

            Identifier connectionId = decoder.readObject();
            Message message = decoder.readObject();
            connectionManager.publishMessage(ctx.channel(), connectionId, message);
        }
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

    private MemoryBuffer createHandshakeResponse(ChannelHandlerContext ctx)
            throws Exception {

        ByteBuf buffer = ctx.alloc().buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeObject("response", new HandshakeResponse());
        }
        return memoryBuffer;
    }

}
