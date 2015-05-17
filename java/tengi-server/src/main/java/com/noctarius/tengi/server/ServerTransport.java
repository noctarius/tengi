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
package com.noctarius.tengi.server;

import com.noctarius.tengi.spi.connection.Transport;
import com.noctarius.tengi.spi.connection.TransportLayer;
import com.noctarius.tengi.server.impl.transport.http.HttpTransport;
import com.noctarius.tengi.server.impl.transport.tcp.TcpTransport;

public enum ServerTransport
        implements Transport {

    HTTP_TRANSPORT(new HttpTransport()),
    HTTP2_TRANSPORT(null),
    WEBSOCKET_TRANSPORT(null),
    RDP_TRANSPORT(null),
    TCP_TRANSPORT(new TcpTransport()),
    UDP_TRANSPORT(null);

    private final Transport transport;

    private ServerTransport(Transport transport) {
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

}
