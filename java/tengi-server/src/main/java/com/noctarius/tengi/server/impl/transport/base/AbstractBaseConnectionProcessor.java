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
package com.noctarius.tengi.server.impl.transport.base;

import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.ServerConnectionProcessor;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

@ChannelHandler.Sharable
public abstract class AbstractBaseConnectionProcessor
        extends ServerConnectionProcessor<ByteBuf> {

    protected AbstractBaseConnectionProcessor(ConnectionManager connectionManager, Serializer serializer, Transport transport) {
        super(connectionManager, serializer, transport);
    }

    @Override
    protected AutoClosableDecoder decode(ChannelHandlerContext ctx, ByteBuf buffer)
            throws Exception {

        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        return getSerializer().retrieveDecoder(memoryBuffer);
    }

    @Override
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new BaseConnectionContext(ctx.channel(), connectionId, getSerializer(), getTransport());
    }

}
