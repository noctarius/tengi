package com.noctarius.tengi.server.transport.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.server.server.ConnectionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class ConnectionProcessor<T>
        extends SimpleChannelInboundHandler<T> {

    private final ConnectionManager connectionManager;
    private final Serializer serializer;
    private final Transport transport;

    public ConnectionProcessor(ConnectionManager connectionManager, Serializer serializer, Transport transport) {
        this.connectionManager = connectionManager;
        this.serializer = serializer;
        this.transport = transport;
    }

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, T msg)
            throws Exception {

        ReadableMemoryBuffer memoryBuffer = decode(ctx, msg);
        if (memoryBuffer == null) {
            ctx.close().sync();
            return;
        }

        boolean loggedIn = memoryBuffer.readBoolean();

        Identifier connectionId;
        if (!loggedIn) {
            connectionId = Identifier.randomIdentifier();
            ConnectionContext connectionContext = createConnectionContext(ctx, connectionId);
            connectionManager.assignConnection(connectionId, connectionContext, transport);
        } else {
            connectionId = memoryBuffer.readIdentifier();
        }

        Message message = memoryBuffer.readObject(serializer.getProtocol());
        connectionManager.publishMessage(connectionId, message);
    }

    protected Serializer getSerializer() {
        return serializer;
    }

    protected Transport getTransport() {
        return transport;
    }

    protected ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    protected abstract ReadableMemoryBuffer decode(ChannelHandlerContext ctx, T msg)
            throws Exception;

    protected abstract ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId);

}
