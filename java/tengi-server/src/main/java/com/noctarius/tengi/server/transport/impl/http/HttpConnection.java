package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.connection.AbstractConnection;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.server.transport.ServerTransport;

import java.util.concurrent.CompletableFuture;

class HttpConnection extends AbstractConnection {

    HttpConnection(Identifier connectionId) {
        super(connectionId, ServerTransport.HTTP_TRANSPORT);
    }

    @Override
    public <M extends Message> CompletableFuture<M> writeMessage(M message) {
        return null;
    }

    @Override
    public <M extends Marshallable> CompletableFuture<M> writeMarshallable(M marshallable) {
        return null;
    }

    @Override
    public <O> CompletableFuture<O> writeObject(O object) {
        return null;
    }

    @Override
    public CompletableFuture<Connection> close() {
        return null;
    }
}
