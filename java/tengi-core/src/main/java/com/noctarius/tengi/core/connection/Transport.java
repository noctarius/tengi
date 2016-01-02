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
package com.noctarius.tengi.core.connection;

/**
 * The <tt>Transport</tt> interface describes a basic transport
 * definition. A transport layer is any kind of data transmission
 * technique that may either use UDP, TCP or any other kind of
 * socket layer as well as can either be streaming (full duplex)
 * long-polling or polling implementation.
 */
public interface Transport {

    /**
     * Returns the internal name of this Transport implementation.
     * Names do not need to be unique, therefore building decisions
     * based on the name of an implementation might result in
     * unexpected behavior, better use the type itself.
     *
     * @return the name of the implementation
     */
    String getName();

    /**
     * Returns <tt>true</tt> if this Transport implementation is
     * a streaming (full-duplex) based approach or <tt>false</tt>
     * if based on either long-polling or polling.
     *
     * @return true if Transport is streaming, otherwise false
     */
    boolean isStreaming();

    /**
     * Returns the internal default port for this very transport.
     *
     * @return the default port of this transport
     */
    int getDefaultPort();

    /**
     * Returns the underlying {@link com.noctarius.tengi.core.connection.TransportLayer}
     * which defines the internally used socket type.
     *
     * @return the <tt>TransportLayer</tt> used by this transport
     */
    TransportLayer getTransportLayer();

}
