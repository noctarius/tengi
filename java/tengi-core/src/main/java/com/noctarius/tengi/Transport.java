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
package com.noctarius.tengi;

import com.noctarius.tengi.connection.Connection;

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
     * if based or either long-polling or polling.
     *
     * @return true if Transport is streaming, otherwise false
     */
    boolean isStreaming();

    /**
     * This method tries to accept the given
     * {@link com.noctarius.tengi.connection.Connection} and returns <tt>true</tt>
     * if accepted or <tt>false</tt> if connection does not support
     * this transportation type (for example because of the client
     * does not) or the acceptance was not possible.
     *
     * @param connection the connection to accept
     * @return true if accepted, otherwise false
     */
    boolean accept(Connection connection);

}
