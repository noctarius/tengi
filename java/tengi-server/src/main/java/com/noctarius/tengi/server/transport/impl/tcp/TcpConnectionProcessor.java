package com.noctarius.tengi.server.transport.impl.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class TcpConnectionProcessor
        extends ByteToMessageCodec {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
            throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out)
            throws Exception {

    }
}
