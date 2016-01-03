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
package com.noctarius.tengi.server.impl.transport.negotiation;

import com.noctarius.tengi.core.exception.ConnectionFailedException;
import com.noctarius.tengi.server.ServerTransports;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.http.HttpConnectionProcessor;
import com.noctarius.tengi.server.impl.transport.websocket.WebsocketConnectionProcessor;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;

import static com.noctarius.tengi.spi.connection.impl.TransportConstants.WEBSOCKET_RELATIVE_PATH;

public class WebsocketNegotiator
        extends ChannelInboundHandlerAdapter {

    private final int port;
    private final ConnectionManager connectionManager;
    private final Serializer serializer;

    public WebsocketNegotiator(int port, ConnectionManager connectionManager, Serializer serializer) {
        this.port = port;
        this.connectionManager = connectionManager;
        this.serializer = serializer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object)
            throws Exception {

        if (object instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) object;

            // Activate websocket handshake
            if (WEBSOCKET_RELATIVE_PATH.equals(request.uri())) {
                if (!connectionManager.acceptTransport(ServerTransports.WEBSOCKET_TRANSPORT, port)) {
                    ctx.close();
                    throw new ConnectionFailedException("Transport not enabled");
                }
                switchToWebsocket(ctx, request);
            } else {
                if (!connectionManager.acceptTransport(ServerTransports.HTTP_TRANSPORT, port)) {
                    ctx.close();
                    throw new ConnectionFailedException("Transport not enabled");
                }
                switchToHttpLongPolling(ctx);
            }
            ctx.fireChannelRead(request);
        } else {
            ReferenceCountUtil.release(object);
        }
    }

    private void switchToHttpLongPolling(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("http-connection-processor", new HttpConnectionProcessor(connectionManager, serializer));
        pipeline.remove(this);
    }

    private void switchToWebsocket(ChannelHandlerContext ctx, FullHttpRequest request) {
        String location = getWebSocketLocation(ctx, request);
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(location, null, true);
        WebSocketServerHandshaker handshaker = factory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
            ChannelPipeline pipeline = ctx.pipeline();
            pipeline.addLast("websocket-connection-processor",
                    new WebsocketConnectionProcessor(handshaker, connectionManager, serializer));
            pipeline.remove(this);
        }
    }

    private String getWebSocketLocation(ChannelHandlerContext ctx, FullHttpRequest request) {
        SslHandler handler = ctx.pipeline().get(SslHandler.class);
        String location = request.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_RELATIVE_PATH;
        if (handler != null) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

}
