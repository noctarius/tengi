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

import com.noctarius.tengi.Connection;
import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.core.buffer.MemoryBuffer;
import com.noctarius.tengi.core.serialization.Protocol;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.core.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.connection.handshake.LongPollingRequest;

import java.util.concurrent.CompletableFuture;

public abstract class ConnectionContext<S> {
    private final Identifier connectionId;
    private final Transport transport;
    private final Serializer serializer;

    protected ConnectionContext(Identifier connectionId, Serializer serializer, Transport transport) {
        this.connectionId = connectionId;
        this.serializer = serializer;
        this.transport = transport;
    }

    public Identifier getConnectionId() {
        return connectionId;
    }

    public Transport getTransport() {
        return transport;
    }

    public Protocol getProtocol() {
        return serializer.getProtocol();
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void processLongPollingRequest(S socket, Connection connection, LongPollingRequest request) {
    }

    public abstract CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message object)
            throws Exception;

    public abstract CompletableFuture<Connection> writeSocket(S socket, Connection connection, MemoryBuffer memoryBuffer)
            throws Exception;

    public abstract CompletableFuture<Connection> close(Connection connection);

    protected MemoryBuffer preparePacket(MemoryBuffer memoryBuffer)
            throws Exception {

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", true);
            encoder.writeObject("connectionId", getConnectionId());
            return memoryBuffer;
        }
    }

}
