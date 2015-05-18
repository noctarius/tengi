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
package com.noctarius.tengi.core.connection;

/**
 * A <tt>TransportLayer defines the underlying socket type of a
 * {@link com.noctarius.tengi.core.connection.Transport}</tt> implementation.
 * Currently supported socket types are <tt>TCP</tt>, <tt>UDP</tt> or
 * <tt>SCTP</tt>.
 */
public enum TransportLayer {

    /**
     * This value defines, that the {@link com.noctarius.tengi.core.connection.Transport}
     * uses an internal <tt>TCP</tt> socket to make or accept connections.
     */
    TCP,

    /**
     * This value defines, that the {@link com.noctarius.tengi.core.connection.Transport}
     * uses an internal <tt>UDP</tt> socket to make or accept connections.
     */
    UDP,

    /**
     * This value defines, that the {@link com.noctarius.tengi.core.connection.Transport}
     * uses an internal <tt>SCTP</tt> socket to make or accept connections.
     */
    SCTP

}
