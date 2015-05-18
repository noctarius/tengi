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
package com.noctarius.tengi.client.impl.transport.http;

import com.noctarius.tengi.core.Identifier;
import com.noctarius.tengi.core.Message;
import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.core.impl.CompletableFutureUtil;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.core.Connection;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

import java.util.concurrent.CompletableFuture;

import static com.noctarius.tengi.client.impl.transport.http.HttpConnector.buildHttpRequest;

class HttpConnectionContext
        extends ConnectionContext<Channel> {

    private final Connector<HttpRequest> connector;

    HttpConnectionContext(Identifier connectionId, Serializer serializer, Connector<HttpRequest> connector) {
        super(connectionId, serializer, connector);
        this.connector = connector;
    }

    @Override
    public CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message)
            throws Exception {

        ByteBuf request = connector.allocator().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(request));
        buffer.writeBuffer(memoryBuffer);

        return CompletableFutureUtil.executeAsync(() -> {
            connector.write(buildHttpRequest(request));
            return message;
        });
    }

    @Override
    public CompletableFuture<Connection> writeSocket(Channel channel, Connection connection, MemoryBuffer memoryBuffer)
            throws Exception {

        ByteBuf request = channel.alloc().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(request));
        buffer.writeBuffer(memoryBuffer);
        return CompletableFutureUtil.executeAsync(() -> {
            channel.writeAndFlush(buildHttpRequest(request)).sync();
            return connection;
        });
    }

    @Override
    public CompletableFuture<Connection> close(Connection connection) {
        return CompletableFutureUtil.executeAsync(() -> {
            connector.destroy();
            return connection;
        });
    }

}
