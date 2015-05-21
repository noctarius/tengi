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

import com.noctarius.tengi.core.serialization.debugger.SerializationDebugger;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.spi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.codec.Codec;
import com.noctarius.tengi.spi.serialization.codec.impl.utf8.UTF8Codec;

public class DefaultCodec
        implements Codec {

    private final SerializationDebugger debugger = SerializationDebugger.create();

    private final Protocol protocol;

    private MemoryBuffer memoryBuffer;

    public DefaultCodec setMemoryBuffer(MemoryBuffer memoryBuffer) {
        this.memoryBuffer = memoryBuffer;
        return this;
    }

    public DefaultCodec(Protocol protocol) {
        this(protocol, null);
    }

    public DefaultCodec(Protocol protocol, MemoryBuffer memoryBuffer) {
        this.protocol = protocol;
        this.memoryBuffer = memoryBuffer;
    }

    @Override
    public void readBytes(byte[] bytes) {
        memoryBuffer.readBytes(bytes);
    }

    @Override
    public void readBytes(byte[] bytes, int offset, int length) {
        memoryBuffer.readBytes(bytes, offset, length);
    }

    @Override
    public boolean readBoolean() {
        return memoryBuffer.readBoolean();
    }

    @Override
    public boolean[] readBitSet() {
        return BitSetCompressor.readBitSet(memoryBuffer);
    }

    @Override
    public byte readByte() {
        return memoryBuffer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return memoryBuffer.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return ByteOrderUtils.getShort(memoryBuffer);
    }

    @Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public int readInt32() {
        return ByteOrderUtils.getInt(memoryBuffer);
    }

    @Override
    public int readCompressedInt32() {
        return Int32Compressor.readInt32(memoryBuffer);
    }

    @Override
    public long readInt64() {
        return ByteOrderUtils.getLong(memoryBuffer);
    }

    @Override
    public long readCompressedInt64() {
        return Int64Compressor.readInt64(memoryBuffer);
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt32());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readInt64());
    }

    @Override
    public String readString() {
        try {
            // TODO Pool buffers
            return UTF8Codec.readUTF(this, new byte[1024]);
        } catch (Exception e) {
            RuntimeException ex = new IndexOutOfBoundsException(e.getLocalizedMessage());
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        }
    }

    @Override
    public <O> O readObject()
            throws Exception {

        if (SerializationDebugger.Debugger.ENABLED) {
            debugger.push(protocol, this);
        }
        O object = protocol.readObject(this);
        if (SerializationDebugger.Debugger.ENABLED) {
            debugger.pop();
        }
        return object;
    }

    @Override
    public <O> O readNullableObject()
            throws Exception {

        return protocol.readNullable(this, (d, p) -> {
            if (SerializationDebugger.Debugger.ENABLED) {
                debugger.push(protocol, d);
            }
            O object = readObject();
            if (SerializationDebugger.Debugger.ENABLED) {
                debugger.pop();
            }
            return object;
        });
    }

    @Override
    public ReadableMemoryBuffer getReadableMemoryBuffer() {
        return memoryBuffer;
    }

    @Override
    public void writeBytes(byte[] bytes) {
        memoryBuffer.writeBytes(bytes);
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length) {
        memoryBuffer.writeBytes(bytes, offset, length);
    }

    @Override
    public void writeBoolean(boolean value) {
        memoryBuffer.writeBoolean(value);
    }

    @Override
    public void writeBitSet(boolean[] values) {
        BitSetCompressor.writeBitSet(values, memoryBuffer);
    }

    @Override
    public void writeByte(int value) {
        memoryBuffer.writeByte(value);
    }

    @Override
    public void writeUnsignedByte(short value) {
        memoryBuffer.writeUnsignedByte(value);
    }

    @Override
    public void writeShort(short value) {
        ByteOrderUtils.putShort(value, memoryBuffer);
    }

    @Override
    public void writeChar(char value) {
        writeShort((short) value);
    }

    @Override
    public void writeInt32(int value) {
        ByteOrderUtils.putInt(value, memoryBuffer);
    }

    @Override
    public void writeCompressedInt32(int value) {
        Int32Compressor.writeInt32(value, memoryBuffer);
    }

    @Override
    public void writeInt64(long value) {
        ByteOrderUtils.putLong(value, memoryBuffer);
    }

    @Override
    public void writeCompressedInt64(long value) {
        Int64Compressor.writeInt64(value, memoryBuffer);
    }

    @Override
    public void writeFloat(float value) {
        writeInt32(Float.floatToIntBits(value));
    }

    @Override
    public void writeDouble(double value) {
        writeInt64(Double.doubleToLongBits(value));
    }

    @Override
    public void writeString(String value) {
        try {
            // TODO Pool buffers
            UTF8Codec.writeUTF(this, value, new byte[1024]);
        } catch (Exception e) {
            RuntimeException ex = new IndexOutOfBoundsException(e.getLocalizedMessage());
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        }
    }

    @Override
    public void writeObject(Object object)
            throws Exception {

        if (SerializationDebugger.Debugger.ENABLED) {
            debugger.push(protocol, this, object);
        }
        protocol.writeObject("object", object, this);
        if (SerializationDebugger.Debugger.ENABLED) {
            debugger.pop();
        }
    }

    @Override
    public void writeNullableObject(Object object)
            throws Exception {

        protocol.writeNullable(object, this, (o, e, p) -> {
            if (SerializationDebugger.Debugger.ENABLED) {
                debugger.push(protocol, this, object);
            }
            writeObject("object", object);
            if (SerializationDebugger.Debugger.ENABLED) {
                debugger.pop();
            }
        });
    }

    @Override
    public WritableMemoryBuffer getWritableMemoryBuffer() {
        return memoryBuffer;
    }

}
