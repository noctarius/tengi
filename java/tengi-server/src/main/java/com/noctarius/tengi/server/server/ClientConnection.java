package com.noctarius.tengi.server.server;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.connection.AbstractConnection;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;

public class ClientConnection
        extends AbstractConnection {

    ClientConnection(ConnectionContext connectionContext, Identifier connectionId, //
                     Transport transport, Serializer serializer) {

        super(connectionContext, connectionId, transport, serializer);
    }

    void publishMessage(Message message) {
        getMessageListeners().forEach((listener) -> listener.onMessage(this, message));
    }

    public ConnectionContext getConnectionContext() {
        return super.getConnectionContext();
    }

}
