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

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.listener.ConnectedListener;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

/**
 * <p>The <tt>Server</tt> interface is the static entry point for tengi servers. It provides a
 * factory method to create new server instances based on given
 * {@link com.noctarius.tengi.core.config.Configuration}s.</p>
 * <pre>
 *   Configuration configuration = buildConfiguration();
 *   Server server = Server.create(configuration);
 *   CompletableFuture&lt;Channel&gt; future = server.start(
 *       (connection) -&gt; connection.addMessageListener(...));
 * </pre>
 */
public interface Server {

    /**
     * <p>Starts the created <tt>Server</tt> instance and binds the given
     * {@link com.noctarius.tengi.core.listener.ConnectedListener} to notify the user application
     * whenever a new client has connected and successfully finished the initial handshake.</p>
     *
     * @param connectedListener the <tt>ConnectedListener</tt> to handle new client connections
     * @return a <tt>CompletableFuture</tt> representing the pending startup
     * @throws java.lang.NullPointerException  when <tt>connectedListener</tt> is null
     * @throws java.lang.IllegalStateException whenever the server instance is in a non-startable state
     */
    CompletableFuture<Channel> start(ConnectedListener connectedListener);

    /**
     * Stops the current server instance and unbinds the transport ports. In addition it closes all
     * currently open client connections and releases internally used resources.
     *
     * @return a <tt>CompletableFuture</tt> representing the pending stop process
     */
    CompletableFuture<Channel> stop();

    /**
     * This factory method is used to create new <tt>Server</tt> instances. It will use the given configuration
     * to setup internals and to register necessary transports.
     *
     * @param configuration the configuration to setup the client
     * @return the created server instance bound to the given configuration
     * @throws java.security.cert.CertificateException when the server is started with SSL but the SSL context cannot be created
     * @throws java.lang.Exception                     whenever an unexpected situation occurs
     */
    public static Server create(Configuration configuration)
            throws Exception {

        return new ServerImpl(configuration);
    }

}
