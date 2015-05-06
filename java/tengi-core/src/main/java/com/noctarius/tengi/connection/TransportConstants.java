package com.noctarius.tengi.connection;

public final class TransportConstants {

    private TransportConstants() {
    }

    public static final String TRANSPORT_NAME_TCP = "tengi::transport::tcp";
    public static final String TRANSPORT_NAME_HTTP = "tengi::transport::http/1.1";

    public static final int DEFAULT_PORT_TCP = 8080;
    public static final int DEFAULT_PORT_UDP = 9090;

}
