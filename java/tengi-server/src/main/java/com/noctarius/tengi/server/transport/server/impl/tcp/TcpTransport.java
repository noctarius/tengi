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
package com.noctarius.tengi.server.transport.server.impl.tcp;

import com.noctarius.tengi.Transport;
import com.noctarius.tengi.TransportLayer;
import com.noctarius.tengi.connection.TransportConstants;

public class TcpTransport
        implements Transport {

    @Override
    public String getName() {
        return TransportConstants.TRANSPORT_NAME_TCP;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public int getDefaultPort() {
        return TransportConstants.DEFAULT_PORT_TCP;
    }

    @Override
    public TransportLayer getTransportLayer() {
        return TransportLayer.TCP;
    }

}
