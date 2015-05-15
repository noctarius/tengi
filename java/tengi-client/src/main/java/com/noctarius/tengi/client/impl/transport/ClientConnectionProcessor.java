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

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.MessagePublisher;
import com.noctarius.tengi.spi.connection.Connection;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.connection.handshake.HandshakeResponse;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.core.serialization.codec.AutoClosableDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.CompletableFuture;

public abstract class ClientConnectionProcessor<T, C>
        extends SimpleChannelInboundHandler<T> {

    private static final Logger LOGGER = LoggerManager.getLogger(ClientConnectionProcessor.class);

    private final Serializer serializer;
    private final MessagePublisher messagePublisher;
    private final CompletableFuture<Connection> connectorFuture;
    private final Connector connector;

    protected ClientConnectionProcessor(Serializer serializer, MessagePublisher messagePublisher,
                                        CompletableFuture<Connection> connectorFuture, Connector connector) {

        this.serializer = serializer;
        this.messagePublisher = messagePublisher;
        this.connectorFuture = connectorFuture;
        this.connector = connector;
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

            if (object instanceof HandshakeResponse) {
                ConnectionContext<C> connectionContext = createConnectionContext(ctx, connectionId, connector);
                Connection connection = createConnection(connectionContext, connectionId, connector, serializer);
                connectorFuture.complete(connection);

            } else {
                messagePublisher.publishMessage(ctx.channel(), connectionId, (Message) object);
            }
        }
    }

    protected Serializer getSerializer() {
        return serializer;
    }

    protected abstract AutoClosableDecoder decode(ChannelHandlerContext ctx, T msg)
            throws Exception;

    protected abstract ConnectionContext<C> createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId,
                                                                    Connector connector);

    protected abstract Connection createConnection(ConnectionContext<C> connectionContext, Identifier connectionId,
                                                   Connector connector, Serializer serializer);

}
