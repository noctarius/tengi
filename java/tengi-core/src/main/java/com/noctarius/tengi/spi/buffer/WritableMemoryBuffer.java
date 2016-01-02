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
package com.noctarius.tengi.spi.buffer;

import java.nio.ByteBuffer;

/**
 * The <tt>WritableMemoryBuffer</tt> interface describes a buffer which
 * contains a content-stream to be written to. Only basic data types can be
 * written to this buffer directly. For more complex types, additional helpers
 * need to be used to write multi-byte types or values to the stream.
 */
public interface WritableMemoryBuffer {

    /**
     * <p>Returns <tt>true</tt> if the buffer is writable, otherwise
     * <tt>false</tt>. Returning <tt>true</tt> means that the current
     * {@link #writerIndex()} ()} value is smaller than the internal maximal capacity.</p>
     * <p>If this method returns <tt>true</tt>, than the returned value of
     * {@link #writableBytes()} ()} must be greater than 0.</p>
     *
     * @return returns true if more bytes are available to write, otherwise false
     */
    boolean writable();

    /**
     * <p>Returns the number of upcoming bytes to write. This value represents
     * the number of bytes between the current {@link #writerIndex()} ()} and
     * the maximum capacity of the content-stream.</p>
     * <p>If this method returns a value greater than 0, than the returned
     * value of {@link #writable()} ()} must be <tt>true</tt>.</p>
     *
     * @return returns the number of writable (upcoming) bytes
     */
    int writableBytes();

    /**
     * <p>Transfers the content of a byte-array to the underlying byte-stream buffer. The byte-array
     * is written as a whole from begin to the end.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param bytes the byte-array to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBytes(byte[] bytes);

    /**
     * <p>Transfers the content of a byte-array to the underlying byte-stream buffer. The byte-array
     * is written as a whole beginning from the given <tt>offset</tt> and as many bytes as defined by
     * the given <tt>length</tt> will be transferred.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param bytes  the byte-array to be written to the buffer
     * @param offset the offset to begin to read from
     * @param length the number of bytes to write
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBytes(byte[] bytes, int offset, int length);

    /**
     * <p>Transfers the content of a {@link java.nio.ByteBuffer} to the underlying byte-stream buffer. The
     * <tt>ByteBuffer</tt> is written as a whole from begin to the end.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param byteBuffer the <tt>ByteBuffer</tt> to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBuffer(ByteBuffer byteBuffer);

    /**
     * <p>Transfers the content of a {@link java.nio.ByteBuffer} to the underlying byte-stream buffer. The
     * <tt>ByteBuffer</tt> is written as a whole beginning from the given <tt>offset</tt> and as many bytes
     * as defined by the given <tt>length</tt> will be transferred.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param byteBuffer the <tt>ByteBuffer</tt> to be written to the buffer
     * @param offset     the offset to begin to read from
     * @param length     the number of bytes to write
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBuffer(ByteBuffer byteBuffer, int offset, int length);

    /**
     * <p>Transfers the content of a {@link com.noctarius.tengi.spi.buffer.MemoryBuffer} to the underlying
     * byte-stream buffer. The <tt>MemoryBuffer</tt> is written as a whole from begin to the end.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBuffer(MemoryBuffer memoryBuffer);

    /**
     * <p>Transfers the content of a {@link com.noctarius.tengi.spi.buffer.MemoryBuffer} to the underlying
     * byte-stream buffer. The <tt>MemoryBuffer</tt> is written as a whole beginning from the given
     * <tt>offset</tt> and as many bytes as defined by the given <tt>length</tt> will be transferred.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to be written to the buffer
     * @param offset       the offset to begin to read from
     * @param length       the number of bytes to write
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBuffer(MemoryBuffer memoryBuffer, int offset, int length);

    /**
     * <p>Transfers the content of a byte to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the byte value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeByte(int value);

    /**
     * Returns the current write index position inside the buffer.
     *
     * @return the current write index position
     */
    int writerIndex();

    /**
     * Sets the write index position inside the buffer to the given value.
     *
     * @param writerIndex the new write index position to be set
     */
    void writerIndex(int writerIndex);

}
