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
package com.noctarius.tengi.server.impl.transport.tcp;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.impl.FutureUtil;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;

class TcpConnectionContext
        extends ConnectionContext<Channel> {

    private final Channel channel;

    TcpConnectionContext(Channel channel, Identifier connectionId, Serializer serializer, Transport transport) {
        super(connectionId, serializer, transport);
        this.channel = channel;
    }

    @Override
    public CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message)
            throws Exception {

        ByteBuf response = channel.alloc().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(response));
        buffer.writeBuffer(memoryBuffer);

        return FutureUtil.executeAsync(() -> {
            channel.writeAndFlush(response);
            return message;
        });
    }

    @Override
    public CompletableFuture<Connection> writeSocket(Channel channel, Connection connection, MemoryBuffer memoryBuffer)
            throws Exception {

        ByteBuf response = channel.alloc().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(response));
        buffer.writeBuffer(memoryBuffer);
        return FutureUtil.executeAsync(() -> {
            channel.writeAndFlush(response);
            return connection;
        });
    }

    @Override
    public CompletableFuture<Connection> close(Connection connection) {
        return FutureUtil.executeAsync(() -> {
            channel.close().sync();
            return connection;
        });
    }
}
