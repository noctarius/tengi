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
package com.noctarius.tengi.spi.buffer.impl;

import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.testing.AbstractTestCase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NettyMemoryBufferTestCase
        extends AbstractTestCase {

    @Test
    public void test_lock()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.lock();

        assertFalse(memoryBuffer.isReleasable());
        assertFalse(memoryBuffer.isReleased());
    }

    @Test
    public void test_release()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.lock();

        assertFalse(memoryBuffer.isReleasable());
        assertFalse(memoryBuffer.isReleased());

        memoryBuffer.release();
        memoryBuffer.release();

        assertTrue(memoryBuffer.isReleasable());
        assertTrue(memoryBuffer.isReleased());
    }

    @Test
    public void test_capacity()
            throws Exception {

        ByteBuf buffer = Unpooled.buffer(10);
        MemoryBuffer memoryBuffer = createMemoryBuffer(buffer);
        assertEquals(10, memoryBuffer.capacity());
    }

    @Test
    public void test_max_capacity()
            throws Exception {

        ByteBuf buffer = Unpooled.buffer(10);
        MemoryBuffer memoryBuffer = createMemoryBuffer(buffer);
        assertEquals(Integer.MAX_VALUE, memoryBuffer.maxCapacity());
    }

    @Test
    public void test_clear()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte(1);
        }

        assertEquals(10, memoryBuffer.writerIndex());
        memoryBuffer.clear();
        assertEquals(0, memoryBuffer.writerIndex());
    }

    @Test
    public void test_duplicate()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }

        MemoryBuffer duplicate = memoryBuffer.duplicate();
        for (int i = 0; i < 10; i++) {
            byte b1 = memoryBuffer.readByte();
            byte b2 = duplicate.readByte();
            assertEquals(b1, (byte) i);
            assertEquals(b1, b2);
        }
    }

    @Test
    public void test_readable()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        assertFalse(memoryBuffer.readable());
        memoryBuffer.writeByte(1);
        assertTrue(memoryBuffer.readable());
    }

    @Test
    public void test_readable_bytes()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        assertEquals(0, memoryBuffer.readableBytes());
        memoryBuffer.writeByte(1);
        assertEquals(1, memoryBuffer.readableBytes());
    }

    @Test
    public void test_boolean()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        memoryBuffer.writeBoolean(false);
        memoryBuffer.writeBoolean(true);
        assertEquals(false, memoryBuffer.readBoolean());
        assertEquals(true, memoryBuffer.readBoolean());
    }

    @Test
    public void test_byte()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        memoryBuffer.writeByte(5);
        memoryBuffer.writeByte(10);
        assertEquals(5, memoryBuffer.readByte());
        assertEquals(10, memoryBuffer.readByte());
    }

    @Test
    public void test_bytes_no_offset()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = new byte[5];
        data[0] = 10;
        data[1] = 20;
        data[2] = 30;
        data[3] = 40;
        data[4] = 50;
        memoryBuffer.writeBytes(data);

        byte[] result = new byte[5];
        memoryBuffer.readBytes(result);

        assertArrayEquals(data, result);
    }

    @Test
    public void test_bytes_offset_length()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] data = new byte[5];
        data[0] = 10;
        data[1] = 20;
        data[2] = 30;
        data[3] = 40;
        data[4] = 50;
        memoryBuffer.writeBytes(data, 1, 3);

        byte[] result = new byte[3];
        memoryBuffer.readBytes(result);

        byte[] expected = new byte[3];
        expected[0] = 20;
        expected[1] = 30;
        expected[2] = 40;

        assertArrayEquals(expected, result);
    }

    @Test
    public void test_write_buffer_array_bytebuffer()
            throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; i++) {
            byteBuffer.put((byte) i);
        }

        assertEquals(10, byteBuffer.position());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(byteBuffer);
        assertEquals(10, memoryBuffer.writerIndex());

        for (int i = 0; i < 10; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_array_bytebuffer()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuffer result = ByteBuffer.allocate(10);
        memoryBuffer.readBuffer(result);
        assertEquals(10, result.position());
    }

    @Test
    public void test_write_buffer_direct_bytebuffer()
            throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10);
        for (int i = 0; i < 10; i++) {
            byteBuffer.put((byte) i);
        }

        assertEquals(10, byteBuffer.position());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(byteBuffer);
        assertEquals(10, memoryBuffer.writerIndex());

        for (int i = 0; i < 10; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_direct_bytebuffer()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuffer result = ByteBuffer.allocateDirect(10);
        memoryBuffer.readBuffer(result);
        assertEquals(10, result.position());
    }

    @Test
    public void test_write_buffer_array_bytebuffer_offset_length()
            throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        for (int i = 0; i < 10; i++) {
            byteBuffer.put((byte) i);
        }

        assertEquals(10, byteBuffer.position());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(byteBuffer, 1, 8);
        assertEquals(8, memoryBuffer.writerIndex());

        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_array_bytebuffer_offset_length()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuffer result = ByteBuffer.allocate(8);
        memoryBuffer.readBuffer(result, 1, 8);

        result.flip();
        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, result.get());
        }
    }

    @Test
    public void test_write_buffer_direct_bytebuffer_offset_length()
            throws Exception {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10);
        for (int i = 0; i < 10; i++) {
            byteBuffer.put((byte) i);
        }

        assertEquals(10, byteBuffer.position());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(byteBuffer, 1, 8);
        assertEquals(8, memoryBuffer.writerIndex());

        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_direct_bytebuffer_offset_length()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuffer result = ByteBuffer.allocateDirect(8);
        memoryBuffer.readBuffer(result, 1, 8);

        result.flip();
        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, result.get());
        }
    }

    @Test
    public void test_write_buffer_array_memorybuffer()
            throws Exception {

        ByteBuf byteBuf = Unpooled.buffer();
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte((byte) i);
        }

        assertEquals(10, buffer.writerIndex());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(buffer);
        assertEquals(10, memoryBuffer.writerIndex());

        for (int i = 0; i < 10; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_array_memorybuffer()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuf byteBuf = Unpooled.buffer(10);
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        memoryBuffer.readBuffer(buffer);
        assertEquals(10, buffer.writerIndex());
    }

    @Test
    public void test_write_buffer_direct_memorybuffer()
            throws Exception {

        ByteBuf byteBuf = Unpooled.directBuffer();
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte((byte) i);
        }

        assertEquals(10, buffer.writerIndex());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(buffer);
        assertEquals(10, memoryBuffer.writerIndex());

        for (int i = 0; i < 10; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_direct_memorybuffer()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuf byteBuf = Unpooled.directBuffer(10);
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        memoryBuffer.readBuffer(buffer);
        assertEquals(10, buffer.writerIndex());
    }

    @Test
    public void test_write_buffer_array_memorybuffer_offset_length()
            throws Exception {

        ByteBuf byteBuf = Unpooled.buffer();
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte((byte) i);
        }

        assertEquals(10, buffer.writerIndex());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(buffer, 1, 8);
        assertEquals(8, memoryBuffer.writerIndex());

        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_array_memorybuffer_offset_length()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuf byteBuf = Unpooled.buffer(8);
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        memoryBuffer.readBuffer(buffer, 1, 8);

        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, buffer.readByte());
        }
    }

    @Test
    public void test_write_buffer_direct_memorybuffer_offset_length()
            throws Exception {

        ByteBuf byteBuf = Unpooled.directBuffer();
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte((byte) i);
        }

        assertEquals(10, buffer.writerIndex());

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeBuffer(buffer, 1, 8);
        assertEquals(8, memoryBuffer.writerIndex());

        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, memoryBuffer.readByte());
        }
    }

    @Test
    public void test_read_buffer_direct_memorybuffer_offset_length()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        for (int i = 0; i < 10; i++) {
            memoryBuffer.writeByte((byte) i);
        }
        assertEquals(10, memoryBuffer.writerIndex());

        ByteBuf byteBuf = Unpooled.directBuffer(8);
        MemoryBuffer buffer = createMemoryBuffer(byteBuf);
        memoryBuffer.readBuffer(buffer, 1, 8);

        for (int i = 1; i < 9; i++) {
            assertEquals((byte) i, buffer.readByte());
        }
    }

    @Test
    public void test_unsigned_byte()
            throws Exception {

        short value = 250;
        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeByte(value);
        assertEquals(1, memoryBuffer.writerIndex());
        short result = memoryBuffer.readUnsignedByte();
        assertEquals(value, result);
    }

    @Test
    public void test_read_reader_index()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeByte(1);

        assertEquals(0, memoryBuffer.readerIndex());
        memoryBuffer.readByte();
        assertEquals(1, memoryBuffer.readerIndex());
    }

    @Test
    public void test_set_reader_index()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        memoryBuffer.writeByte(1);

        assertEquals(0, memoryBuffer.readerIndex());
        memoryBuffer.readByte();
        assertEquals(1, memoryBuffer.readerIndex());
        memoryBuffer.readerIndex(0);
        assertEquals(0, memoryBuffer.readerIndex());
    }

    @Test
    public void test_writable()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        assertTrue(memoryBuffer.writable());
    }

    @Test
    public void test_writable_bytes()
            throws Exception {

        ByteBuf buffer = Unpooled.buffer(10);
        MemoryBuffer memoryBuffer = createMemoryBuffer(buffer);
        assertEquals(10, memoryBuffer.writableBytes());
    }

    @Test
    public void test_read_writer_index()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        assertEquals(0, memoryBuffer.writerIndex());
        memoryBuffer.writeByte(1);
        assertEquals(1, memoryBuffer.writerIndex());
    }

    @Test
    public void test_set_writer_index()
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        assertEquals(0, memoryBuffer.writerIndex());
        memoryBuffer.writeByte(1);
        assertEquals(1, memoryBuffer.writerIndex());
        memoryBuffer.writerIndex(0);
        assertEquals(0, memoryBuffer.writerIndex());
    }

}