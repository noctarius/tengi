package com.noctarius.tengi.server.server;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.SystemException;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.connection.impl.LongPollingRequest;
import com.noctarius.tengi.listener.ConnectionConnectedListener;
import com.noctarius.tengi.serialization.Serializer;
import io.netty.handler.ssl.SslContext;
import io.netty.util.internal.ConcurrentSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager
        implements Service {

    private final Set<ConnectionConnectedListener> connectedListeners = new ConcurrentSet<>();
    private final Map<Identifier, ClientConnection> connections = new ConcurrentHashMap<>();

    private final SslContext sslContext;
    private final Serializer serializer;

    public ConnectionManager(SslContext sslContext, Serializer serializer) {
        this.sslContext = sslContext;
        this.serializer = serializer;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    public SslContext getSslContext() {
        return sslContext;
    }

    public void registerConnectedListener(ConnectionConnectedListener connectedListener) {
        connectedListeners.add(connectedListener);
    }

    public Connection assignConnection(Identifier connectionId, ConnectionContext connectionContext, Transport transport) {
        Connection connection = connections.computeIfAbsent(connectionId,
                (key) -> new ClientConnection(connectionContext, connectionId, transport, serializer));

        connectedListeners.forEach((listener) -> listener.onConnectionAccept(connection));
        return connection;
    }

    public void publishMessage(Identifier connectionId, Message message) {
        ClientConnection connection = connections.get(connectionId);
        if (connection == null) {
            throw new SystemException("ConnectionId '" + connectionId.toString() + "' is not registered");
        }

        if (!connection.getTransport().isStreaming() && message.getBody() instanceof LongPollingRequest) {
            LongPollingRequest request = message.getBody();
            connection.getConnectionContext().processLongPollingRequest(request);
        }

        connection.publishMessage(message);
    }

}
