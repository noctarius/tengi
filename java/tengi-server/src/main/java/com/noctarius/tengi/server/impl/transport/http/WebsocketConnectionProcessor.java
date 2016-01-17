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
package com.noctarius.tengi.server.impl.transport.http;

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.server.ServerTransports;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.ServerConnectionProcessor;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebsocketConnectionProcessor
        extends ServerConnectionProcessor<WebSocketFrame> {

    private final WebSocketServerHandshaker handshaker;

    public WebsocketConnectionProcessor(WebSocketServerHandshaker handshaker, //
                                        ConnectionManager connectionManager, Serializer serializer) {

        super(connectionManager, serializer, ServerTransports.WEBSOCKET_TRANSPORT);
        this.handshaker = handshaker;
    }

    @Override
    protected AutoClosableDecoder decode(ChannelHandlerContext ctx, WebSocketFrame frame)
            throws Exception {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return null;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
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
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new WebsocketConnectionContext(ctx.channel(), connectionId, getSerializer(), getTransport());
    }

}
