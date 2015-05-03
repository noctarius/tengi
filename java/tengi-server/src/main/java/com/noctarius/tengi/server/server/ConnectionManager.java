package com.noctarius.tengi.server.server;

import com.noctarius.tengi.listener.ConnectionConnectedListener;
import io.netty.handler.ssl.SslContext;

import java.util.HashSet;
import java.util.Set;

public class ConnectionManager
        implements Service {

    private final Set<ConnectionConnectedListener> connectedListeners = new HashSet<>();

    private final SslContext sslContext;

    public ConnectionManager(SslContext sslContext) {
        this.sslContext = sslContext;
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

}
