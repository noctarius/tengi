package com.noctarius.tengi.connection;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Transport;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.CompletableFuture;

public abstract class ConnectionContext {

    private final Identifier connectionId;
    private final Transport transport;

    protected ConnectionContext(Identifier connectionId, Transport transport) {
        this.connectionId = connectionId;
        this.transport = transport;
    }

    public Identifier getConnectionId() {
        return connectionId;
    }

    public Transport getTransport() {
        return transport;
    }

    public abstract <T> CompletableFuture<T> writeByteBuf(ByteBuf buffer, T object);

}
