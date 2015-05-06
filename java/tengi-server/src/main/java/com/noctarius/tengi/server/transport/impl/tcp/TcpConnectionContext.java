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
package com.noctarius.tengi.server.transport.impl.tcp;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.utils.CompletableFutureUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

class TcpConnectionContext
        extends ConnectionContext {

    private final Channel channel;

    TcpConnectionContext(Channel channel, Identifier connectionId, Protocol protocol, Transport transport) {
        super(connectionId, protocol, transport);
        this.channel = channel;
    }

    @Override
    public CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message)
            throws Exception {

        ByteBuf buffer = channel.alloc().ioBuffer();
        MemoryBuffer response = MemoryBufferFactory.unpooled(buffer, getProtocol());

        response.writeBoolean(true);
        response.writeObject(getConnectionId());
        response.writeBuffer(memoryBuffer);

        return CompletableFutureUtil.executeAsync(() -> {
            channel.writeAndFlush(buffer);
            return message;
        });
    }

    @Override
    public CompletableFuture<Connection> close(Connection connection) {
        return CompletableFutureUtil.executeAsync(() -> {
            channel.close().sync();
            return connection;
        });
    }
}
