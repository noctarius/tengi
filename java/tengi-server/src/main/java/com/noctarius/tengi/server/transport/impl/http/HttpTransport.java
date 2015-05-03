package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.Transport;

public class HttpTransport
        implements Transport {

    private static final String TRANSPORT_NAME = "tengi::transport::http/1.1";

    @Override
    public String getName() {
        return TRANSPORT_NAME;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

}
