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
package com.noctarius.tengi.server.impl.transport.websocket;

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.model.Packet;
import com.noctarius.tengi.server.ServerTransport;
import com.noctarius.tengi.server.impl.transport.AbstractStreamingTransportTestCase;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.impl.TransportConstants;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class WebsocketTransportTestCase
        extends AbstractStreamingTransportTestCase {

    @Test(timeout = 120000)
    public void test_websocket_transport()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Object> future = new CompletableFuture<>();
        Packet packet = new Packet("login");
        packet.setValue("username", "Stan");
        Message message = Message.create(packet);

        ChannelReader<WebsocketTestClient, ByteBuf> channelReader = (client, buffer) -> {
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

        Runner<Object, WebsocketTestClient> runner = (client) -> {
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

        Object response = practice(runner, clientFactory(channelReader), false, ServerTransport.WEBSOCKET_TRANSPORT);
        assertEquals(message, response);
    }

    @Test(timeout = 120000)
    public void test_websocket_transport_ping_pong()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Packet> future = new CompletableFuture<>();

        ChannelReader<WebsocketTestClient, ByteBuf> channelReader = (client, buffer) -> {
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

        Runner<Packet, WebsocketTestClient> runner = (client) -> {
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

        Packet response = practice(runner, clientFactory(channelReader), false, ServerTransport.WEBSOCKET_TRANSPORT);
        assertEquals(4, (int) response.getValue("counter"));
    }

    private static void writeChannel(Serializer serializer, WebsocketTestClient client, Identifier connectionId, Object value)
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

    private static ClientFactory<WebsocketTestClient> clientFactory(ChannelReader<WebsocketTestClient, ByteBuf> channelReader) {
        return (host, port, ssl, group) -> new WebsocketTestClient(host, port, ssl, channelReader);
    }

    @ClientEndpoint
    public static class WebsocketTestClient {

        private final Session session;
        private final ChannelReader<WebsocketTestClient, ByteBuf> channelReader;

        private WebsocketTestClient(String host, int port, boolean ssl, ChannelReader<WebsocketTestClient, ByteBuf> channelReader)
                throws Exception {

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, createURI(host, port, ssl));
            this.channelReader = channelReader;
        }

        private URI createURI(String host, int port, boolean ssl) {
            String url = (ssl ? "wss" : "ws") + "://" + host + ":" + port + TransportConstants.WEBSOCKET_RELATIVE_PATH;
            return URI.create(url);
        }

        @OnMessage
        public void onMessage(ByteBuffer byteBuffer)
                throws Exception {

            ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
            channelReader.channelRead(this, buffer);
        }

        private void sendMessage(ByteBuf buffer) {
            ByteBuffer nioBuffer = buffer.nioBuffer();
            if (buffer.isDirect()) {
                nioBuffer = Unpooled.copiedBuffer(buffer).nioBuffer();
            }
            session.getAsyncRemote().sendBinary(nioBuffer);
        }

        private void close()
                throws IOException {

            session.close();
        }
    }

}
