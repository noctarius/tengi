/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.client;

import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ConnectorFactory;
import com.noctarius.tengi.client.impl.transport.http.HttpConnector;
import com.noctarius.tengi.client.impl.transport.tcp.TcpConnector;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.channel.EventLoopGroup;

import java.net.InetAddress;

import static com.noctarius.tengi.spi.connection.impl.TransportConstants.DEFAULT_PORT_TCP;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.DEFAULT_PORT_UDP;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.TRANSPORT_NAME_HTTP;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.TRANSPORT_NAME_HTTP2;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.TRANSPORT_NAME_RDP;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.TRANSPORT_NAME_TCP;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.TRANSPORT_NAME_UDP;
import static com.noctarius.tengi.spi.connection.impl.TransportConstants.TRANSPORT_NAME_WEBSOCKET;

/**
 * <p>The <tt>ClientTransport</tt> enum defines built-in, client-side
 * {@link com.noctarius.tengi.core.connection.Transport} implementations, however other
 * implementations are still possible to be chosen.</p>
 * <p>Client-side <tt>Transport</tt> implementations must also implement
 * {@link com.noctarius.tengi.client.impl.ConnectorFactory} to create the actual
 * {@link com.noctarius.tengi.client.impl.Connector} instance which provides the
 * implementation on how to connect and retrieve data.</p>
 */
public enum ClientTransport
        implements Transport, ConnectorFactory {

    /**
     * <p>This constant defines a HTTP based {@link com.noctarius.tengi.core.connection.Transport}
     * implementation. Due to the way HTTP/1.1 works, this transport uses HTTP Long-Polling, with
     * two connections (one upstream, one downstream), which is not streaming but tries to emulate
     * it as best as possible.</p>
     * <p>This transport should be used as a fallback or last-resort transport because of the huge
     * overhead the HTTP protocol implies.</p>
     */
    HTTP_TRANSPORT(HttpConnector::new, TRANSPORT_NAME_HTTP, false, DEFAULT_PORT_TCP, TransportLayer.TCP),

    /**
     * Reserved for later implementation
     */
    HTTP2_TRANSPORT(null, TRANSPORT_NAME_HTTP2, true, DEFAULT_PORT_TCP, TransportLayer.TCP),

    /**
     * Reserved for later implementation
     */
    WEBSOCKET_TRANSPORT(null, TRANSPORT_NAME_WEBSOCKET, true, DEFAULT_PORT_TCP, TransportLayer.TCP),

    /**
     * Reserved for later implementation
     */
    RDP_TRANSPORT(null, TRANSPORT_NAME_RDP, true, DEFAULT_PORT_UDP, TransportLayer.UDP),

    /**
     * This constant defines a TCP plain socket based {@link com.noctarius.tengi.core.connection.Transport}
     * implementation. Using an optimized internal protocol, this transport is one of the suggested standard
     * transports to be chosen to connect first.
     */
    TCP_TRANSPORT(TcpConnector::new, TRANSPORT_NAME_TCP, true, DEFAULT_PORT_TCP, TransportLayer.TCP),

    /**
     * Reserved for later implementation
     */
    UDP_TRANSPORT(null, TRANSPORT_NAME_UDP, true, DEFAULT_PORT_UDP, TransportLayer.UDP);

    private final ConnectorFactory connectorFactory;
    private final String name;
    private final boolean streaming;
    private final int defaultPort;
    private final TransportLayer transportLayer;

    ClientTransport(ConnectorFactory connectorFactory, String name, boolean streaming, //
                    int defaultPort, TransportLayer transportLayer) {

        this.connectorFactory = connectorFactory;
        this.name = name;
        this.streaming = streaming;
        this.defaultPort = defaultPort;
        this.transportLayer = transportLayer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStreaming() {
        return streaming;
    }

    @Override
    public int getDefaultPort() {
        return defaultPort;
    }

    @Override
    public TransportLayer getTransportLayer() {
        return transportLayer;
    }

    @Override
    public Connector create(InetAddress address, int port, Serializer serializer, EventLoopGroup clientGroup) {
        return connectorFactory.create(address, port, serializer, clientGroup);
    }

}
