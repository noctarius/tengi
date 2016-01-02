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
package com.noctarius.tengi.spi.connection;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.connection.packets.PollingRequest;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;

import java.util.concurrent.CompletableFuture;

/**
 * The <tt>ConnectionContext</tt> is an abstract base class for binding protocol processors
 * and {@link com.noctarius.tengi.core.connection.Connection} representations. It is capable
 * of forwarding write-requests to an underlying transport as well as implements some protocol
 * basics such as a common protocol header.
 *
 * @param <S> the socket type
 */
public abstract class ConnectionContext<S> {
    private final Identifier connectionId;
    private final Transport transport;
    private final Serializer serializer;

    /**
     * Constructs a new <tt>ConnectionContext</tt> instance using the given parameters.
     *
     * @param connectionId the connection's connectionId
     * @param serializer   the <tt>Serializer</tt> to bind
     * @param transport    the <tt>Transport</tt> that received the connection request
     */
    protected ConnectionContext(Identifier connectionId, Serializer serializer, Transport transport) {
        this.connectionId = connectionId;
        this.serializer = serializer;
        this.transport = transport;
    }

    /**
     * Returns the connectionId of the corresponding {@link com.noctarius.tengi.core.connection.Connection}.
     *
     * @return the connectionId for the bound <tt>Connection</tt>
     */
    public Identifier getConnectionId() {
        return connectionId;
    }

    /**
     * Returns the <tt>Transport</tt> instance that received the initial connection request and that handles
     * the connection itself.
     *
     * @return the underlying <tt>Transport</tt>
     */
    public Transport getTransport() {
        return transport;
    }

    /**
     * Returns the bound <tt>Protocol</tt> instance.
     *
     * @return the bound <tt>Protocol</tt>
     */
    public Protocol getProtocol() {
        return serializer.getProtocol();
    }

    /**
     * Returns the bound <tt>Serializer</tt> instance.
     *
     * @return the bound <tt>Serializer</tt>
     */
    public Serializer getSerializer() {
        return serializer;
    }

    /**
     * This method is called when a non-streaming {@link com.noctarius.tengi.core.connection.Transport}
     * receives a long-polling or polling request for all cached elements since the last retrieval.
     *
     * @param socket     the socket to send the response to
     * @param connection the bound connection of this context
     * @param request    the request itself
     */
    public void processPollingRequest(S socket, Connection connection, PollingRequest request) {
    }

    /**
     * Writes the given <tt>MemoryBuffer</tt> to the internally bound socket or, in case of non-streaming
     * transports, caches the buffer for later retrieval. The message passed in is meant for the
     * {@link java.util.concurrent.CompletableFuture} to return whenever the operation is done successfully.
     * It is not required to keep the message and a <tt>null</tt> return value must be expected as a legally
     * returned value from the future instance.
     *
     * @param memoryBuffer the buffer to write
     * @param message      the message that is represented inside the buffer
     * @return a <tt>CompletableFuture</tt> representing the sending process
     * @throws java.lang.Exception whenever an unexpected situation occurs while sending the object
     */
    public abstract CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message)
            throws Exception;

    /**
     * Writes the given <tt>MemoryBuffer</tt> to the given socket. Even in case of a non-streaming transport
     * this write operation cannot be delayed and must be executed immediately.
     *
     * @param socket       the socket to write to
     * @param connection   the connection to be returned from the future
     * @param memoryBuffer the buffer to write to the socket
     * @return a <tt>CompletableFuture</tt> representing the sending process
     * @throws java.lang.Exception whenever an unexpected situation occurs while sending the object
     */
    public abstract CompletableFuture<Connection> writeSocket(S socket, Connection connection, MemoryBuffer memoryBuffer)
            throws Exception;

    /**
     * Closes the <tt>ConnectionContext</tt> itself and must free all internal resources.
     *
     * @param connection the connection to be returned from the future
     * @return a <tt>CompletableFuture</tt> representing the close process
     */
    public abstract CompletableFuture<Connection> close(Connection connection);

    /**
     * Writes all required packet header information to the given <tt>MemoryBuffer</tt>.
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to write the header to
     * @return the given <tt>MemoryBuffer</tt> instance for fluent usage
     * @throws java.lang.Exception whenever an unexpected situation occurs while sending the object
     */
    protected MemoryBuffer preparePacket(MemoryBuffer memoryBuffer)
            throws Exception {

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", true);
            encoder.writeObject("connectionId", getConnectionId());
            return memoryBuffer;
        }
    }

}
