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

import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

class WebsocketHandshaker
        extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handshaker;
    private final Serializer serializer;
    private final Connector<WebSocketFrame> connector;

    WebsocketHandshaker(WebSocketClientHandshaker handshaker, Serializer serializer, Connector<WebSocketFrame> connector) {
        this.handshaker = handshaker;
        this.serializer = serializer;
        this.connector = connector;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        Channel channel = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(channel, (FullHttpResponse) msg);

            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.remove(this);
            pipeline.addLast(new WebsocketConnectionProcessor(handshaker, serializer, connector));

            handshakeRequest(channel);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {

        handshaker.handshake(ctx.channel());
    }

    private void handshakeRequest(Channel channel)
            throws Exception {

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", false);
            encoder.writeObject("handshake", new Handshake());
        }

        channel.writeAndFlush(new BinaryWebSocketFrame(buffer));
    }

}
