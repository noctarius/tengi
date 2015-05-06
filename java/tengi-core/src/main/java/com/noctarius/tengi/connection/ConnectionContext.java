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
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.connection.impl.LongPollingRequest;
import com.noctarius.tengi.serialization.Protocol;

import java.util.concurrent.CompletableFuture;

public abstract class ConnectionContext {

    private final Identifier connectionId;
    private final Transport transport;
    private final Protocol protocol;

    protected ConnectionContext(Identifier connectionId, Protocol protocol, Transport transport) {
        this.connectionId = connectionId;
        this.transport = transport;
        this.protocol = protocol;
    }

    public Identifier getConnectionId() {
        return connectionId;
    }

    public Transport getTransport() {
        return transport;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void processLongPollingRequest(LongPollingRequest request) {
    }

    public abstract CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message object)
            throws Exception;

    public abstract CompletableFuture<Connection> close(Connection connection);
}
