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
package com.noctarius.tengi.server.transport.impl.websocket;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.server.server.ConnectionManager;
import com.noctarius.tengi.server.transport.ServerTransport;
import com.noctarius.tengi.server.transport.impl.ConnectionProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebsocketConnectionProcessor
        extends ConnectionProcessor<WebSocketFrame> {

    private final WebSocketServerHandshaker handshaker;

    public WebsocketConnectionProcessor(WebSocketServerHandshaker handshaker, //
                                        ConnectionManager connectionManager, Serializer serializer) {

        super(connectionManager, serializer, ServerTransport.WEBSOCKET_TRANSPORT);
        this.handshaker = handshaker;
    }

    @Override
    protected ReadableMemoryBuffer decode(ChannelHandlerContext ctx, WebSocketFrame frame)
            throws Exception {

        return null;
    }

    @Override
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return null;
    }
}
