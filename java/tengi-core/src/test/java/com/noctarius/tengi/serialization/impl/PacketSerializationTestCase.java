package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PacketSerializationTestCase {

    @Test
    public void testPacket()
            throws Exception {

        Protocol protocol = new DefaultProtocol(Collections.emptyList());

        Packet packet = new Packet("Test");

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer);

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
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer);

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
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer);

        protocol.writeObject(packet, memoryBuffer);
        Packet read = protocol.readObject(memoryBuffer);

        assertEquals(packet, read);
    }

    @TypeId(1000)
    public static class SubPacketWithDefaultConstructor
            extends Packet {

        public SubPacketWithDefaultConstructor() {
            super("SubPacketWithDefaultConstructor");
        }
    }

    @TypeId(1001)
    public static class SubPacketWithoutDefaultConstructor
            extends Packet {

        public SubPacketWithoutDefaultConstructor(String packageName) {
            super(packageName);
        }
    }

}
