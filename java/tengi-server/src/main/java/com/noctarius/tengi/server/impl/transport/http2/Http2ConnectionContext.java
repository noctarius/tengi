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
package com.noctarius.tengi.server.impl.transport.http2;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.impl.CompletableFutureUtil;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Headers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

class Http2ConnectionContext
        extends ConnectionContext<Http2ConnectionEncoder> {

    private final AtomicInteger pushStreamId = new AtomicInteger();

    private final Http2ConnectionEncoder encoder;
    private final ChannelHandlerContext ctx;
    private final int streamId;

    public Http2ConnectionContext(Identifier connectionId, Serializer serializer, Transport transport,
                                  Http2ConnectionEncoder encoder, int streamId, ChannelHandlerContext ctx) {

        super(connectionId, serializer, transport);
        this.encoder = encoder;
        this.streamId = streamId;
        this.ctx = ctx;
    }

    @Override
    public CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message)
            throws Exception {

        ByteBuf bb = ctx.alloc().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(bb));
        buffer.writeBuffer(memoryBuffer);

        return CompletableFutureUtil.executeAsync(() -> {
            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
            encoder.writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());
            encoder.writeData(ctx, streamId, bb, 0, false, ctx.newPromise());
            ctx.flush();
            return message;
        });
    }

    @Override
    public CompletableFuture<Connection> writeSocket(Http2ConnectionEncoder socket, Connection connection,
                                                     MemoryBuffer memoryBuffer)
            throws Exception {

        ByteBuf bb = ctx.alloc().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(bb));
        buffer.writeBuffer(memoryBuffer);

        return CompletableFutureUtil.executeAsync(() -> {
            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
            encoder.writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());
            encoder.writeData(ctx, streamId, bb, 0, false, ctx.newPromise());
            ctx.flush();
            return connection;
        });
    }

    @Override
    public CompletableFuture<Connection> close(Connection connection) {
        return CompletableFutureUtil.executeAsync(() -> {
            encoder.close();
            ctx.close();
            return connection;
        });
    }

}
