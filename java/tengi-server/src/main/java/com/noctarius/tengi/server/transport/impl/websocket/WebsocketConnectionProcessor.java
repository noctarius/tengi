package com.noctarius.tengi.server.transport.impl.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebsocketConnectionProcessor
        extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final WebSocketServerHandshaker handshaker;

    public WebsocketConnectionProcessor(WebSocketServerHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg)
            throws Exception {

    }
}
