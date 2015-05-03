package com.noctarius.tengi.server.transport.impl.tcp;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.server.server.ConnectionManager;
import com.noctarius.tengi.server.transport.ServerTransport;
import com.noctarius.tengi.server.transport.impl.ConnectionProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class TcpConnectionProcessor
        extends ConnectionProcessor<ByteBuf> {

    public TcpConnectionProcessor(ConnectionManager connectionManager, Serializer serializer) {
        super(connectionManager, serializer, ServerTransport.TCP_TRANSPORT);
    }

    @Override
    protected ReadableMemoryBuffer decode(ChannelHandlerContext ctx, ByteBuf buffer)
            throws Exception {

        return MemoryBufferFactory.unpooled(buffer);
    }

    @Override
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new TcpConnectionContext(ctx.channel(), connectionId, getTransport());
    }

}
