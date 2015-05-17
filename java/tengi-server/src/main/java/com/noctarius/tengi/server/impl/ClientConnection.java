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
package com.noctarius.tengi.server.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.spi.connection.Transport;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.spi.connection.AbstractConnection;
import com.noctarius.tengi.Connection;
import com.noctarius.tengi.spi.connection.ConnectionContext;

public class ClientConnection
        extends AbstractConnection {

    ClientConnection(ConnectionContext connectionContext, Identifier connectionId, //
                     Transport transport, Serializer serializer) {

        super(connectionContext, connectionId, transport, serializer);
    }

    public ConnectionContext getConnectionContext() {
        return super.getConnectionContext();
    }

    void publishMessage(Message message) {
        getMessageListeners().forEach((listener) -> listener.onMessage(this, message));
    }

    @Override
    public void onExceptionally(Connection connection, Throwable throwable) {
        disconnect();
    }

    @Override
    public void exceptionally(Throwable throwable) {
        super.exceptionally(throwable);
    }

}
