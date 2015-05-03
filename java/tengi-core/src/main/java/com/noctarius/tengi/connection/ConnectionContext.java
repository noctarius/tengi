package com.noctarius.tengi.connection;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.connection.impl.LongPollingRequest;

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

    public void processLongPollingRequest(LongPollingRequest request) {
    }

    public abstract CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message object)
            throws Exception;

    public abstract CompletableFuture<Connection> close(Connection connection);
}
