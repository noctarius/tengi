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

import com.noctarius.tengi.core.buffer.WritableMemoryBuffer;

public interface Encoder {

    void writeBytes(String fieldName, byte[] bytes);

    void writeBytes(String fieldName, byte[] bytes, int offset, int length);

    void writeBoolean(String fieldName, boolean value);

    void writeBitSet(String fieldName, boolean[] values);

    void writeByte(String fieldName, int value);

    void writeUnsignedByte(String fieldName, short value);

    void writeShort(String fieldName, short value);

    void writeChar(String fieldName, char value);

    void writeInt32(String fieldName, int value);

    void writeCompressedInt32(String fieldName, int value);

    void writeInt64(String fieldName, long value);

    void writeCompressedInt64(String fieldName, long value);

    void writeFloat(String fieldName, float value);

    void writeDouble(String fieldName, double value);

    void writeString(String fieldName, String value);

    void writeObject(String fieldName, Object object)
            throws Exception;

    void writeNullableObject(String fieldName, Object object)
            throws Exception;

    WritableMemoryBuffer getWritableMemoryBuffer();

}
