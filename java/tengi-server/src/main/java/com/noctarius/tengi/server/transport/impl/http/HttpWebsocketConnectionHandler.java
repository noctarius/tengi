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
package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.server.server.ConnectionManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

public class HttpWebsocketConnectionHandler
        extends SimpleChannelInboundHandler<Object> {

    private static final String WEBSOCKET_PATH = "/wss";

    private final ConnectionManager connectionManager;

    private volatile WebSocketServerHandshaker handshaker;

    public HttpWebsocketConnectionHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if (msg instanceof FullHttpRequest) {
            handleHttp(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebsocket(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        cause.printStackTrace();
        ctx.close();
    }

    private void handleHttp(ChannelHandlerContext ctx, FullHttpRequest request) {
        // Handle a bad request.
        if (!request.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        // Allow only GET methods.
        if (request.method() != HttpMethod.GET) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        // Send the demo page and favicon.ico
        String webSocketLocation = getWebSocketLocation(ctx, request);
        if ("/".equals(request.uri())) {
            // Handle normal HTTP (Long-)Polling requests
            ByteBuf content = Unpooled.buffer();
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpHeaderUtil.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, request, res);
            return;
        }
        if ("/favicon.ico".equals(request.uri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            sendHttpResponse(ctx, request, res);
            return;
        }

        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(webSocketLocation, null, true);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), request);
        }
    }

    private void handleWebsocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        // Send the uppercase string back.
        String request = ((TextWebSocketFrame) frame).text();
        System.err.printf("%s received %s%n", ctx.channel(), request);
        ctx.channel().write(new TextWebSocketFrame(request.toUpperCase()));
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(res, res.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaderUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(ChannelHandlerContext ctx, FullHttpRequest request) {
        SslHandler handler = ctx.pipeline().get(SslHandler.class);
        String location = request.headers().get(HttpHeaderNames.HOST) + WEBSOCKET_PATH;
        if (handler != null) {
            return "wss://" + location;
        } else {
            return "ws://" + location;
        }
    }

}
