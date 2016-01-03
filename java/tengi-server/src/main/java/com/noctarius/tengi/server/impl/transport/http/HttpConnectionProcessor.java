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
package com.noctarius.tengi.server.impl.transport.http;

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.server.ServerTransports;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.ServerConnectionProcessor;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class HttpConnectionProcessor
        extends ServerConnectionProcessor<FullHttpRequest> {

    public HttpConnectionProcessor(ConnectionManager connectionManager, Serializer serializer) {
        super(connectionManager, serializer, ServerTransports.HTTP_TRANSPORT);
    }

    @Override
    protected AutoClosableDecoder decode(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {

        // Only POST requests are allowed, kill the request
        if (HttpMethod.POST != request.method()) {
            sendHttpResponse(ctx, request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED));
            return null;
        }

        // Unknown uri, kill the request
        if (!"/channel".equals(request.uri())) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return null;
        }

        String mimeType = getSerializer().getProtocol().getMimeType();
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);

        // Wrong content type, kill the request
        if (!mimeType.equals(contentType)) {
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_ACCEPTABLE));
            return null;
        }

        // TODO: Pool MemoryBuffers
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(request.content());
        return getSerializer().retrieveDecoder(memoryBuffer);
    }

    @Override
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new HttpConnectionContext(connectionId, getSerializer(), getTransport());
    }

    static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        // Generate an error page if response getStatus code is not OK (200).
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(response, response.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.writeAndFlush(response);
        if (!HttpHeaderUtil.isKeepAlive(request) || response.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
