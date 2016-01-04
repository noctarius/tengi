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

import com.noctarius.tengi.core.connection.TransportLayer;

class SocketCoordinate {
    private final int port;
    private final TransportLayer transportLayer;

    SocketCoordinate(int port, TransportLayer transportLayer) {
        this.port = port;
        this.transportLayer = transportLayer;
    }

    public int getPort() {
        return port;
    }

    public TransportLayer getTransportLayer() {
        return transportLayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SocketCoordinate)) {
            return false;
        }

        SocketCoordinate that = (SocketCoordinate) o;

        if (port != that.port) {
            return false;
        }
        return transportLayer != null ? transportLayer.equals(that.transportLayer) : that.transportLayer == null;

    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + (transportLayer != null ? transportLayer.hashCode() : 0);
        return result;
    }
}
