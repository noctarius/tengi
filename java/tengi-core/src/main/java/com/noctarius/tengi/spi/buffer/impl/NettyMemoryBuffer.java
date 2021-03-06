/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
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
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

class NettyMemoryBuffer
        implements MemoryBuffer {

    private ByteBuf buffer;

    private volatile boolean released = false;

    MemoryBuffer setByteBuf(ByteBuf buffer) {
        this.buffer = buffer;
        return this;
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
        return new NettyMemoryBuffer().setByteBuf(buffer.duplicate());
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
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public void readBytes(byte[] bytes) {
        buffer.readBytes(bytes, 0, bytes.length);
    }

    @Override
    public void readBytes(byte[] bytes, int offset, int length) {
        buffer.readBytes(bytes, offset, length);
    }

    @Override
    public void readBuffer(ByteBuffer byteBuffer) {
        int remaining = Math.min(byteBuffer.remaining(), readableBytes());
        readBuffer(byteBuffer, byteBuffer.position(), remaining);
    }

    @Override
    public void readBuffer(ByteBuffer byteBuffer, int offset, int length) {
        int realLength = Math.min(byteBuffer.remaining(), readableBytes());
        if (byteBuffer.hasArray()) {
            int readerIndex = buffer.readerIndex();
            buffer.readerIndex(offset);
            readBytes(byteBuffer.array(), 0, realLength);
            buffer.readerIndex(readerIndex);
            byteBuffer.position(realLength);
        } else {
            for (int pos = offset; pos < offset + length; pos++) {
                byteBuffer.put(buffer.getByte(pos));
            }
        }
    }

    @Override
    public void readBuffer(MemoryBuffer memoryBuffer) {
        int remaining = Math.min(memoryBuffer.writableBytes(), readableBytes());
        readBuffer(memoryBuffer, memoryBuffer.writerIndex(), remaining);
    }

    @Override
    public void readBuffer(MemoryBuffer memoryBuffer, int offset, int length) {
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
        buffer.writeBytes(bytes, offset, length);
    }

    @Override
    public void writeBuffer(ByteBuffer byteBuffer) {
        int remaining = Math.min(byteBuffer.position(), writableBytes());
        writeBuffer(byteBuffer, 0, remaining);
    }

    @Override
    public void writeBuffer(ByteBuffer byteBuffer, int offset, int length) {
        if (byteBuffer.hasArray()) {
            writeBytes(byteBuffer.array(), offset, length);
        } else {
            for (int pos = offset; pos < offset + length; pos++) {
                writeByte(byteBuffer.get(pos));
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

            /*if (buffer instanceof CompositeByteBuf) {
                CompositeByteBuf cbb = (CompositeByteBuf) buffer;
                if (cbb.numComponents() < cbb.maxNumComponents()) {
                    cbb.addComponent(buffer);
                    return;
                }
            }*/
            buffer.writeBytes(other, offset, realLength);

        } else {
            memoryBuffer.readerIndex(offset);
            for (int i = offset; i < offset + realLength; i++) {
                writeByte(memoryBuffer.readByte());
            }
        }
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
