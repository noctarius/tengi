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
package com.noctarius.tengi.client.impl.transport.tcp;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ServerConnection;
import com.noctarius.tengi.client.impl.transport.ClientConnectionProcessor;
import com.noctarius.tengi.core.buffer.MemoryBuffer;
import com.noctarius.tengi.core.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.core.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.Connection;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static com.noctarius.tengi.client.impl.ClientUtil.CONNECTION;
import static com.noctarius.tengi.client.impl.ClientUtil.connectionAttribute;

class TcpConnectionProcessor
        extends ClientConnectionProcessor<ByteBuf, Channel, ByteBuf> {

    TcpConnectionProcessor(Serializer serializer, Connector<ByteBuf> connector) {
        super(serializer, connector);
    }

    @Override
    protected AutoClosableDecoder decode(ChannelHandlerContext ctx, ByteBuf buffer)
            throws Exception {

        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        return getSerializer().retrieveDecoder(memoryBuffer);
    }

    @Override
    protected ConnectionContext<Channel> createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new TcpConnectionContext(connectionId, getSerializer(), getConnector());
    }

    @Override
    protected Connection createConnection(ChannelHandlerContext ctx, ConnectionContext<Channel> connectionContext,
                                          Identifier connectionId) {

        ServerConnection connection = new TcpServerConnection(connectionContext, connectionId, getConnector(), getSerializer());
        connectionAttribute(ctx, CONNECTION, connection);
        return connection;
    }

}
