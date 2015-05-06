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

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.debugger.SerializationDebugger;
import com.noctarius.tengi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class TcpTransportTestCase
        extends AbstractTransportTestCase {

    @Test
    public void testTcpTransport()
            throws Exception {

        InputStream is = getClass().getResourceAsStream("transport.types.manifest");
        Serializer serializer = Serializer.create(new DefaultProtocol(is, Collections.emptyList()));

        CompletableFuture<Object> future = new CompletableFuture<>();

        Initializer initializer = initializer(serializer, future);
        Runner runner = (channel) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, serializer.getProtocol());

            memoryBuffer.writeBytes(DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER);
            memoryBuffer.writeBoolean(false);

            Packet packet = new Packet("login");
            packet.setValue("username", "Stan");

            Message message = Message.create(packet);
            serializer.writeObject(message, memoryBuffer);

            channel.writeAndFlush(buffer);

            Object response = future.get();
            assertEquals(message, response);
        };

        practice(initializer, runner, false, ServerTransport.TCP_TRANSPORT);
    }

    private static Initializer initializer(Serializer serializer, CompletableFuture<Object> future) {
        return (pipeline) -> pipeline.addLast(inboundHandler(channelReader(serializer, future)));
    }

    private static ChannelReader<ByteBuf> channelReader(Serializer serializer, CompletableFuture<Object> future) {
        return (ctx, object) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(object, serializer.getProtocol());

            boolean loggedIn = memoryBuffer.readBoolean();
            Identifier connectionId = memoryBuffer.readObject();

            Object response = serializer.readObject(memoryBuffer);
            future.complete(response);
        };
    }

}
