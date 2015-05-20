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

public interface Decoder {

    int readBytes(byte[] bytes);

    int readBytes(byte[] bytes, int offset, int length);

    boolean readBoolean();

    boolean[] readBitSet();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    char readChar();

    int readInt32();

    int readCompressedInt32();

    long readInt64();

    long readCompressedInt64();

    float readFloat();

    double readDouble();

    String readString();

    <O> O readObject()
            throws Exception;

    <O> O readNullableObject()
            throws Exception;

    default int readBytes(String fieldName, byte[] bytes) {
        // TODO store field name information
        return readBytes(bytes);
    }

    default int readBytes(String fieldName, byte[] bytes, int offset, int length) {
        // TODO store field name information
        return readBytes(bytes, offset, length);
    }

    default boolean readBoolean(String fieldName) {
        // TODO store field name information
        return readBoolean();
    }

    default boolean[] readBitSet(String fieldName) {
        // TODO store field name information
        return readBitSet();
    }

    default byte readByte(String fieldName) {
        // TODO store field name information
        return readByte();
    }

    default short readUnsignedByte(String fieldName) {
        // TODO store field name information
        return readUnsignedByte();
    }

    default short readShort(String fieldName) {
        // TODO store field name information
        return readShort();
    }

    default char readChar(String fieldName) {
        // TODO store field name information
        return readChar();
    }

    default int readInt32(String fieldName) {
        // TODO store field name information
        return readInt32();
    }

    default int readCompressedInt32(String fieldName) {
        // TODO store field name information
        return readCompressedInt32();
    }

    default long readInt64(String fieldName) {
        // TODO store field name information
        return readInt64();
    }

    default long readCompressedInt64(String fieldName) {
        // TODO store field name information
        return readCompressedInt64();
    }

    default float readFloat(String fieldName) {
        // TODO store field name information
        return readFloat();
    }

    default double readDouble(String fieldName) {
        // TODO store field name information
        return readDouble();
    }

    default String readString(String fieldName) {
        // TODO store field name information
        return readString();
    }

    default <O> O readObject(String fieldName)
            throws Exception {

        // TODO store field name information
        return readObject();
    }

    default <O> O readNullableObject(String fieldName)
            throws Exception {

        // TODO store field name information
        return readNullableObject();
    }

    ReadableMemoryBuffer getReadableMemoryBuffer();

}
