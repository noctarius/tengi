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
package com.noctarius.tengi.spi.connection;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.listener.ConnectionListener;
import com.noctarius.tengi.core.listener.MessageListener;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.serialization.Serializer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The <tt>AbstractConnection</tt> acts as a base class for server- and client-side
 * {@link com.noctarius.tengi.core.connection.Connection} implementations. It handles
 * listener registrations as well as basic notification logic.
 */
public abstract class AbstractConnection
        implements Connection, ConnectionListener {

    private final ConnectionContext connectionContext;
    private final Identifier connectionId;
    private final Transport transport;
    private final Serializer serializer;

    private final Map<Identifier, MessageListener> messageListeners = new ConcurrentHashMap<>();
    private final Map<Identifier, ConnectionListener> connectionListeners = new ConcurrentHashMap<>();

    /**
     * Constructs a new <tt>AbstractConnection</tt> using the given parameters.
     *
     * @param connectionContext the <tt>ConnectionContext</tt> to bind
     * @param connectionId      the connection's connectionId
     * @param transport         the <tt>Transport</tt> that received the connection request
     * @param serializer        the <tt>Serializer</tt> to bind
     */
    protected AbstractConnection(ConnectionContext connectionContext, Identifier connectionId, //
                                 Transport transport, Serializer serializer) {

        this.connectionContext = connectionContext;
        this.connectionId = connectionId;
        this.transport = transport;
        this.serializer = serializer;
    }

    @Override
    public Identifier getConnectionId() {
        return connectionId;
    }

    @Override
    public Transport getTransport() {
        return transport;
    }

    @Override
    public Identifier addMessageListener(MessageListener messageListener) {
        for (MessageListener ml : messageListeners.values()) {
            if (ml == messageListener) {
                throw new IllegalStateException("MessageListener is already registered");
            }
        }

        Identifier identifier = Identifier.randomIdentifier();
        messageListeners.put(identifier, messageListener);
        return identifier;
    }

    @Override
    public void removeMessageListener(Identifier registrationIdentifier) {
        messageListeners.remove(registrationIdentifier);
    }

    @Override
    public Identifier addConnectionListener(ConnectionListener connectionListener) {
        for (ConnectionListener cl : connectionListeners.values()) {
            if (cl == connectionListener) {
                throw new IllegalStateException("ConnectionListener is already registered");
            }
        }

        Identifier identifier = Identifier.randomIdentifier();
        connectionListeners.put(identifier, connectionListener);
        return identifier;
    }

    @Override
    public void removeConnectionListener(Identifier registrationIdentifier) {
        connectionListeners.remove(registrationIdentifier);
    }

    @Override
    public <O> CompletableFuture<Message> writeObject(O object)
            throws Exception {

        Message message;
        if (object instanceof Message) {
            message = (Message) object;
        } else {
            message = Message.create(object);
        }

        MemoryBuffer memoryBuffer = serializer.writeObject(message);
        return connectionContext.writeMemoryBuffer(memoryBuffer, message);
    }

    @Override
    public CompletableFuture<Connection> disconnect() {
        return connectionContext.close(this);
    }

    @Override
    public void close()
            throws Exception {

        disconnect().get();
    }

    @Override
    public void onConnection(Connection connection) {
    }

    @Override
    public void onDisconnect(Connection connection) {
    }

    @Override
    public void onExceptionally(Connection connection, Throwable throwable) {
    }

    /**
     * Returns all registered {@link com.noctarius.tengi.core.listener.MessageListener}s. The returned
     * collection is not modifiable.
     *
     * @return all registered <tt>MessageListener</tt>s
     */
    protected Collection<MessageListener> getMessageListeners() {
        return Collections.unmodifiableCollection(messageListeners.values());
    }

    /**
     * Returns all registered {@link com.noctarius.tengi.core.listener.ConnectionListener}s. The returned
     * collection is not modifiable.
     *
     * @return all registered <tt>ConnectionListener</tt>s
     */
    protected Collection<ConnectionListener> getConnectionListeners() {
        return Collections.unmodifiableCollection(connectionListeners.values());
    }

    /**
     * Notifies all registered {@link com.noctarius.tengi.core.listener.ConnectionListener}s about an
     * unexpected exception occurrence.
     *
     * @param throwable the <tt>Throwable</tt> instance to delegate to listeners
     */
    protected void exceptionally(Throwable throwable) {
        for (ConnectionListener connectionListener : getConnectionListeners()) {
            connectionListener.onExceptionally(this, throwable);
        }
    }

    /**
     * Returns the <tt>ConnectionContext</tt> bound to this connection.
     *
     * @return the bound <tt>ConnectionContext</tt>
     */
    protected ConnectionContext getConnectionContext() {
        return connectionContext;
    }

}
