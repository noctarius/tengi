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
package com.noctarius.tengi.spi.serialization.impl;

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.debugger.SerializationDebugger;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.spi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.pooling.ObjectPool;
import com.noctarius.tengi.spi.pooling.PooledObject;
import com.noctarius.tengi.spi.pooling.impl.NonBlockingObjectPool;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.codec.impl.DefaultCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultSerializer
        implements Serializer {

    private final ObjectPool<DefaultCodec> codecPool;
    private final Protocol protocol;

    public DefaultSerializer(Protocol protocol) {
        this.protocol = protocol;
        this.codecPool = new NonBlockingObjectPool<>(new CodecObjectHandler(protocol), 100);
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public <O> O readObject(Decoder decoder)
            throws Exception {

        if (!SerializationDebugger.Debugger.ENABLED) {
            return decoder.readObject();

        } else {
            try {
                return decoder.readObject();

            } catch (Exception e) {
                SerializationDebugger debugger = SerializationDebugger.create();
                debugger.fixFramesToStackTrace(e);
                throw e;
            }
        }
    }

    @Override
    public <O> MemoryBuffer writeObject(O object)
            throws Exception {

        PooledObject<DefaultCodec> po = codecPool.acquire();
        try {
            if (!SerializationDebugger.Debugger.ENABLED) {
                ByteBuf buffer = Unpooled.buffer();
                MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
                DefaultCodec codec = po.getObject().setMemoryBuffer(memoryBuffer);
                writeObject("object", object, codec);
                return memoryBuffer;

            } else {
                try {
                    ByteBuf buffer = Unpooled.buffer();
                    MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
                    DefaultCodec codec = po.getObject().setMemoryBuffer(memoryBuffer);
                    writeObject("object", object, codec);
                    return memoryBuffer;

                } catch (Exception e) {
                    SerializationDebugger debugger = SerializationDebugger.create();
                    debugger.fixFramesToStackTrace(e);
                    throw e;
                }
            }
        } finally {
            codecPool.release(po);
        }
    }

    @Override
    public <O> void writeObject(String fieldName, O object, Encoder encoder)
            throws Exception {

        if (!SerializationDebugger.Debugger.ENABLED) {
            encoder.writeObject(fieldName, object);

        } else {
            try {
                encoder.writeObject(fieldName, object);

            } catch (Exception e) {
                SerializationDebugger debugger = SerializationDebugger.create();
                debugger.fixFramesToStackTrace(e);
                throw e;
            }
        }
    }

    @Override
    public AutoClosableEncoder retrieveEncoder(MemoryBuffer memoryBuffer) {
        PooledObject<DefaultCodec> pooledObject = codecPool.acquire((codec) -> codec.setMemoryBuffer(memoryBuffer));
        return new AutoClosableCodecDelegate(pooledObject);
    }

    @Override
    public AutoClosableDecoder retrieveDecoder(MemoryBuffer memoryBuffer) {
        PooledObject<DefaultCodec> pooledObject = codecPool.acquire((codec) -> codec.setMemoryBuffer(memoryBuffer));
        return new AutoClosableCodecDelegate(pooledObject);
    }

    private final class AutoClosableCodecDelegate
            implements AutoClosableDecoder, AutoClosableEncoder {

        private final PooledObject<DefaultCodec> pooledObject;
        private final DefaultCodec defaultCodec;

        private AutoClosableCodecDelegate(PooledObject<DefaultCodec> pooledObject) {
            this.pooledObject = pooledObject;
            this.defaultCodec = pooledObject.getObject();
        }

        @Override
        public int readBytes(byte[] bytes) {
            return defaultCodec.readBytes(bytes);
        }

        @Override
        public int readBytes(byte[] bytes, int offset, int length) {
            return defaultCodec.readBytes(bytes, offset, length);
        }

        @Override
        public boolean readBoolean() {
            return defaultCodec.readBoolean();
        }

        @Override
        public boolean[] readBitSet() {
            return defaultCodec.readBitSet();
        }

        @Override
        public byte readByte() {
            return defaultCodec.readByte();
        }

        @Override
        public short readUnsignedByte() {
            return defaultCodec.readUnsignedByte();
        }

        @Override
        public short readShort() {
            return defaultCodec.readShort();
        }

        @Override
        public char readChar() {
            return defaultCodec.readChar();
        }

        @Override
        public int readInt32() {
            return defaultCodec.readInt32();
        }

        @Override
        public int readCompressedInt32() {
            return defaultCodec.readCompressedInt32();
        }

        @Override
        public long readInt64() {
            return defaultCodec.readInt64();
        }

        @Override
        public long readCompressedInt64() {
            return defaultCodec.readCompressedInt64();
        }

        @Override
        public float readFloat() {
            return defaultCodec.readFloat();
        }

        @Override
        public double readDouble() {
            return defaultCodec.readDouble();
        }

        @Override
        public String readString() {
            return defaultCodec.readString();
        }

        @Override
        public <O> O readObject()
                throws Exception {

            return defaultCodec.readObject();
        }

        @Override
        public <O> O readNullableObject()
                throws Exception {

            return defaultCodec.readNullableObject();
        }

        @Override
        public ReadableMemoryBuffer getReadableMemoryBuffer() {
            return defaultCodec.getReadableMemoryBuffer();
        }

        @Override
        public void writeBytes(byte[] bytes) {
            defaultCodec.writeBytes(bytes);
        }

        @Override
        public void writeBytes(byte[] bytes, int offset, int length) {
            defaultCodec.writeBytes(bytes, offset, length);
        }

        @Override
        public void writeBoolean(boolean value) {
            defaultCodec.writeBoolean(value);
        }

        @Override
        public void writeBitSet(boolean[] values) {
            defaultCodec.writeBitSet(values);
        }

        @Override
        public void writeByte(int value) {
            defaultCodec.writeByte(value);
        }

        @Override
        public void writeUnsignedByte(short value) {
            defaultCodec.writeUnsignedByte(value);
        }

        @Override
        public void writeShort(short value) {
            defaultCodec.writeShort(value);
        }

        @Override
        public void writeChar(char value) {
            defaultCodec.writeChar(value);
        }

        @Override
        public void writeInt32(int value) {
            defaultCodec.writeInt32(value);
        }

        @Override
        public void writeCompressedInt32(int value) {
            defaultCodec.writeCompressedInt32(value);
        }

        @Override
        public void writeInt64(long value) {
            defaultCodec.writeInt64(value);
        }

        @Override
        public void writeCompressedInt64(long value) {
            defaultCodec.writeCompressedInt64(value);
        }

        @Override
        public void writeFloat(float value) {
            defaultCodec.writeFloat(value);
        }

        @Override
        public void writeDouble(double value) {
            defaultCodec.writeDouble(value);
        }

        @Override
        public void writeString(String value) {
            defaultCodec.writeString(value);
        }

        @Override
        public void writeObject(Object object)
                throws Exception {

            defaultCodec.writeObject(object);
        }

        @Override
        public void writeNullableObject(Object object)
                throws Exception {

            defaultCodec.writeNullableObject(object);
        }

        @Override
        public WritableMemoryBuffer getWritableMemoryBuffer() {
            return defaultCodec.getWritableMemoryBuffer();
        }

        @Override
        public void close()
                throws Exception {

            codecPool.release(pooledObject);
        }
    }

}
