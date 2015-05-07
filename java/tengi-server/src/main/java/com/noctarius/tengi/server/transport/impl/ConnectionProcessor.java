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
package com.noctarius.tengi.server.transport.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.server.server.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class ConnectionProcessor<T>
        extends SimpleChannelInboundHandler<T> {

    private final ConnectionManager connectionManager;
    private final Serializer serializer;
    private final Transport transport;

    public ConnectionProcessor(ConnectionManager connectionManager, Serializer serializer, Transport transport) {
        this.connectionManager = connectionManager;
        this.serializer = serializer;
        this.transport = transport;
    }

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, T msg)
            throws Exception {

        try (AutoClosableDecoder decoder = decode(ctx, msg)) {
            if (decoder == null) {
                ctx.close().sync();
                return;
            }

            boolean loggedIn = decoder.readBoolean();

            Identifier connectionId;
            if (!loggedIn) {
                connectionId = Identifier.randomIdentifier();
                ConnectionContext connectionContext = createConnectionContext(ctx, connectionId);
                connectionManager.assignConnection(connectionId, connectionContext, transport);
            } else {
                byte[] data = new byte[16];
                decoder.readBytes(data);
                connectionId = Identifier.fromBytes(data);
            }

            Message message = decoder.readObject();
            connectionManager.publishMessage(connectionId, message);
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

}
