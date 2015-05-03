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
package com.noctarius.tengi.buffer.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.utils.UnsafeUtil;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

class NettyMemoryBuffer
        implements MemoryBuffer {

    private static final Unsafe UNSAFE = UnsafeUtil.UNSAFE;

    private static final long IDENTIFIER_DATA_OFFSET;

    static {
        if (!UnsafeUtil.UNSAFE_AVAILABLE) {
            throw new RuntimeException("Incompatible JVM - sun.misc.Unsafe support is missing");
        }

        try {
            Field identifierData = Identifier.class.getDeclaredField("data");
            identifierData.setAccessible(true);
            IDENTIFIER_DATA_OFFSET = UNSAFE.objectFieldOffset(identifierData);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException();
        }
    }

    private final AbstractReferenceCountedByteBuf buffer;
    private volatile boolean released = false;

    public NettyMemoryBuffer(AbstractReferenceCountedByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void lock() {
        buffer.retain();
    }

    @Override
    public void release() {
        if (buffer.release()) {
            released = true;
        }
    }

    @Override
    public boolean isReleased() {
        return released;
    }

    @Override
    public boolean isReleasable() {
        return buffer.refCnt() == 0;
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public int maxCapacity() {
        return buffer.maxCapacity();
    }

    @Override
    public void clear() {
        buffer.clear();
    }

    @Override
    public MemoryBuffer duplicate() {
        return null;
    }

    @Override
    public boolean readable() {
        return buffer.isReadable();
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }

    @Override
    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public int readBytes(byte[] bytes) {
        int length = Math.min(bytes.length, readableBytes());
        buffer.readBytes(bytes, 0, length);
        return length;
    }

    @Override
    public int readBytes(byte[] bytes, int offset, int length) {
        int realLength = Math.min(length, readableBytes());
        buffer.readBytes(bytes, 0, realLength);
        return realLength;
    }

    @Override
    public int readBuffer(ByteBuffer byteBuffer) {
        int remaining = Math.min(byteBuffer.remaining(), readableBytes());
        return readBuffer(byteBuffer, byteBuffer.position(), remaining);
    }

    @Override
    public int readBuffer(ByteBuffer byteBuffer, int offset, int length) {
        if (byteBuffer.hasArray()) {
            readBytes(byteBuffer.array(), offset, length);
        } else {
            for (int pos = offset; pos < offset + length; pos++) {
                byteBuffer.put(offset, readByte());
            }
        }
        return length;
    }

    @Override
    public int readBuffer(MemoryBuffer memoryBuffer) {
        int remaining = Math.min(memoryBuffer.writableBytes(), readableBytes());
        return readBuffer(memoryBuffer, memoryBuffer.writerIndex(), remaining);
    }

    @Override
    public int readBuffer(MemoryBuffer memoryBuffer, int offset, int length) {
        int realLength = Math.min(length, memoryBuffer.writableBytes());
        if (memoryBuffer instanceof NettyMemoryBuffer) {
            NettyMemoryBuffer mb = (NettyMemoryBuffer) memoryBuffer;
            ByteBuf other = mb.buffer;

            other.writeBytes(buffer, offset, realLength);

        } else {
            memoryBuffer.writerIndex(offset);
            for (int i = offset; i < offset + realLength; i++) {
                memoryBuffer.writeByte(readByte());
            }
        }
        return realLength;
    }

    @Override
    public short readUnsignedByte() {
        return buffer.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return ByteOrderUtils.getShort(this, true);
    }

    @Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public int readInt() {
        return ByteOrderUtils.getInt(this, true);
    }

    @Override
    public int readCompressedInt() {
        return Int32Compressor.readInt32(this);
    }

    @Override
    public long readLong() {
        return ByteOrderUtils.getLong(this, true);
    }

    @Override
    public long readCompressedLong() {
        return Int64Compressor.readInt64(this);
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readString() {
        return Unicode.UTF8toUTF16(this);
    }

    @Override
    public Identifier readIdentifier() {
        try {
            byte[] data = new byte[16];
            readBytes(data);
            return Identifier.fromBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error while de-serializing Identifier", e);
        }
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }

    @Override
    public void readerIndex(int readerIndex) {
        buffer.readerIndex(readerIndex);
    }

    @Override
    public boolean writable() {
        return buffer.isWritable();
    }

    @Override
    public void writeBoolean(boolean value) {
        buffer.writeBoolean(value);
    }

    @Override
    public void writeByte(int value) {
        buffer.writeByte(value);
    }

    @Override
    public int writableBytes() {
        return buffer.writableBytes();
    }

    @Override
    public void writeBytes(byte[] bytes) {
        buffer.writeBytes(bytes);
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length) {
        int realLength = Math.min(bytes.length, writableBytes());
        buffer.writeBytes(bytes, offset, realLength);
    }

    @Override
    public void writeBuffer(ByteBuffer byteBuffer) {
        int remaining = Math.min(byteBuffer.remaining(), writableBytes());
        writeBuffer(byteBuffer, byteBuffer.position(), remaining);
    }

    @Override
    public void writeBuffer(ByteBuffer byteBuffer, int offset, int length) {
        if (byteBuffer.hasArray()) {
            writeBytes(byteBuffer.array(), offset, length);
        } else {
            for (int pos = offset; pos < offset + length; pos++) {
                writeByte(byteBuffer.get(offset));
            }
        }
    }

    @Override
    public void writeBuffer(MemoryBuffer memoryBuffer) {
        int remaining = Math.min(memoryBuffer.readableBytes(), writableBytes());
        writeBuffer(memoryBuffer, memoryBuffer.readerIndex(), remaining);
    }

    @Override
    public void writeBuffer(MemoryBuffer memoryBuffer, int offset, int length) {
        int realLength = Math.min(length, memoryBuffer.readableBytes());
        if (memoryBuffer instanceof NettyMemoryBuffer) {
            NettyMemoryBuffer mb = (NettyMemoryBuffer) memoryBuffer;
            ByteBuf other = mb.buffer;

            buffer.writeBytes(other, offset, realLength);

        } else {
            memoryBuffer.readerIndex(offset);
            for (int i = offset; i < offset + realLength; i++) {
                writeByte(memoryBuffer.readByte());
            }
        }
    }

    @Override
    public void writeUnsignedByte(short value) {
        buffer.writeByte(value);
    }

    @Override
    public void writeShort(short value) {
        ByteOrderUtils.putShort(value, this, true);
    }

    @Override
    public void writeChar(char value) {
        writeShort((short) value);
    }

    @Override
    public void writeInt(int value) {
        ByteOrderUtils.putInt(value, this, true);
    }

    @Override
    public void writeCompressedInt(int value) {
        Int32Compressor.writeInt32(value, this);
    }

    @Override
    public void writeLong(long value) {
        ByteOrderUtils.putLong(value, this, true);
    }

    @Override
    public void writeCompressedLong(long value) {
        Int64Compressor.writeInt64(value, this);
    }

    @Override
    public void writeFloat(float value) {
        writeInt(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) {
        writeLong(Double.doubleToLongBits(value));
    }

    @Override
    public void writeString(String value) {
        Unicode.UTF16toUTF8(value, this);
    }

    @Override
    public void writeIdentifier(Identifier identifier) {
        try {
            byte[] data = (byte[]) UNSAFE.getObject(identifier, IDENTIFIER_DATA_OFFSET);
            writeBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing Identifier", e);
        }
    }

    @Override
    public <O> O readObject(Protocol protocol)
            throws Exception {

        return protocol.readObject(this);
    }

    @Override
    public void writeObject(Object object, Protocol protocol)
            throws Exception {

        protocol.writeObject(object, this);
    }

    @Override
    public int writerIndex() {
        return buffer.writerIndex();
    }

    @Override
    public void writerIndex(int writerIndex) {
        buffer.writerIndex(writerIndex);
    }

}
