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
package com.noctarius.tengi.transport;

import com.noctarius.tengi.Transport;
import com.noctarius.tengi.TransportLayer;
import com.noctarius.tengi.client.Connector;
import com.noctarius.tengi.client.ConnectorFactory;
import com.noctarius.tengi.transport.client.impl.tcp.TcpConnector;

import static com.noctarius.tengi.connection.TransportConstants.DEFAULT_PORT_TCP;
import static com.noctarius.tengi.connection.TransportConstants.DEFAULT_PORT_UDP;
import static com.noctarius.tengi.connection.TransportConstants.TRANSPORT_NAME_HTTP;
import static com.noctarius.tengi.connection.TransportConstants.TRANSPORT_NAME_HTTP2;
import static com.noctarius.tengi.connection.TransportConstants.TRANSPORT_NAME_RDP;
import static com.noctarius.tengi.connection.TransportConstants.TRANSPORT_NAME_TCP;
import static com.noctarius.tengi.connection.TransportConstants.TRANSPORT_NAME_UDP;
import static com.noctarius.tengi.connection.TransportConstants.TRANSPORT_NAME_WEBSOCKET;

public enum ClientTransport
        implements Transport, ConnectorFactory {

    HTTP_TRANSPORT(null, TRANSPORT_NAME_HTTP, false, DEFAULT_PORT_TCP, TransportLayer.TCP),
    HTTP2_TRANSPORT(null, TRANSPORT_NAME_HTTP2, true, DEFAULT_PORT_TCP, TransportLayer.TCP),
    WEBSOCKET_TRANSPORT(null, TRANSPORT_NAME_WEBSOCKET, true, DEFAULT_PORT_TCP, TransportLayer.TCP),
    RDP_TRANSPORT(null, TRANSPORT_NAME_RDP, true, DEFAULT_PORT_UDP, TransportLayer.UDP),
    TCP_TRANSPORT(TcpConnector::new, TRANSPORT_NAME_TCP, true, DEFAULT_PORT_TCP, TransportLayer.TCP),
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
    public Connector create() {
        return connectorFactory.create();
    }

}
