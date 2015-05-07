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
package com.noctarius.tengi.buffer;

import java.nio.ByteBuffer;

public interface WritableMemoryBuffer {

    boolean writable();

    int writableBytes();

    void writeBytes(byte[] bytes);

    void writeBytes(byte[] bytes, int offset, int length);

    void writeBuffer(ByteBuffer byteBuffer);

    void writeBuffer(ByteBuffer byteBuffer, int offset, int length);

    void writeBuffer(MemoryBuffer memoryBuffer);

    void writeBuffer(MemoryBuffer memoryBuffer, int offset, int length);

    void writeBoolean(boolean value);

    void writeByte(int value);

    void writeUnsignedByte(short value);

    int writerIndex();

    void writerIndex(int writerIndex);

}
