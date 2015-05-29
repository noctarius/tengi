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
package com.noctarius.tengi.client.impl.transport.websocket;

import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ServerConnection;
import com.noctarius.tengi.client.impl.transport.ClientConnectionProcessor;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import static com.noctarius.tengi.client.impl.ClientUtil.CONNECTION;
import static com.noctarius.tengi.client.impl.ClientUtil.connectionAttribute;

public class WebsocketConnectionProcessor
        extends ClientConnectionProcessor<WebSocketFrame, Channel, WebSocketFrame> {

    private final WebSocketClientHandshaker handshaker;
    private final Connector<WebSocketFrame> connector;

    WebsocketConnectionProcessor(WebSocketClientHandshaker handshaker, Serializer serializer,
                                 Connector<WebSocketFrame> connector) {

        super(serializer, connector);
        this.handshaker = handshaker;
        this.connector = connector;
    }

    @Override
    protected AutoClosableDecoder decode(ChannelHandlerContext ctx, WebSocketFrame frame)
            throws Exception {

        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            ctx.channel().close();
            return null;
        }
        if (frame instanceof PongWebSocketFrame) {
            // Action?
            return null;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            ByteBuf copy = binaryFrame.content().copy();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(copy);
            return getSerializer().retrieveDecoder(memoryBuffer);
        }
        return null;
    }

    @Override
    protected ConnectionContext<Channel> createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new WebsocketConnectionContext(connectionId, getSerializer(), connector);
    }

    @Override
    protected Connection createConnection(ChannelHandlerContext ctx, ConnectionContext<Channel> connectionContext,
                                          Identifier connectionId) {

        ServerConnection connection = new WebsocketServerConnection(connectionContext, connectionId, connector, getSerializer());
        connectionAttribute(ctx, CONNECTION, connection);
        return connection;
    }

}
