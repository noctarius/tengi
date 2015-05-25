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
package com.noctarius.tengi.spi.serialization.codec.impl;

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.codec.impl.utf8.UTF8Codec;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;

public class DefaultCodecTestCase
        extends AbstractTestCase {

    @Test
    public void test_read_bytes()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = {(byte) 1, (byte) 2, (byte) 3, (byte) 4};
        memoryBuffer.writeBytes(data);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            byte[] result = new byte[4];
            decoder.readBytes("data", result);

            assertArrayEquals(data, result);
        }
    }

    @Test
    public void test_read_bytes_offset_length()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = {(byte) 1, (byte) 2, (byte) 3, (byte) 4};
        memoryBuffer.writeBytes(data);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            byte[] result = new byte[2];
            decoder.readBytes("data", result, 0, 2);

            assertEquals(data[0], result[0]);
            assertEquals(data[1], result[1]);
        }
    }

    @Test
    public void test_read_boolean()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = {(byte) 1, (byte) 0, (byte) 1};
        memoryBuffer.writeBytes(data);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(true, decoder.readBoolean("value"));
            assertEquals(false, decoder.readBoolean("value"));
            assertEquals(true, decoder.readBoolean("value"));
        }
    }

    @Test
    public void test_read_bit_set()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        memoryBuffer.writeByte(0x76);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            boolean[] values = decoder.readBitSet("values");
            assertEquals(false, values[0]);
            assertEquals(true, values[1]);
            assertEquals(true, values[2]);
        }
    }

    @Test
    public void test_read_byte()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = {(byte) 0x76};
        memoryBuffer.writeBytes(data);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(0x76, decoder.readByte("value"));
        }
    }

    @Test
    public void test_read_unsigned_byte()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        memoryBuffer.writeByte(200);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(200, decoder.readUnsignedByte("value"));
        }
    }

    @Test
    public void test_read_short()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putShort(Short.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Short.MAX_VALUE, decoder.readShort("value"));
        }
    }

    @Test
    public void test_read_char()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putShort((short) 'g', memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals('g', decoder.readChar("value"));
        }
    }

    @Test
    public void test_read_int32()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putInt(Integer.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Integer.MAX_VALUE, decoder.readInt32("value"));
        }
    }

    @Test
    public void test_read_compressed_int32()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        Int32Compressor.writeInt32(Integer.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Integer.MAX_VALUE, decoder.readCompressedInt32("value"));
        }
    }

    @Test
    public void test_read_int64()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putLong(Long.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Long.MAX_VALUE, decoder.readInt64("value"));
        }
    }

    @Test
    public void test_read_compressed_int64()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        Int64Compressor.writeInt64(Long.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Long.MAX_VALUE, decoder.readCompressedInt64("value"));
        }
    }

    @Test
    public void test_read_float()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putInt(Float.floatToRawIntBits(Float.MAX_VALUE), memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Float.MAX_VALUE, decoder.readFloat("value"), 0.01);
        }
    }

    @Test
    public void test_read_double()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putLong(Double.doubleToRawLongBits(Double.MAX_VALUE), memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals(Double.MAX_VALUE, decoder.readDouble("value"), 0.01);
        }
    }

    @Test
    public void test_read_string()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        Encoder encoder = Mockito.mock(Encoder.class);
        doAnswer((invocation) -> {
            Object[] arguments = invocation.getArguments();
            byte[] data = (byte[]) arguments[1];
            int offset = (Integer) arguments[2];
            int length = (Integer) arguments[3];
            memoryBuffer.writeBytes(data, offset, length);
            return null;
        }).when(encoder).writeBytes(anyString(), any(byte[].class), anyInt(), anyInt());

        doAnswer((invocation) -> {
            Object[] arguments = invocation.getArguments();
            int value = (Integer) arguments[1];
            ByteOrderUtils.putInt(value, memoryBuffer);
            return null;
        }).when(encoder).writeInt32(anyString(), anyInt());

        doAnswer((invocation) -> {
            Object[] arguments = invocation.getArguments();
            short value = (Short) arguments[1];
            ByteOrderUtils.putShort(value, memoryBuffer);
            return null;
        }).when(encoder).writeShort(anyString(), anyShort());

        UTF8Codec.writeUTF(encoder, "my-simple-string", new byte[1024]);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals("my-simple-string", decoder.readString("value"));
        }
    }

    @Test
    public void test_read_object()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        ByteOrderUtils.putShort(DefaultProtocolConstants.SERIALIZED_TYPE_INTEGER, memoryBuffer);
        ByteOrderUtils.putInt(Integer.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertEquals((Object) Integer.MAX_VALUE, decoder.readObject("value"));
        }
    }

    @Test
    public void test_read_nullable_object()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        // Null element
        memoryBuffer.writeByte(0);

        // Non null element
        memoryBuffer.writeByte(1);
        ByteOrderUtils.putShort(DefaultProtocolConstants.SERIALIZED_TYPE_INTEGER, memoryBuffer);
        ByteOrderUtils.putInt(Integer.MAX_VALUE, memoryBuffer);

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertNull(decoder.readNullableObject("value"));
            assertEquals((Object) Integer.MAX_VALUE, decoder.readNullableObject("value"));
        }
    }

    @Test
    public void test_get_readable_memory_buffer()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            assertSame(memoryBuffer, decoder.getReadableMemoryBuffer());
        }
    }

    @Test
    public void test_write_bytes()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = {(byte) 1, (byte) 2, (byte) 3, (byte) 4};
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBytes("data", data);
        }

        byte[] result = new byte[4];
        memoryBuffer.readBytes(result);
        assertArrayEquals(data, result);
    }

    @Test
    public void test_write_bytes_offset_length()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = {(byte) 1, (byte) 2, (byte) 3, (byte) 4};
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBytes("data", data, 2, 2);
        }

        byte[] result = new byte[2];
        memoryBuffer.readBytes(result);

        assertEquals(data[2], result[0]);
        assertEquals(data[3], result[1]);
    }

    @Test
    public void test_write_boolean()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("value", true);
            encoder.writeBoolean("value", false);
            encoder.writeBoolean("value", true);
        }

        assertEquals(1, memoryBuffer.readByte());
        assertEquals(0, memoryBuffer.readByte());
        assertEquals(1, memoryBuffer.readByte());
    }

    @Test
    public void test_write_bit_set()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            boolean[] values = {false, true, true};
            encoder.writeBitSet("values", values);
        }

        byte result = memoryBuffer.readByte();
        assertEquals(0x76, result);
    }

    @Test
    public void test_write_byte()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeByte("value", 0x76);
        }

        byte result = memoryBuffer.readByte();
        assertEquals(0x76, result);
    }

    @Test
    public void test_write_unsigned_byte()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeUnsignedByte("value", (short) 255);
        }

        byte result = memoryBuffer.readByte();
        assertEquals(255, result & 0xFF);
    }

    @Test
    public void test_write_short()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeShort("value", Short.MAX_VALUE);
        }

        short result = ByteOrderUtils.getShort(memoryBuffer);
        assertEquals(Short.MAX_VALUE, result);
    }

    @Test
    public void test_write_char()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeChar("value", 'g');
        }

        short result = ByteOrderUtils.getShort(memoryBuffer);
        assertEquals('g', (char) result);
    }

    @Test
    public void test_write_int32()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeInt32("value", Integer.MAX_VALUE);
        }

        int result = ByteOrderUtils.getInt(memoryBuffer);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void test_write_compressed_int32()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeCompressedInt32("value", Integer.MAX_VALUE);
        }

        int result = Int32Compressor.readInt32(memoryBuffer);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    public void test_write_int64()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeInt64("value", Long.MAX_VALUE);
        }

        long result = ByteOrderUtils.getLong(memoryBuffer);
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void test_write_compressed_int64()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeCompressedInt64("value", Long.MAX_VALUE);
        }

        long result = Int64Compressor.readInt64(memoryBuffer);
        assertEquals(Long.MAX_VALUE, result);
    }

    @Test
    public void test_write_float()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeFloat("value", Float.MAX_VALUE);
        }

        float result = Float.intBitsToFloat(ByteOrderUtils.getInt(memoryBuffer));
        assertEquals(Float.MAX_VALUE, result, 0.01);
    }

    @Test
    public void test_write_double()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeDouble("value", Double.MAX_VALUE);
        }

        double result = Double.longBitsToDouble(ByteOrderUtils.getLong(memoryBuffer));
        assertEquals(Double.MAX_VALUE, result, 0.01);
    }

    @Test
    public void test_write_string()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeString("value", "some-simple-string");
        }

        Decoder decoder = Mockito.mock(Decoder.class);
        doAnswer((invocation) -> {
            Object[] arguments = invocation.getArguments();
            byte[] data = (byte[]) arguments[1];
            int offset = (Integer) arguments[2];
            int length = (Integer) arguments[3];
            memoryBuffer.readBytes(data, offset, length);
            return null;
        }).when(decoder).readBytes(anyString(), any(byte[].class), anyInt(), anyInt());

        doAnswer((invocation) -> ByteOrderUtils.getInt(memoryBuffer)).when(decoder).readInt32(anyString());

        doAnswer((invocation) -> ByteOrderUtils.getShort(memoryBuffer)).when(decoder).readShort(anyString());

        String result = UTF8Codec.readUTF(decoder, new byte[1024]);
        assertEquals("some-simple-string", result);
    }

    @Test
    public void test_write_object()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeObject("value", Integer.MAX_VALUE);
        }

        assertEquals(DefaultProtocolConstants.SERIALIZED_TYPE_INTEGER, ByteOrderUtils.getShort(memoryBuffer));
        assertEquals(Integer.MAX_VALUE, ByteOrderUtils.getInt(memoryBuffer));
    }

    @Test
    public void test_write_nullable_object()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeNullableObject("value", null);
            encoder.writeNullableObject("value", Integer.MAX_VALUE);
        }

        // Null element
        assertEquals(0, memoryBuffer.readByte());

        // Non null element
        assertEquals(1, memoryBuffer.readByte());
        assertEquals(DefaultProtocolConstants.SERIALIZED_TYPE_INTEGER, ByteOrderUtils.getShort(memoryBuffer));
        assertEquals(Integer.MAX_VALUE, ByteOrderUtils.getInt(memoryBuffer));
    }

    @Test
    public void test_get_writable_memory_buffer()
            throws Exception {

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            assertSame(memoryBuffer, encoder.getWritableMemoryBuffer());
        }
    }

}