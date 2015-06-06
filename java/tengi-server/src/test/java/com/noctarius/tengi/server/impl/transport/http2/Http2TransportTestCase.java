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

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.model.Packet;
import com.noctarius.tengi.server.ServerTransport;
import com.noctarius.tengi.server.impl.transport.AbstractStreamingTransportTestCase;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.FuturePromise;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class Http2TransportTestCase
        extends AbstractStreamingTransportTestCase {

    @Test(timeout = 120000)
    public void test_http2_transport()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Object> future = new CompletableFuture<>();
        Packet packet = new Packet("login");
        packet.setValue("username", "Stan");
        Message message = Message.create(packet);

        ChannelReader<Http2TestClient, ByteBuf> channelReader = (client, buffer) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            boolean loggedIn = codec.readBoolean();
            Identifier connectionId = codec.readObject();
            Object object = codec.readObject();
            if (loggedIn && object instanceof Handshake) {
                writeChannel(serializer, client, connectionId, message);
                return;
            }

            future.complete(object);
        };

        Runner<Object, Http2TestClient> runner = (client) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBoolean("loggedIn", false);
            codec.writeObject("handshake", new Handshake());
            client.sendMessage(buffer);

            Object result = future.get(120, TimeUnit.SECONDS);
            client.close();
            return result;
        };

        Object response = practice(runner, clientFactory(channelReader), false, ServerTransport.HTTP2_TRANSPORT);
        assertEquals(message, response);
    }

    @Test(timeout = 120000)
    public void test_http2_transport_ping_pong()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Packet> future = new CompletableFuture<>();

        ChannelReader<Http2TestClient, ByteBuf> channelReader = (client, buffer) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            boolean loggedIn = codec.readBoolean();
            Identifier connectionId = codec.readObject();

            Object object = codec.readObject();
            if (object instanceof Handshake) {
                Packet packet = new Packet("pingpong");
                packet.setValue("counter", 1);
                Message message = Message.create(packet);
                writeChannel(serializer, client, connectionId, message);
                return;
            }

            Message message = (Message) object;
            Packet packet = message.getBody();

            int counter = packet.getValue("counter");
            if (counter == 4) {
                future.complete(packet);
            } else {
                packet.setValue("counter", counter + 1);
                message = Message.create(packet);

                ByteBuf buffer2 = Unpooled.buffer();
                MemoryBuffer memoryBuffer2 = MemoryBufferFactory.create(buffer2);
                DefaultCodec codec2 = new DefaultCodec(serializer.getProtocol(), memoryBuffer2);

                codec2.writeBoolean("loggedIn", loggedIn);
                codec2.writeObject("connectionId", connectionId);
                serializer.writeObject("message", message, codec2);

                client.sendMessage(buffer2);
            }
        };

        Runner<Packet, Http2TestClient> runner = (client) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBoolean("loggedIn", false);
            codec.writeObject("handshake", new Handshake());
            client.sendMessage(buffer);

            Packet result = future.get(120, TimeUnit.SECONDS);
            client.close();
            return result;
        };

        Packet response = practice(runner, clientFactory(channelReader), false, ServerTransport.HTTP2_TRANSPORT);
        assertEquals(4, (int) response.getValue("counter"));
    }

    private static void writeChannel(Serializer serializer, Http2TestClient client, Identifier connectionId, Object value)
            throws Exception {

        ByteBuf buffer = Unpooled.directBuffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", true);
            encoder.writeObject("connectionId", connectionId);
            serializer.writeObject("value", value, encoder);
        }
        client.sendMessage(buffer);
    }

    private static ClientFactory<Http2TestClient> clientFactory(ChannelReader<Http2TestClient, ByteBuf> channelReader) {
        return (host, port, ssl, group) -> new Http2TestClient(host, port, ssl, channelReader);
    }

    public static class Http2TestClient {

        private final HTTP2Client client = new HTTP2Client();
        private final Session session;
        private final ChannelReader<Http2TestClient, ByteBuf> channelReader;
        private final HttpURI httpURI;

        private Http2TestClient(String host, int port, boolean ssl, ChannelReader<Http2TestClient, ByteBuf> channelReader)
                throws Exception {

            this.channelReader = channelReader;
            this.httpURI = new HttpURI(createURI(host, port, ssl));

            FuturePromise<Session> promise = new FuturePromise<>();
            client.start();
            client.connect(new InetSocketAddress(host, port), new Session.Listener.Adapter(), promise);
            this.session = promise.get();
        }

        private URI createURI(String host, int port, boolean ssl) {
            String url = (ssl ? "https" : "http") + "://" + host + ":" + port + "/channel";
            return URI.create(url);
        }

        private void sendMessage(ByteBuf buffer)
                throws Exception {

            ByteBuffer nioBuffer = buffer.nioBuffer();
            if (buffer.isDirect()) {
                nioBuffer = Unpooled.copiedBuffer(buffer).nioBuffer();
            }

            HttpFields requestFields = new HttpFields();
            requestFields.put(HttpHeader.CONTENT_TYPE, DefaultProtocolConstants.PROTOCOL_MIME_TYPE);

            MetaData.Request request = new MetaData.Request("PUT", httpURI, HttpVersion.HTTP_2, requestFields);
            HeadersFrame headersFrame = new HeadersFrame(0, request, null, false);

            Stream.Listener listener = new Stream.Listener.Adapter() {
                @Override
                public void onData(Stream stream, DataFrame frame, org.eclipse.jetty.util.Callback callback) {
                    ByteBuf buf = Unpooled.wrappedBuffer(frame.getData());
                    try {
                        channelReader.channelRead(Http2TestClient.this, buf);
                        callback.succeeded();
                    } catch (Exception e) {
                        callback.failed(e);
                    }
                }
            };

            FuturePromise<Stream> promise = new FuturePromise<>();
            session.newStream(headersFrame, promise, listener);

            Stream stream = promise.get();
            DataFrame requestContent = new DataFrame(stream.getId(), nioBuffer, true);
            stream.data(requestContent, org.eclipse.jetty.util.Callback.Adapter.INSTANCE);
        }

        private void close()
                throws Exception {

            client.stop();
        }
    }

}
