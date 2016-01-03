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
package com.noctarius.tengi.server.impl.transport.http;

import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.server.TransportLayers;
import com.noctarius.tengi.server.spi.negotiation.NegotiatableTransport;
import com.noctarius.tengi.server.spi.negotiation.Negotiator;
import com.noctarius.tengi.spi.connection.impl.TransportConstants;

public class HttpTransport
        implements NegotiatableTransport {

    public HttpTransport() {
    }

    @Override
    public String getName() {
        return TransportConstants.TRANSPORT_NAME_HTTP;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public int getDefaultPort() {
        return TransportConstants.DEFAULT_PORT_TCP;
    }

    @Override
    public TransportLayer getTransportLayer() {
        return TransportLayers.TCP;
    }

    @Override
    public Negotiator getNegotiator() {
        return new HttpProtocolNegotiator();
    }
}
