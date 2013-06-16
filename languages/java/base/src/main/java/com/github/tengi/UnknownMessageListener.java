package com.github.tengi;

import io.netty.channel.ChannelHandlerContext;

public interface UnknownMessageListener
{

    void unknownMessageReceived( ChannelHandlerContext ctx, Object message );

}
