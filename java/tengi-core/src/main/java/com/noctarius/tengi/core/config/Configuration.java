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
package com.noctarius.tengi.core.config;

import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.connection.handshake.HandshakeHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>This interface describes a configuration for tengi. Clients and servers use
 * the same configuration style. A configuration is required to not change after
 * the client or server is created. It is highly recommended to only build immutable
 * implementations of this interface.</p>
 * <p>Implementations also need to be fully thread-safe!</p>
 */
public interface Configuration {

    /**
     * <p>Returns an unmodifiable set of configured
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}s bound to their
     * corresponding {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter}s.</p>
     *
     * @return a set of configured <tt>Marshaller</tt>s and <tt>MarshallerFilter</tt>s
     */
    Set<MarshallerConfiguration> getMarshallers();

    /**
     * Returns an unmodifiable list of configured {@link com.noctarius.tengi.core.connection.Transport}s.
     * The returned order corresponds to the order the transports were configured. It also defines the
     * order in which transports will be tested to connect.
     *
     * @return a list of <tt>Transport</tt>s with guaranteed order
     */
    List<Transport> getTransports();

    /**
     * Returns a unmodifiable map of configured port mapped to their transports. This map only contains
     * explicitly configured ports but no defaults. Default ports are retrieved using
     * {@link com.noctarius.tengi.core.connection.Transport#getDefaultPort()} or
     * {@link #getTransportPort(com.noctarius.tengi.core.connection.Transport)}.
     *
     * @return a mapping of explicitly defined ports mapped to their transports
     */
    Map<Transport, Integer> getTransportPorts();

    /**
     * Returns either the explicitly configured or default port of the given transport. No check if the
     * transport is supported is executed.
     *
     * @param transport the <tt>Transport</tt> instance to retrieve the port for
     * @return the explicitly configured port if set, otherwise the default port of the transport
     */
    int getTransportPort(Transport transport);

    /**
     * Returns if the transports should use SSL or not. Transports that don't support SSL encryption might
     * silently ignore this configuration. Please refer to any transport documentation to find out if a
     * transport supports SSL.
     *
     * @return true if SSL should be enabled, otherwise false
     */
    boolean isSslEnabled();

    /**
     * Returns the configured {@link com.noctarius.tengi.core.connection.handshake.HandshakeHandler} instance
     * to verify, accept or deny new connection handshakes. On client-side additional information can be
     * extracted from the handshake response retrieved from the server.
     *
     * @return the <tt>HandshakeHandler</tt> instance to be used for handshake handling
     */
    HandshakeHandler getHandshakeHandler();

}
