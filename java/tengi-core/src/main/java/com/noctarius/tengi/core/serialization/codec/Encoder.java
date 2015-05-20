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

public interface Encoder {

    void writeBytes(byte[] bytes);

    void writeBytes(byte[] bytes, int offset, int length);

    void writeBoolean(boolean value);

    void writeBitSet(boolean[] values);

    void writeByte(int value);

    void writeUnsignedByte(short value);

    void writeShort(short value);

    void writeChar(char value);

    void writeInt32(int value);

    void writeCompressedInt32(int value);

    void writeInt64(long value);

    void writeCompressedInt64(long value);

    void writeFloat(float value);

    void writeDouble(double value);

    void writeString(String value);

    void writeObject(Object object)
            throws Exception;

    void writeNullableObject(Object object)
            throws Exception;

    default void writeBytes(String fieldName, byte[] bytes) {
        // TODO store field name information
        writeBytes(bytes);
    }

    default void writeBytes(String fieldName, byte[] bytes, int offset, int length) {
        // TODO store field name information
        writeBytes(bytes, offset, length);
    }

    default void writeBoolean(String fieldName, boolean value) {
        // TODO store field name information
        writeBoolean(value);
    }

    default void writeBitSet(String fieldName, boolean[] values) {
        // TODO store field name information
        writeBitSet(values);
    }

    default void writeByte(String fieldName, int value) {
        // TODO store field name information
        writeByte(value);
    }

    default void writeUnsignedByte(String fieldName, short value) {
        // TODO store field name information
        writeUnsignedByte(value);
    }

    default void writeShort(String fieldName, short value) {
        // TODO store field name information
        writeShort(value);
    }

    default void writeChar(String fieldName, char value) {
        // TODO store field name information
        writeChar(value);
    }

    default void writeInt32(String fieldName, int value) {
        // TODO store field name information
        writeInt32(value);
    }

    default void writeCompressedInt32(String fieldName, int value) {
        // TODO store field name information
        writeCompressedInt32(value);
    }

    default void writeInt64(String fieldName, long value) {
        // TODO store field name information
        writeInt64(value);
    }

    default void writeCompressedInt64(String fieldName, long value) {
        // TODO store field name information
        writeCompressedInt64(value);
    }

    default void writeFloat(String fieldName, float value) {
        // TODO store field name information
        writeFloat(value);
    }

    default void writeDouble(String fieldName, double value) {
        // TODO store field name information
        writeDouble(value);
    }

    default void writeString(String fieldName, String value) {
        // TODO store field name information
        writeString(value);
    }

    default void writeObject(String fieldName, Object object)
            throws Exception {

        // TODO store field name information
        writeObject(object);
    }

    default void writeNullableObject(String fieldName, Object object)
            throws Exception {

        // TODO store field name information
        writeNullableObject(object);
    }

    WritableMemoryBuffer getWritableMemoryBuffer();

}
