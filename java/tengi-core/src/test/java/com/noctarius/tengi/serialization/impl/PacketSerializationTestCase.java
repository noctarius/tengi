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
package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.Codec;
import com.noctarius.tengi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.serialization.debugger.SerializationDebugger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Collections;

import static com.noctarius.tengi.serialization.impl.SerializationClasses.SubPacketMarshallException;
import static com.noctarius.tengi.serialization.impl.SerializationClasses.SubPacketUnmarshallException;
import static com.noctarius.tengi.serialization.impl.SerializationClasses.SubPacketWithDefaultConstructor;
import static com.noctarius.tengi.serialization.impl.SerializationClasses.SubPacketWithoutDefaultConstructor;
import static org.junit.Assert.assertEquals;

public class PacketSerializationTestCase {

    @Test
    public void testPacket()
            throws Exception {

        Protocol protocol = new DefaultProtocol(Collections.emptyList());

        Packet packet = new Packet("Test");

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);

        Codec codec = new DefaultCodec(protocol, memoryBuffer);
        protocol.writeObject("packet", packet, codec);
        Packet read = protocol.readObject(codec);

        assertEquals(packet, read);
    }

    @Test
    public void testSubclassWithDefaultConstructor()
            throws Exception {

        Protocol protocol = new DefaultProtocol(Collections.emptyList());

        Packet packet = new SubPacketWithDefaultConstructor();

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);

        Codec codec = new DefaultCodec(protocol, memoryBuffer);
        protocol.writeObject("packet", packet, codec);
        Packet read = protocol.readObject(codec);

        assertEquals(packet, read);
    }

    @Test
    public void testSubclassWithoutDefaultConstructor()
            throws Exception {

        Protocol protocol = new DefaultProtocol(Collections.emptyList());

        Packet packet = new SubPacketWithoutDefaultConstructor("Test");

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);

        Codec codec = new DefaultCodec(protocol, memoryBuffer);
        protocol.writeObject("packet", packet, codec);
        Packet read = protocol.readObject(codec);

        assertEquals(packet, read);
    }

    @Test(expected = NullPointerException.class)
    public void testSubclassMarshallException()
            throws Exception {

        SerializationDebugger.Debugger.ENABLED = true;
        try {
            Protocol protocol = new DefaultProtocol(Collections.emptyList());
            Serializer serializer = Serializer.create(protocol);

            Packet packet = new SubPacketMarshallException("Test");

            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);

            Codec codec = new DefaultCodec(protocol, memoryBuffer);
            serializer.writeObject("packet", packet, codec);

        } finally {
            SerializationDebugger.Debugger.ENABLED = false;
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSubclassUnmarshallException()
            throws Exception {

        SerializationDebugger.Debugger.ENABLED = true;
        try {
            Protocol protocol = new DefaultProtocol(Collections.emptyList());
            Serializer serializer = Serializer.create(protocol);

            Packet packet = new SubPacketUnmarshallException("Test");

            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);

            Codec codec = new DefaultCodec(protocol, memoryBuffer);
            serializer.writeObject("packet", packet, codec);
            Packet read = protocol.readObject(codec);

            assertEquals(packet, read);
        } finally {
            SerializationDebugger.Debugger.ENABLED = false;
        }
    }

}
