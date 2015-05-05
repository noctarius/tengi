package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.SystemException;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.Serializer;
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
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

        protocol.writeObject(packet, memoryBuffer);
        Packet read = protocol.readObject(memoryBuffer);

        assertEquals(packet, read);
    }

    @Test
    public void testSubclassWithDefaultConstructor()
            throws Exception {

        Protocol protocol = new DefaultProtocol(Collections.emptyList());

        Packet packet = new SubPacketWithDefaultConstructor();

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

        protocol.writeObject(packet, memoryBuffer);
        Packet read = protocol.readObject(memoryBuffer);

        assertEquals(packet, read);
    }

    @Test
    public void testSubclassWithoutDefaultConstructor()
            throws Exception {

        Protocol protocol = new DefaultProtocol(Collections.emptyList());

        Packet packet = new SubPacketWithoutDefaultConstructor("Test");

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

        protocol.writeObject(packet, memoryBuffer);
        Packet read = protocol.readObject(memoryBuffer);

        assertEquals(packet, read);
    }

    @Test(expected = SystemException.class)
    public void testSubclassMarshallException()
            throws Exception {

        SerializationDebugger.Debugger.ENABLED = true;
        try {
            Protocol protocol = new DefaultProtocol(Collections.emptyList());
            Serializer serializer = Serializer.create(protocol);

            Packet packet = new SubPacketMarshallException("Test");

            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

            serializer.writeObject(packet, memoryBuffer);
            Packet read = protocol.readObject(memoryBuffer);

            assertEquals(packet, read);
        } finally {
            SerializationDebugger.Debugger.ENABLED = false;
        }
    }

    @Test(expected = SystemException.class)
    public void testSubclassUnmarshallException()
            throws Exception {

        SerializationDebugger.Debugger.ENABLED = true;
        try {
            Protocol protocol = new DefaultProtocol(Collections.emptyList());
            Serializer serializer = Serializer.create(protocol);

            Packet packet = new SubPacketUnmarshallException("Test");

            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

            serializer.writeObject(packet, memoryBuffer);
            Packet read = protocol.readObject(memoryBuffer);

            assertEquals(packet, read);
        } finally {
            SerializationDebugger.Debugger.ENABLED = false;
        }
    }

}
