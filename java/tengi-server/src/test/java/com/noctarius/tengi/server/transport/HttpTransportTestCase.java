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

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.connection.impl.HandshakeRequest;
import com.noctarius.tengi.connection.impl.HandshakeResponse;
import com.noctarius.tengi.connection.impl.LongPollingRequest;
import com.noctarius.tengi.connection.impl.LongPollingResponse;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.Test;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HttpTransportTestCase
        extends AbstractLongPollingTransportTestCase {

    @Test
    public void testHttpTransport()
            throws Exception {

        InputStream is = getClass().getResourceAsStream("transport.types.manifest");
        Serializer serializer = Serializer.create(new DefaultProtocol(is, Collections.emptyList()));

        Packet packet = new Packet("login");
        packet.setValue("username", "Stan");
        Message message = Message.create(packet);

        CompletableFuture<Object> future = new CompletableFuture<>();

        Object result = practice(() -> {
            AsyncHttpClient client = new AsyncHttpClient();
            Response response = handshake(client, serializer).get();

            Identifier connectionId = null;
            try (AutoClosableDecoder decoder = decodeResponse(serializer, response)) {
                if (decoder.readBoolean()) {
                    connectionId = decoder.readObject();
                }
                if (!(decoder.readObject() instanceof HandshakeResponse)) {
                    fail("No HandshakeResponse received");
                }
            }

            AtomicBoolean stop = new AtomicBoolean(false);
            ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
            startLongPolling(client, serializer, connectionId, ses, stop, (m) -> {
                stop.set(true);
                System.out.println(m);
                future.complete(m);
            });

            writeRequest(client, serializer, connectionId, message, null);

            return future.get();
        }, false, ServerTransport.HTTP_TRANSPORT);

        assertEquals(message, result);
    }

    private static AutoClosableDecoder decodeResponse(Serializer serializer, Response response)
            throws Exception {

        ByteBuffer byteBuffer = response.getResponseBodyAsByteBuffer();
        ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        return serializer.retrieveDecoder(memoryBuffer);
    }

    private static ListenableFuture<Response> handshake(AsyncHttpClient client, Serializer serializer)
            throws Exception {

        return writeRequest(client, serializer, null, new HandshakeRequest(), null);
    }

    private static ListenableFuture<Response> writeRequest(AsyncHttpClient client, Serializer serializer, //
                                                           Identifier connectionId, Object value, //
                                                           Handler<Object> handler)
            throws Exception {

        AsyncHttpClient.BoundRequestBuilder request = client.preparePost("http://localhost:8080/channel");

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", connectionId != null);
            if (connectionId != null) {
                encoder.writeObject("connectionId", connectionId);
            }
            encoder.writeObject("value", value);
        }

        byte[] data = buffer.array();
        request.addHeader(HttpHeaderNames.CONTENT_TYPE.toString(), DefaultProtocolConstants.PROTOCOL_MIME_TYPE);
        request.addHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), String.valueOf(data.length));
        request.setBody(data);

        if (handler != null) {
            return request.execute(handler(client, handler, serializer));
        }
        return request.execute();
    }

    private static void startLongPolling(AsyncHttpClient client, Serializer serializer, Identifier connectionId, //
                                         ScheduledExecutorService ses, AtomicBoolean stop, Handler<Message> messageHandler)
            throws Exception {

        Message longPollingMessage = Message.create(new LongPollingRequest());
        Handler<Object> handler = createLongPollingHandler(client, serializer, connectionId, ses, stop, messageHandler);
        writeRequest(client, serializer, connectionId, longPollingMessage, handler);
    }

    private static Handler<Object> createLongPollingHandler(AsyncHttpClient client, Serializer serializer, //
                                                            Identifier connectionId, ScheduledExecutorService ses,  //
                                                            AtomicBoolean stop, Handler<Message> messageHandler) {
        return (object) -> {
            if (stop.get()) {
                return;
            }

            Message response = (Message) object;
            Object body = response.getBody();
            if (body instanceof LongPollingResponse) {
                ((LongPollingResponse) body).getMessages().forEach(messageHandler::handle);
            }

            if (stop.get()) {
                return;
            }

            ses.schedule(() -> {
                startLongPolling(client, serializer, connectionId, ses, stop, messageHandler);
                return null;
            }, 100, TimeUnit.MILLISECONDS);
        };
    }

    private static AsyncCompletionHandler<Response> handler(AsyncHttpClient client, Handler handler, Serializer serializer) {
        return new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response)
                    throws Exception {

                Identifier connectionId;
                try (AutoClosableDecoder decoder = decodeResponse(serializer, response)) {
                    if (!decoder.readBoolean()) {
                        throw new AssertionError();
                    }
                    connectionId = decoder.readObject();
                    Object object = decoder.readObject();
                    handler.handle(object);
                }

                Message message = Message.create(new LongPollingRequest());
                writeRequest(client, serializer, connectionId, message, handler);
                return response;
            }
        };
    }

    private static interface Handler<T> {
        void handle(T object);
    }

}
