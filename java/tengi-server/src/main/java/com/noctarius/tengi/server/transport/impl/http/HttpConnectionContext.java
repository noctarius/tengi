package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.utils.CompletableFutureUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.CompletableFuture;

public class HttpConnectionContext
        extends ConnectionContext {

    private final Channel channel;

    protected HttpConnectionContext(Channel channel, Identifier connectionId, Transport transport) {
        super(connectionId, transport);
        this.channel = channel;
    }

    @Override
    public <T> CompletableFuture<T> writeByteBuf(ByteBuf buffer, T object) {
        ChannelFuture future = channel.writeAndFlush(buffer);
        return CompletableFutureUtil.executeAsync(() -> {
            future.sync();
            return object;
        });
    }
}
