/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.tengi.server;

import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.server.impl.transport.http.HttpTransport;
import com.noctarius.tengi.server.impl.transport.http2.Http2Transport;
import com.noctarius.tengi.server.impl.transport.tcp.TcpTransport;
import com.noctarius.tengi.server.impl.transport.udt.UdtTransport;
import com.noctarius.tengi.server.impl.transport.websocket.WebsocketTransport;
import com.noctarius.tengi.server.spi.negotiation.NegotiatableTransport;
import com.noctarius.tengi.server.spi.negotiation.Negotiator;

/**
 * <p>The <tt>ServerTransport</tt> enum defines built-in, server-side
 * {@link com.noctarius.tengi.core.connection.Transport} implementations, however other
 * implementations are still possible to be chosen.</p>
 */
public enum ServerTransports
        implements NegotiatableTransport {

    /**
     * <p>This constant defines a HTTP based {@link com.noctarius.tengi.core.connection.Transport}
     * implementation. Due to the way HTTP/1.1 works, this transport uses HTTP Long-Polling, with
     * two connections (one upstream, one downstream), which is not streaming but tries to emulate
     * it as best as possible.</p>
     * <p>This transport should be used as a fallback or last-resort transport because of the huge
     * overhead the HTTP protocol implies.</p>
     */
    HTTP_TRANSPORT(new HttpTransport()),

    /**
     * Reserved for later implementation
     */
    HTTP2_TRANSPORT(new Http2Transport()),

    /**
     * This constant defines a Websocket based {@link com.noctarius.tengi.core.connection.Transport}
     * implementation. Using an optimized internal protocol, this transport is one of the suggested standard
     * transports to be chosen to connect first.
     */
    WEBSOCKET_TRANSPORT(new WebsocketTransport()),

    /**
     * This constant defines a UDT based {@link com.noctarius.tengi.core.connection.Transport}
     * implementation. UDT is a reliable UDP implementation which simulates a full duplex socket
     * channel with guaranteed delivery (and redelivery) and is insensitive to unreliable networks
     * like WIFI, mobile networks and similar installations.
     */
    UDT_TRANSPORT(new UdtTransport()),

    /**
     * Reserved for later implementation
     */
    RDP_TRANSPORT(null),

    /**
     * This constant defines a TCP plain socket based {@link com.noctarius.tengi.core.connection.Transport}
     * implementation. Using an optimized internal protocol, this transport is one of the suggested standard
     * transports to be chosen to connect first.
     */
    TCP_TRANSPORT(new TcpTransport()),

    /**
     * Reserved for later implementation
     */
    UDP_TRANSPORT(null);

    private final Transport transport;

    private ServerTransports(Transport transport) {
        this.transport = transport;
    }

    @Override
    public String getName() {
        return transport.getName();
    }

    @Override
    public boolean isStreaming() {
        return transport.isStreaming();
    }

    @Override
    public int getDefaultPort() {
        return transport.getDefaultPort();
    }

    @Override
    public TransportLayer getTransportLayer() {
        return transport.getTransportLayer();
    }

    @Override
    public Negotiator getNegotiator() {
        return transport instanceof NegotiatableTransport ? ((NegotiatableTransport) transport).getNegotiator() : null;
    }
}
