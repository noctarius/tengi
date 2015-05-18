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
package com.noctarius.tengi.server.impl.transport.tcp;

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.model.Packet;
import com.noctarius.tengi.server.ServerTransport;
import com.noctarius.tengi.server.impl.transport.AbstractStreamingTransportTestCase;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.packets.HandshakeRequest;
import com.noctarius.tengi.spi.connection.packets.HandshakeResponse;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TcpTransportTestCase
        extends AbstractStreamingTransportTestCase {

    @Test(timeout = 120000)
    public void test_tcp_transport()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Object> future = new CompletableFuture<>();
        Packet packet = new Packet("login");
        packet.setValue("username", "Stan");
        Message message = Message.create(packet);

        ChannelReader<ByteBuf> channelReader = (ctx, buffer) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            boolean loggedIn = codec.readBoolean();
            Identifier connectionId = codec.readObject();
            Object object = codec.readObject();
            if (loggedIn && object instanceof HandshakeResponse) {
                writeChannel(serializer, ctx, connectionId, message);
                return;
            }

            future.complete(object);
        };

        Initializer initializer = (pipeline) -> pipeline.addLast(inboundHandler(channelReader));

        Runner<Object> runner = (channel) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBytes("magic", DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER);
            codec.writeBoolean("loggedIn", false);
            codec.writeObject("handshake", new HandshakeRequest());
            channel.writeAndFlush(buffer);

            return future.get(120, TimeUnit.SECONDS);
        };

        Object response = AbstractStreamingTransportTestCase.practice(initializer, runner, false, ServerTransport.TCP_TRANSPORT);
        assertEquals(message, response);
    }

    @Test(timeout = 120000)
    public void test_tcp_transport_ping_pong()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Packet> future = new CompletableFuture<>();

        ChannelReader<ByteBuf> channelReader = (ctx, buffer) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            boolean loggedIn = codec.readBoolean();
            Identifier connectionId = codec.readObject();

            Object object = codec.readObject();
            if (object instanceof HandshakeResponse) {
                Packet packet = new Packet("pingpong");
                packet.setValue("counter", 1);
                Message message = Message.create(packet);
                writeChannel(serializer, ctx, connectionId, message);
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

                ctx.channel().writeAndFlush(buffer2);
            }
        };

        Initializer initializer = (pipeline) -> pipeline.addLast(inboundHandler(channelReader));

        Runner<Packet> runner = (channel) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBytes("magic", DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER);
            codec.writeBoolean("loggedIn", false);
            codec.writeObject("handshake", new HandshakeRequest());
            channel.writeAndFlush(buffer);

            return future.get(120, TimeUnit.SECONDS);
        };

        Packet response = practice(initializer, runner, false, ServerTransport.TCP_TRANSPORT);
        assertEquals(4, (int) response.getValue("counter"));
    }

    private static void writeChannel(Serializer serializer, ChannelHandlerContext ctx, Identifier connectionId, Object value)
            throws Exception {

        ByteBuf buffer = ctx.alloc().directBuffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", true);
            encoder.writeObject("connectionId", connectionId);
            serializer.writeObject("value", value, encoder);
        }
        ctx.channel().writeAndFlush(buffer);
    }

}
