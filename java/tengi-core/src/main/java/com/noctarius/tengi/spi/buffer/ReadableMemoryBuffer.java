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
 * The <tt>ReadableMemoryBuffer</tt> interface describes a buffer which
 * contains a content-stream to be read. Only basic data types can be
 * retrieved from this buffer. For more complex types, additional helpers
 * need to be used to read multi-byte types or values from the stream.
 */
public interface ReadableMemoryBuffer {

    /**
     * <p>Returns <tt>true</tt> if the buffer is readable, otherwise
     * <tt>false</tt>. Returning <tt>true</tt> means that the current
     * {@link #readerIndex()} value is smaller than the internal content size.</p>
     * <p>If this method returns <tt>true</tt>, than the returned value of
     * {@link #readableBytes()} must be greater than 0.</p>
     *
     * @return returns true if more bytes are available to read, otherwise false
     */
    boolean readable();

    /**
     * <p>Returns the number of upcoming bytes to be read. This value represents
     * the number of bytes between the current {@link #readerIndex()} and the
     * size of the stored content-stream.</p>
     * <p>If this method returns a value greater than 0, than the returned
     * value of {@link #readable()} must be <tt>true</tt>.</p>
     *
     * @return returns the number of readable (upcoming) bytes
     */
    int readableBytes();

    /**
     * <p>Transfers the content of a byte-array from the underlying byte-stream buffer. The byte-array
     * is read as a whole from begin to the end.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param bytes the byte-array to transfer the content to
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBytes(byte[] bytes);

    /**
     * <p>Transfers the content of a byte-array from the underlying byte-stream buffer. The byte-array
     * is read as a whole beginning from the given <tt>offset</tt> and will be filled with as many bytes
     * as defined by the given <tt>length</tt>.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param bytes  the byte-array to transfer the content to
     * @param offset the offset to begin to write to
     * @param length the number of bytes to write
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBytes(byte[] bytes, int offset, int length);

    /**
     * <p>Transfers the content of a {@link java.nio.ByteBuffer} from the underlying byte-stream buffer.
     * The <tt>ByteBuffer</tt> is read as a whole from current position to the end.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param byteBuffer the <tt>ByteBuffer</tt> to transfer the content to
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBuffer(ByteBuffer byteBuffer);

    /**
     * <p>Transfers the content of a {@link java.nio.ByteBuffer} from the underlying byte-stream buffer.
     * The <tt>ByteBuffer</tt> is read as a whole beginning from the given <tt>offset</tt> and will be
     * filled with as many bytes as defined by the given <tt>length</tt>.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param byteBuffer the <tt>ByteBuffer</tt> to transfer the content to
     * @param offset     the offset to begin to write to
     * @param length     the number of bytes to write
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBuffer(ByteBuffer byteBuffer, int offset, int length);

    /**
     * <p>Transfers the content of a {@link com.noctarius.tengi.spi.buffer.MemoryBuffer} from the
     * underlying byte-stream buffer. The <tt>MemoryBuffer</tt> is read as a whole from the current
     * writerIndex to the end.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to transfer the content to
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBuffer(MemoryBuffer memoryBuffer);

    /**
     * <p>Transfers the content of a {@link com.noctarius.tengi.spi.buffer.MemoryBuffer} from the
     * underlying byte-stream buffer. The <tt>ByteBuffer</tt> is read as a whole beginning from the given
     * <tt>offset</tt> and will be filled with as many bytes as defined by the given <tt>length</tt>.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to transfer the content to
     * @param offset       the offset to begin to write to
     * @param length       the number of bytes to write
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBuffer(MemoryBuffer memoryBuffer, int offset, int length);

    /**
     * <p>Reads the content of a byte from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the byte value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    byte readByte();

    /**
     * Returns the current read index position inside the buffer.
     *
     * @return the current read index position
     */
    int readerIndex();

    /**
     * Sets the read index position inside the buffer to the given value.
     *
     * @param readerIndex the new read index position to be set
     */
    void readerIndex(int readerIndex);

}
