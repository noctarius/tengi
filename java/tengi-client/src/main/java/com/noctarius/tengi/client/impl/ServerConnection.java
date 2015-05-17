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
package com.noctarius.tengi.client.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.spi.connection.AbstractConnection;
import com.noctarius.tengi.Connection;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.connection.handshake.LongPollingResponse;
import io.netty.channel.Channel;

public class ServerConnection
        extends AbstractConnection {

    protected ServerConnection(ConnectionContext<Channel> connectionContext, Identifier connectionId, //
                               Connector connector, Serializer serializer) {

        super(connectionContext, connectionId, connector, serializer);
        addConnectionListener(this);
    }

    public ConnectionContext getConnectionContext() {
        return super.getConnectionContext();
    }

    public void publishMessage(Message message) {
        if (getTransport().isStreaming() || !(message.getBody() instanceof LongPollingResponse)) {
            getMessageListeners().forEach((listener) -> listener.onMessage(this, message));

        } else {
            LongPollingResponse longPollingResponse = message.getBody();
            for (Message m : longPollingResponse.getMessages()) {
                getMessageListeners().forEach((listener) -> listener.onMessage(this, m));
            }
        }
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
