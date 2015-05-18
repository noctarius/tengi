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

import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.client.impl.ServerConnection;
import com.noctarius.tengi.client.impl.transport.ClientConnectionProcessor;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import static com.noctarius.tengi.client.impl.ClientUtil.CONNECTION;
import static com.noctarius.tengi.client.impl.ClientUtil.connectionAttribute;

class HttpConnectionProcessor
        extends ClientConnectionProcessor<FullHttpResponse, Channel, HttpRequest> {

    HttpConnectionProcessor(Serializer serializer, Connector<HttpRequest> connector) {
        super(serializer, connector);
    }

    @Override
    protected AutoClosableDecoder decode(ChannelHandlerContext ctx, FullHttpResponse response)
            throws Exception {

        String mimeType = getSerializer().getProtocol().getMimeType();
        String contentType = response.headers().get(HttpHeaderNames.CONTENT_TYPE);

        // Wrong content type, kill the request
        if (!mimeType.equals(contentType)) {
            ctx.close();
            return null;
        }

        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(response.content());
        return getSerializer().retrieveDecoder(memoryBuffer);
    }

    @Override
    protected ConnectionContext<Channel> createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new HttpConnectionContext(connectionId, getSerializer(), getConnector());
    }

    @Override
    protected Connection createConnection(ChannelHandlerContext ctx, ConnectionContext<Channel> connectionContext,
                                          Identifier connectionId) {

        ServerConnection connection = new HttpServerConnection(connectionContext, connectionId, getConnector(), getSerializer());
        connectionAttribute(ctx, CONNECTION, connection);
        return connection;
    }

}
