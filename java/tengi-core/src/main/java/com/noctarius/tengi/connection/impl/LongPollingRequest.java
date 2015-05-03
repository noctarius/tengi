package com.noctarius.tengi.connection.impl;

import com.noctarius.tengi.Identifier;

public final class LongPollingRequest  {

    private final Identifier connectionId;

    public LongPollingRequest(Identifier connectionId) {
        this.connectionId = connectionId;
    }

    public Identifier getConnectionId() {
        return connectionId;
    }
}
