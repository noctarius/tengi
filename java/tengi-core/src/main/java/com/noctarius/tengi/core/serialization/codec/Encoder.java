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
package com.noctarius.tengi.core.serialization.codec;

import com.noctarius.tengi.spi.buffer.WritableMemoryBuffer;

/**
 * The <tt>Encoder</tt> interface defines the encoding part of the
 * {@link com.noctarius.tengi.spi.serialization.codec.Codec} contract. It provides
 * a set of write-methods to transform values into a byte-stream. The internal
 * implementation of the wire-protocol byte-stream depends on the chosen <tt>Codec</tt>
 * and {@link com.noctarius.tengi.spi.serialization.Protocol} implementation and
 * serialization and deserialization should not depend on implementation details.
 */
public interface Encoder {

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
     * <p>Transfers the content of a boolean to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the boolean value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBoolean(boolean value);

    /**
     * <p>Transfers the content of a boolean-array (or bitSet) to the underlying byte-stream buffer. A bitSet
     * is always written as a whole but the <tt>Encoder</tt> implementation is free to compact or compress
     * the values before writing, just as the default implementation does. Therefore the wire-protocol depends
     * on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param values the boolean-array (or bitSet) values to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeBitSet(boolean[] values);

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
     * <p>Transfers the content of an unsigned byte to the underlying byte-stream buffer. It is up to the
     * <tt>Encoder</tt> implementation how to write the value to the buffer. Therefore the wire-protocol
     * depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the unsigned byte value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeUnsignedByte(short value);

    /**
     * <p>Transfers the content of a short to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the short value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeShort(short value);

    /**
     * <p>Transfers the content of a char to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the char value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeChar(char value);

    /**
     * <p>Transfers the content of an int with 32 bits to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the int32 value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeInt32(int value);

    /**
     * <p>Transfers the content of an int with 32 bits to the underlying byte-stream buffer. Compared to
     * {@link #writeInt32(int)} this method writes the 32 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the int32 value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeCompressedInt32(int value);

    /**
     * <p>Transfers the content of an int with 64 bits to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the int64 value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeInt64(long value);

    /**
     * <p>Transfers the content of an int with 64 bits to the underlying byte-stream buffer. Compared to
     * {@link #writeInt64(long)} this method writes the 64 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the int64 value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeCompressedInt64(long value);

    /**
     * <p>Transfers the content of a float to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the float value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeFloat(float value);

    /**
     * <p>Transfers the content of a double to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the double value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeDouble(double value);

    /**
     * <p>Transfers the content of a string to the underlying byte-stream buffer. How the string content is
     * written to the stream depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation and might offer <tt>ASCII</tt> and <tt>UTF-8</tt> or other options but doesn't have to.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param value the double value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    void writeString(String value);

    /**
     * <p>Transfers the a <b>non-null</b> object to the underlying byte-stream buffer. The content itself will
     * be serialized using a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or as an internally handled object type. It is up to the
     * {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to tag the type of the
     * object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param object the object value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.NullPointerException      whenever the given object is null
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    void writeObject(Object object)
            throws Exception;

    /**
     * <p>Transfers the a <b>nullable</b> object to the underlying byte-stream buffer. A marker bit is written
     * to the stream to identify the object was <tt>null</tt> or actual object content is about to follow up.
     * The content itself will be serialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object
     * type. It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to
     * tag the type of the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param object the object value to be written to the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    void writeNullableObject(Object object)
            throws Exception;

    /**
     * <p>Transfers the content of a byte-array to the underlying byte-stream buffer. The byte-array
     * is written as a whole from begin to the end.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param bytes     the byte-array to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeBytes(String fieldName, byte[] bytes) {
        // TODO store field name information
        writeBytes(bytes);
    }

    /**
     * <p>Transfers the content of a byte-array to the underlying byte-stream buffer. The byte-array
     * is written as a whole beginning from the given <tt>offset</tt> and as many bytes as defined by
     * the given <tt>length</tt> will be transferred.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param bytes     the byte-array to be written to the buffer
     * @param offset    the offset to begin to read from
     * @param length    the number of bytes to write
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeBytes(String fieldName, byte[] bytes, int offset, int length) {
        // TODO store field name information
        writeBytes(bytes, offset, length);
    }

    /**
     * <p>Transfers the content of a boolean to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the boolean value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeBoolean(String fieldName, boolean value) {
        // TODO store field name information
        writeBoolean(value);
    }

    /**
     * <p>Transfers the content of a boolean-array (or bitSet) to the underlying byte-stream buffer. A bitSet
     * is always written as a whole but the <tt>Encoder</tt> implementation is free to compact or compress
     * the values before writing, just as the default implementation does. Therefore the wire-protocol depends
     * on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param values    the boolean-array (or bitSet) values to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeBitSet(String fieldName, boolean[] values) {
        // TODO store field name information
        writeBitSet(values);
    }

    /**
     * <p>Transfers the content of a byte to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the byte value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeByte(String fieldName, int value) {
        // TODO store field name information
        writeByte(value);
    }

    /**
     * <p>Transfers the content of an unsigned byte to the underlying byte-stream buffer. It is up to the
     * <tt>Encoder</tt> implementation how to write the value to the buffer. Therefore the wire-protocol
     * depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the unsigned byte value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeUnsignedByte(String fieldName, short value) {
        // TODO store field name information
        writeUnsignedByte(value);
    }

    /**
     * <p>Transfers the content of a short to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the short value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeShort(String fieldName, short value) {
        // TODO store field name information
        writeShort(value);
    }

    /**
     * <p>Transfers the content of a char to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the char value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeChar(String fieldName, char value) {
        // TODO store field name information
        writeChar(value);
    }

    /**
     * <p>Transfers the content of an int with 32 bits to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the int32 value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeInt32(String fieldName, int value) {
        // TODO store field name information
        writeInt32(value);
    }

    /**
     * <p>Transfers the content of an int with 32 bits to the underlying byte-stream buffer. Compared to
     * {@link #writeInt32(int)} this method writes the 32 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the int32 value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeCompressedInt32(String fieldName, int value) {
        // TODO store field name information
        writeCompressedInt32(value);
    }

    /**
     * <p>Transfers the content of an int with 64 bits to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the int64 value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeInt64(String fieldName, long value) {
        // TODO store field name information
        writeInt64(value);
    }

    /**
     * <p>Transfers the content of an int with 64 bits to the underlying byte-stream buffer. Compared to
     * {@link #writeInt64(long)} this method writes the 64 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the int64 value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeCompressedInt64(String fieldName, long value) {
        // TODO store field name information
        writeCompressedInt64(value);
    }

    /**
     * <p>Transfers the content of a float to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the float value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeFloat(String fieldName, float value) {
        // TODO store field name information
        writeFloat(value);
    }

    /**
     * <p>Transfers the content of a double to the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the double value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeDouble(String fieldName, double value) {
        // TODO store field name information
        writeDouble(value);
    }

    /**
     * <p>Transfers the content of a string to the underlying byte-stream buffer. How the string content is
     * written to the stream depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation and might offer <tt>ASCII</tt> and <tt>UTF-8</tt> or other options but doesn't have to.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param value     the double value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     */
    default void writeString(String fieldName, String value) {
        // TODO store field name information
        writeString(value);
    }

    /**
     * <p>Transfers the a <b>non-null</b> object to the underlying byte-stream buffer. The content itself will
     * be serialized using a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or as an internally handled object type. It is up to the
     * {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to tag the type of the
     * object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param object    the object value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.NullPointerException      whenever the given object is null
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default void writeObject(String fieldName, Object object)
            throws Exception {

        // TODO store field name information
        writeObject(object);
    }

    /**
     * <p>Transfers the a <b>nullable</b> object to the underlying byte-stream buffer. A marker bit is written
     * to the stream to identify the object was <tt>null</tt> or actual object content is about to follow up.
     * The content itself will be serialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object
     * type. It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to
     * tag the type of the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param object    the object value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default void writeNullableObject(String fieldName, Object object)
            throws Exception {

        // TODO store field name information
        writeNullableObject(object);
    }

    /**
     * Returns the internally stored {@link com.noctarius.tengi.spi.buffer.WritableMemoryBuffer} to store
     * raw data directly. <b>This is not recommended and should be avoided whenever possible!</b>
     *
     * @return the wrapped <tt>WritableMemoryBuffer</tt> instance
     */
    WritableMemoryBuffer getWritableMemoryBuffer();

}
