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

import com.noctarius.tengi.core.listener.ConnectionListener;
import com.noctarius.tengi.core.listener.MessageListener;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;

import java.util.concurrent.CompletableFuture;

/**
 * <p>The <tt>Connection</tt> interface is the top level type of
 * any kind of connections between client and server.</p>
 * <p>Every connection is identified using a unique Id
 * ({@link com.noctarius.tengi.core.model.Identifier}) which can be used to
 * identify same clients over multiple socket connections or
 * reconnects (e.g. polling styled transports).</p>
 */
public interface Connection
        extends AutoCloseable {

    /**
     * Returns the unique connection Id which can be used to
     * identify a connection over multiple connections or
     * reconnects (e.g. polling connections).
     *
     * @return the unique identifier of this connection
     */
    Identifier getConnectionId();

    /**
     * Returns the transport that accepted this connection and
     * handles the underlying data communication.
     *
     * @return the transport of this connection
     */
    Transport getTransport();

    /**
     * Adds a {@link com.noctarius.tengi.core.listener.MessageListener}
     * to this connection and returns an {@link com.noctarius.tengi.core.model.Identifier}
     * to uniquely identify this registration.
     *
     * @param messageListener MessageListener instance to add
     * @return a unique identifier for this registration
     */
    Identifier addMessageListener(MessageListener messageListener);

    /**
     * <p>Removes a previously registered {@link com.noctarius.tengi.core.listener.MessageListener}
     * based on the {@link Identifier} returned from the registration.</p>
     * <p>Using this method, an anonymous listener implementations or Java 8
     * lambdas can be registered and removed.</p>
     *
     * @param registrationIdentifier the Identifier generated while registration
     */
    void removeMessageListener(Identifier registrationIdentifier);

    /**
     * Adds a {@link com.noctarius.tengi.core.listener.ConnectionListener}
     * to this connection and returns an {@link com.noctarius.tengi.core.model.Identifier}
     * to uniquely identify this registration.
     *
     * @param connectionListener ConnectionListener instance to add
     * @return a unique identifier for this registration
     */
    Identifier addConnectionListener(ConnectionListener connectionListener);

    /**
     * <p>Removes a previously registered {@link com.noctarius.tengi.core.listener.ConnectionListener}
     * based on the {@link Identifier} returned from registration.</p>
     * <p>Using this method, an anonymous listener implementations or Java 8
     * lambdas can be registered and removed.</p>
     *
     * @param registrationIdentifier the Identifier generated while registration
     */
    void removeConnectionListener(Identifier registrationIdentifier);

    /**
     * Writes a common object to this connection. This types needs to be serializable using
     * a previously registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or
     * must be of a commonly supported (internally supported) type. Objects send using this method
     * are automatically wrapped into a {@link com.noctarius.tengi.core.model.Message} object before sent
     * and will be delivered to a registered {@link com.noctarius.tengi.core.listener.MessageListener}
     * on the receiver side.
     *
     * @param object object to write
     * @param <O>    the type of the object to write
     * @return a CompletionFuture to add additional behavior after the object is written
     * @throws java.lang.Exception whenever an unexpected situation occurs while writing or sending the object
     */
    <O> CompletableFuture<Message> writeObject(O object)
            throws Exception;

    /**
     * Disconnects the connection and releases any internally acquired resources that are assigned
     * to this connection.
     *
     * @return a CompletionFuture to add additional behavior (like cleanup) after the connection is closed
     */
    CompletableFuture<Connection> disconnect();

}
