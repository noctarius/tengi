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
package com.noctarius.tengi.client.impl.transport;

import com.noctarius.tengi.client.impl.ConnectCallback;
import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ServerConnection;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.noctarius.tengi.client.impl.ClientUtil.CONNECTION;
import static com.noctarius.tengi.client.impl.ClientUtil.CONNECT_FUTURE;
import static com.noctarius.tengi.client.impl.ClientUtil.connectionAttribute;

public abstract class ClientConnectionProcessor<T, C, M>
        extends SimpleChannelInboundHandler<T> {

    private static final Logger LOGGER = LoggerManager.getLogger(ClientConnectionProcessor.class);

    private final Serializer serializer;
    private final Connector<M> connector;

    protected ClientConnectionProcessor(Serializer serializer, Connector<M> connector) {
        this.serializer = serializer;
        this.connector = connector;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        ctx.channel().close();
        ServerConnection connection = connectionAttribute(ctx, CONNECTION);
        connection.exceptionally(cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, T msg)
            throws Exception {

        try (AutoClosableDecoder decoder = decode(ctx, msg)) {
            boolean loggedIn = decoder.readBoolean();

            if (!loggedIn) {
                LOGGER.info("Client seems not to be logged in");
                ctx.close();
            }

            Identifier connectionId = decoder.readObject();
            Object object = decoder.readObject();

            if (object instanceof Handshake) {
                HandshakeHandler handshakeHandler = connector.handshakeHandler();
                handshakeHandler.handleHandshake(connectionId, (Handshake) object);

                ConnectCallback connectCallback = connectionAttribute(ctx, CONNECT_FUTURE, true);
                ConnectionContext<C> connectionContext = createConnectionContext(ctx, connectionId);
                Connection connection = createConnection(ctx, connectionContext, connectionId);
                connectCallback.on(connection);

            } else {
                ServerConnection connection = connectionAttribute(ctx, CONNECTION);
                connection.publishMessage((Message) object);
            }

            // Some transports might need to handle the request (like HTTP Long-Pollings)
            handleMessage(ctx, msg, object);
        }
    }

    protected Serializer getSerializer() {
        return serializer;
    }

    protected Connector<M> getConnector() {
        return connector;
    }

    protected void handleMessage(ChannelHandlerContext ctx, T object, Object message) {
    }

    protected abstract AutoClosableDecoder decode(ChannelHandlerContext ctx, T msg)
            throws Exception;

    protected abstract ConnectionContext<C> createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId);

    protected abstract Connection createConnection(ChannelHandlerContext ctx, ConnectionContext<C> connectionContext,
                                                   Identifier connectionId);

}
