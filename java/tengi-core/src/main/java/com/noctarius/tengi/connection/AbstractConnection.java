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
package com.noctarius.tengi.connection;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.listener.ConnectionListener;
import com.noctarius.tengi.listener.FrameListener;
import com.noctarius.tengi.listener.MessageListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractConnection
        implements Connection {

    private final Identifier connectionId;
    private final Transport transport;

    private final Map<Identifier, FrameListener> frameListeners = new HashMap<>();
    private final Map<Identifier, MessageListener> messageListeners = new HashMap<>();
    private final Map<Identifier, ConnectionListener> connectionListeners = new HashMap<>();

    protected AbstractConnection(Identifier connectionId, Transport transport) {
        this.connectionId = connectionId;
        this.transport = transport;
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
    public Identifier addFrameListener(FrameListener frameListener) {
        for (FrameListener fl : frameListeners.values()) {
            if (fl == frameListener) {
                throw new IllegalStateException("FrameListener is already registered");
            }
        }

        Identifier identifier = Identifier.randomIdentifier();
        frameListeners.put(identifier, frameListener);
        return identifier;
    }

    @Override
    public void removeFrameListener(Identifier registrationIdentifier) {
        frameListeners.remove(registrationIdentifier);
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

    protected Collection<FrameListener> getFrameListeners() {
        return Collections.unmodifiableCollection(frameListeners.values());
    }

    protected Collection<MessageListener> getMessageListeners() {
        return Collections.unmodifiableCollection(messageListeners.values());
    }

    protected Collection<ConnectionListener> getConnectionListeners() {
        return Collections.unmodifiableCollection(connectionListeners.values());
    }

}
