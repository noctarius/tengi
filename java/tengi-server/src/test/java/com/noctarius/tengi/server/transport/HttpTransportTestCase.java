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
package com.noctarius.tengi.server.transport;

import com.noctarius.tengi.Message;
import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class HttpTransportTestCase
        extends AbstractTransportTestCase {

    @Test
    public void testHttpTransport()
            throws Exception {

        InputStream is = getClass().getResourceAsStream("transport.types.manifest");
        Serializer serializer = Serializer.create(new DefaultProtocol(is, Collections.emptyList()));

        CompletableFuture<Object> future = new CompletableFuture<>();

        Initializer initializer = initializer(serializer, future);
        Runner runner = (channel) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBoolean("loggedIn", false);

            Packet packet = new Packet("login");
            packet.setValue("username", "Stan");

            Message message = Message.create(packet);
            serializer.writeObject("message", message, codec);

            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/channel", buffer);
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, DefaultProtocolConstants.PROTOCOL_MIME_TYPE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.writerIndex());
            channel.writeAndFlush(request);

            Object response = future.get();
            System.out.println(response);
        };

        practice(initializer, runner, false, ServerTransport.HTTP_TRANSPORT);
    }

    private static Initializer initializer(Serializer serializer, CompletableFuture<Object> future) {
        return (pipeline) -> pipeline.addLast(new HttpClientCodec(), new HttpContentDecompressor(), //
                new HttpObjectAggregator(1048576), inboundHandler(channelReader(serializer, future)));
    }

    private static ChannelReader<HttpObject> channelReader(Serializer serializer, CompletableFuture<Object> future) {
        return (ctx, object) -> {
            if (object instanceof HttpContent) {
                HttpContent content = (HttpContent) object;
                MemoryBuffer memoryBuffer = MemoryBufferFactory.create(content.content());
                DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

                boolean loggedIn = codec.readBoolean();

                Object response = serializer.readObject(codec);
                future.complete(response);
            }
        };
    }

}
