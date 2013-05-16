package com.github.tengi.transport.protocol.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebsocketRequestHandler
    extends MessageToMessageCodec<WebSocketFrame, ByteBuf>
{

    @Override
    protected void encode( ChannelHandlerContext ctx, ByteBuf msg, MessageBuf<Object> out )
        throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void decode( ChannelHandlerContext ctx, WebSocketFrame msg, MessageBuf<Object> out )
        throws Exception
    {
        // TODO Auto-generated method stub

    }

}
