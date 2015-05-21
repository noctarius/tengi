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

import com.noctarius.tengi.spi.buffer.ReadableMemoryBuffer;

/**
 * The <tt>Decoder</tt> interface defines the decoding part of the
 * {@link com.noctarius.tengi.spi.serialization.codec.Codec} contract. It provides
 * a set of read-methods to transform a byte-stream into values. The internal
 * implementation of the wire-protocol byte-stream depends on the chosen <tt>Codec</tt>
 * and {@link com.noctarius.tengi.spi.serialization.Protocol} implementation and
 * serialization and deserialization should not depend on implementation details.
 */
public interface Decoder {

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
     * @param bytes the byte-array to transfer the content to
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    void readBytes(byte[] bytes, int offset, int length);

    /**
     * <p>Reads the content of a boolean from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the boolean value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    boolean readBoolean();

    /**
     * <p>Reads the content of a boolean-array (or bitSet) from the underlying byte-stream buffer. A bitSet
     * is always read as a whole but the <tt>Encoder</tt> implementation was free to compact or compress
     * the values before writing, just as the default implementation does. Therefore the wire-protocol depends
     * on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the boolean-array (or bitSet) value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    boolean[] readBitSet();

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
     * <p>Reads the content of an unsigned byte from the underlying byte-stream buffer. It was up to the
     * <tt>Encoder</tt> implementation how to write the value to the buffer. Therefore the wire-protocol depends
     * on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the unsigned byte value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    short readUnsignedByte();

    /**
     * <p>Reads the content of a short from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the short value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    short readShort();

    /**
     * <p>Reads the content of a char from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the char value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    char readChar();

    /**
     * <p>Reads the content of an int value with 32 bits from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the int32 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    int readInt32();

    /**
     * <p>Reads the content of an int value with 32 bits from the underlying byte-stream buffer. Compared to
     * {@link #readInt32()} this method reads the 32 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the int32 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    int readCompressedInt32();

    /**
     * <p>Reads the content of an int value with 64 bits from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the int64 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    long readInt64();

    /**
     * <p>Reads the content of an int value with 64 bits from the underlying byte-stream buffer. Compared to
     * {@link #readInt64()} this method reads the 64 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the int64 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    long readCompressedInt64();

    /**
     * <p>Reads the content of a float from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the float value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    float readFloat();

    /**
     * <p>Reads the content of a double from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the double value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    double readDouble();

    /**
     * <p>Reads the content of a string from the underlying byte-stream buffer. How the string content was
     * written to the stream depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation and might offer <tt>ASCII</tt> and <tt>UTF-8</tt> or other options but doesn't have to.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the double value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    String readString();

    /**
     * <p>Reads the content of a <b>non-null</b> object from the underlying byte-stream buffer. The content itself
     * will be deserialized using a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or as an internally handled object type. It is up to the
     * {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type tag for the
     * object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the a non-null object value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    <O> O readObject()
            throws Exception;

    /**
     * <p>Reads the content of a <b>nullable</b> object from the underlying byte-stream buffer. A marker bit is read
     * from the stream to identify the object was <tt>null</tt> or actual object content is about to follow up. The
     * content itself will be deserialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object type.
     * It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type
     * tag for the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @return the an object value read from the buffer or null
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    <O> O readNullableObject()
            throws Exception;

    /**
     * <p>Transfers the content of a byte-array from the underlying byte-stream buffer. The byte-array
     * is read as a whole from begin to the end.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param bytes     the byte-array to transfer the content to
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default void readBytes(String fieldName, byte[] bytes) {
        // TODO store field name information
        readBytes(bytes);
    }

    /**
     * <p>Transfers the content of a byte-array from the underlying byte-stream buffer. The byte-array
     * is read as a whole beginning from the given <tt>offset</tt> and will be filled with as many bytes
     * as defined by the given <tt>length</tt>.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param bytes     the byte-array to transfer the content to
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default void readBytes(String fieldName, byte[] bytes, int offset, int length) {
        // TODO store field name information
        readBytes(bytes, offset, length);
    }

    /**
     * <p>Reads the content of a boolean from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the boolean value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default boolean readBoolean(String fieldName) {
        // TODO store field name information
        return readBoolean();
    }

    /**
     * <p>Reads the content of a boolean-array (or bitSet) from the underlying byte-stream buffer. A bitSet
     * is always read as a whole but the <tt>Encoder</tt> implementation was free to compact or compress
     * the values before writing, just as the default implementation does. Therefore the wire-protocol depends
     * on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the boolean-array (or bitSet) value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default boolean[] readBitSet(String fieldName) {
        // TODO store field name information
        return readBitSet();
    }

    /**
     * <p>Reads the content of a byte from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the byte value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default byte readByte(String fieldName) {
        // TODO store field name information
        return readByte();
    }

    /**
     * <p>Reads the content of an unsigned byte from the underlying byte-stream buffer. It was up to the
     * <tt>Encoder</tt> implementation how to write the value to the buffer. Therefore the wire-protocol depends
     * on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the unsigned byte value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default short readUnsignedByte(String fieldName) {
        // TODO store field name information
        return readUnsignedByte();
    }

    /**
     * <p>Reads the content of a short from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the short value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default short readShort(String fieldName) {
        // TODO store field name information
        return readShort();
    }

    /**
     * <p>Reads the content of a char from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the char value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default char readChar(String fieldName) {
        // TODO store field name information
        return readChar();
    }

    /**
     * <p>Reads the content of an int value with 32 bits from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the int32 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default int readInt32(String fieldName) {
        // TODO store field name information
        return readInt32();
    }

    /**
     * <p>Reads the content of an int value with 32 bits from the underlying byte-stream buffer. Compared to
     * {@link #readInt32()} this method reads the 32 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the int32 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default int readCompressedInt32(String fieldName) {
        // TODO store field name information
        return readCompressedInt32();
    }

    /**
     * <p>Reads the content of an int value with 64 bits from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the int64 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default long readInt64(String fieldName) {
        // TODO store field name information
        return readInt64();
    }

    /**
     * <p>Reads the content of an int value with 64 bits from the underlying byte-stream buffer. Compared to
     * {@link #readInt64()} this method reads the 64 bits compressed to the stream, whereas the actual
     * compression depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the int64 value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default long readCompressedInt64(String fieldName) {
        // TODO store field name information
        return readCompressedInt64();
    }

    /**
     * <p>Reads the content of a float from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the float value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default float readFloat(String fieldName) {
        // TODO store field name information
        return readFloat();
    }

    /**
     * <p>Reads the content of a double from the underlying byte-stream buffer.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the double value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default double readDouble(String fieldName) {
        // TODO store field name information
        return readDouble();
    }

    /**
     * <p>Reads the content of a string from the underlying byte-stream buffer. How the string content was
     * written to the stream depends on the chosen {@link com.noctarius.tengi.spi.serialization.codec.Codec}
     * implementation and might offer <tt>ASCII</tt> and <tt>UTF-8</tt> or other options but doesn't have to.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the double value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     */
    default String readString(String fieldName) {
        // TODO store field name information
        return readString();
    }

    /**
     * <p>Reads the content of a <b>non-null</b> object from the underlying byte-stream buffer. The content itself
     * will be deserialized using a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or as an internally handled object type. It is up to the
     * {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type tag for the
     * object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the a non-null object value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default <O> O readObject(String fieldName)
            throws Exception {

        // TODO store field name information
        return readObject();
    }

    /**
     * <p>Reads the content of a <b>nullable</b> object from the underlying byte-stream buffer. A marker bit is read
     * from the stream to identify the object was <tt>null</tt> or actual object content is about to follow up. The
     * content itself will be deserialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object type.
     * It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type
     * tag for the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @return the an object value read from the buffer or null
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default <O> O readNullableObject(String fieldName)
            throws Exception {

        // TODO store field name information
        return readNullableObject();
    }

    /**
     * Returns the internally stored {@link com.noctarius.tengi.spi.buffer.ReadableMemoryBuffer} to read
     * raw data directly. <b>This is not recommended and should be avoided whenever possible!</b>
     *
     * @return the wrapped <tt>ReadableMemoryBuffer</tt> instance
     */
    ReadableMemoryBuffer getReadableMemoryBuffer();

}
