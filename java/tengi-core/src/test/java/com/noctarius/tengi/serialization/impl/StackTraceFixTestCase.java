package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
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
import static org.junit.Assert.assertEquals;

public class StackTraceFixTestCase {

    @Test(expected = NullPointerException.class)
    public void testSerializationStackTraceFix()
            throws Exception {

        SerializationDebugger.Debugger.ENABLED = true;
        try {
            Protocol protocol = new DefaultProtocol(Collections.emptyList());
            Serializer serializer = Serializer.create(protocol);

            Packet packet = new Packet("Test");
            Packet innerPacket = new Packet("innerPacket");
            packet.setValue("innerPacket", innerPacket);
            Packet throwing = new SubPacketMarshallException("throwing");
            innerPacket.setValue("throwing", throwing);

            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

            serializer.writeObject(packet, memoryBuffer);
        } catch (NullPointerException e) {

            int serializationFrames = 0;

            Throwable throwable = e;
            do {
                StackTraceElement[] stackTrace = throwable.getStackTrace();
                for (int i = 0; i < stackTrace.length; i++) {
                    if (stackTrace[i].getMethodName().contains("[SERIALIZE => ")) {
                        serializationFrames++;
                    }
                }
            } while ((throwable = throwable.getCause()) != null);

            assertEquals(3, serializationFrames);
            throw e;

        } finally {
            SerializationDebugger.Debugger.ENABLED = false;
        }
    }

    @Test(expected = NullPointerException.class)
    public void testDeserializationStackTraceFix()
            throws Exception {

        SerializationDebugger.Debugger.ENABLED = true;
        try {
            Protocol protocol = new DefaultProtocol(Collections.emptyList());
            Serializer serializer = Serializer.create(protocol);

            Packet packet = new Packet("Test");
            Packet innerPacket = new Packet("innerPacket");
            packet.setValue("innerPacket", innerPacket);
            Packet throwing = new SubPacketUnmarshallException("throwing");
            innerPacket.setValue("throwing", throwing);

            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);

            serializer.writeObject(packet, memoryBuffer);

            serializer.readObject(memoryBuffer);

        } catch (NullPointerException e) {
            int serializationFrames = 0;

            Throwable throwable = e;
            do {
                StackTraceElement[] stackTrace = throwable.getStackTrace();
                for (int i = 0; i < stackTrace.length; i++) {
                    if (stackTrace[i].getMethodName().contains("[DESERIALIZE => ")) {
                        serializationFrames++;
                    }
                }
            } while (throwable == throwable.getCause() || (throwable = throwable.getCause()) != null);

            assertEquals(3, serializationFrames);
            throw e;

        } finally {
            SerializationDebugger.Debugger.ENABLED = false;
        }
    }

}
