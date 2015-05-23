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
package com.noctarius.tengi.client;

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.listener.ConnectedListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

/**
 * <p>The <tt>Client</tt> interface is the static entry point for tengi clients. It provides a
 * factory method to create new client instances based on given
 * {@link com.noctarius.tengi.core.config.Configuration}s.</p>
 * <pre>
 *   Configuration configuration = buildConfiguration();
 *   Client client = Client.create(configuration);
 *   CompletableFuture&lt;Connection&gt; future = client.connect("localhost");
 * </pre>
 */
public interface Client {

    /**
     * <p>Connects the client to the given host and using {@link com.noctarius.tengi.core.connection.Transport}s
     * configured in the {@link com.noctarius.tengi.core.config.Configuration} instance passed to
     * {@link #create(com.noctarius.tengi.core.config.Configuration)} method when creating the client.</p>
     * <p>The <tt>Transport</tt>s will be tested in order they were added to the configuration.</p>
     * <p>The returned {@link java.util.concurrent.CompletableFuture} might throw a
     * {@link com.noctarius.tengi.core.exception.ConnectionFailedException} when non of the configured transports
     * was able to connect.</p>
     * <p>Calling this method is equivalent to
     * {@link #connect(java.lang.String, com.noctarius.tengi.core.listener.ConnectedListener)} with <tt>null</tt>
     * as the given {@link com.noctarius.tengi.core.listener.ConnectedListener}.</p>
     *
     * @param host the host address to connect to
     * @return the future for registering additional listeners or just to wait for completion
     * @throws UnknownHostException whenever the host address is not resolvable
     */
    default CompletableFuture<Connection> connect(String host)
            throws UnknownHostException {

        return connect(host, null);
    }

    /**
     * <p>Connects the client to the given host and using {@link com.noctarius.tengi.core.connection.Transport}s
     * configured in the {@link com.noctarius.tengi.core.config.Configuration} instance passed to
     * {@link #create(com.noctarius.tengi.core.config.Configuration)} method when creating the client.</p>
     * <p>The <tt>Transport</tt>s will be tested in order they were added to the configuration. When a transport
     * was able to connect, the given {@link com.noctarius.tengi.core.listener.ConnectedListener} is called and
     * hands over the connection to the user.</p>
     * <p>The returned {@link java.util.concurrent.CompletableFuture} might throw a
     * {@link com.noctarius.tengi.core.exception.ConnectionFailedException} when non of the configured transports
     * was able to connect. In this case the passed <tt>ConnectedListener</tt> won't be called too.</p>
     *
     * @param host              the host address to connect to
     * @param connectedListener the callback to call when a transport was able to connect
     * @return the future for registering additional listeners or just to wait for completion
     * @throws UnknownHostException whenever the host address is not resolvable
     */
    CompletableFuture<Connection> connect(String host, ConnectedListener connectedListener)
            throws UnknownHostException;

    /**
     * <p>Connects the client to the given address and using {@link com.noctarius.tengi.core.connection.Transport}s
     * configured in the {@link com.noctarius.tengi.core.config.Configuration} instance passed to
     * {@link #create(com.noctarius.tengi.core.config.Configuration)} method when creating the client.</p>
     * <p>The <tt>Transport</tt>s will be tested in order they were added to the configuration.</p>
     * <p>The returned {@link java.util.concurrent.CompletableFuture} might throw a
     * {@link com.noctarius.tengi.core.exception.ConnectionFailedException} when non of the configured transports
     * was able to connect.</p>
     * <p>Calling this method is equivalent to
     * {@link #connect(java.net.InetAddress, com.noctarius.tengi.core.listener.ConnectedListener)} with <tt>null</tt>
     * as the given {@link com.noctarius.tengi.core.listener.ConnectedListener}.</p>
     *
     * @param address the host address to connect to
     * @return the future for registering additional listeners or just to wait for completion
     */
    default CompletableFuture<Connection> connect(InetAddress address) {
        return connect(address, null);
    }

    /**
     * <p>Connects the client to the given address and using {@link com.noctarius.tengi.core.connection.Transport}s
     * configured in the {@link com.noctarius.tengi.core.config.Configuration} instance passed to
     * {@link #create(com.noctarius.tengi.core.config.Configuration)} method when creating the client.</p>
     * <p>The <tt>Transport</tt>s will be tested in order they were added to the configuration. When a transport
     * was able to connect, the given {@link com.noctarius.tengi.core.listener.ConnectedListener} is called and
     * hands over the connection to the user.</p>
     * <p>The returned {@link java.util.concurrent.CompletableFuture} might throw a
     * {@link com.noctarius.tengi.core.exception.ConnectionFailedException} when non of the configured transports
     * was able to connect. In this case the passed <tt>ConnectedListener</tt> won't be called too.</p>
     *
     * @param address           the host address to connect to
     * @param connectedListener the callback to call when a transport was able to connect
     * @return the future for registering additional listeners or just to wait for completion
     */
    CompletableFuture<Connection> connect(InetAddress address, ConnectedListener connectedListener);

    /**
     * This factory method is used to create new <tt>Client</tt> instances. It will use the given configuration
     * to setup internals and to register necessary transports.
     *
     * @param configuration the configuration to setup the client
     * @return the created client instance bound to the given configuration
     */
    public static Client create(Configuration configuration) {
        return new ClientImpl(configuration);
    }

}
