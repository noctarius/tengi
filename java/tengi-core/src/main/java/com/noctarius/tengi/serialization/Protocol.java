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
package com.noctarius.tengi.serialization;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.marshaller.MarshallerReader;
import com.noctarius.tengi.serialization.marshaller.MarshallerWriter;

public interface Protocol {

    String getMimeType();

    short typeId(Object object);

    Object objectFromTypeId(short typeId);

    <T> Class<T> fromTypeId(short typeId);

    <O> O readObject(ReadableMemoryBuffer memoryBuffer)
            throws Exception;

    <O> void writeObject(O object, WritableMemoryBuffer memoryBuffer)
            throws Exception;

    default <O> void writeNullable(O object, WritableMemoryBuffer memoryBuffer, MarshallerWriter<O> writer)
            throws Exception {

        if (object == null) {
            memoryBuffer.writeByte(0);
            return;
        }
        memoryBuffer.writeByte(1);
        writer.marshall(object, memoryBuffer, this);
    }

    default <O> O readNullable(ReadableMemoryBuffer memoryBuffer, MarshallerReader<O> reader)
            throws Exception {

        if (memoryBuffer.readByte() == 1) {
            return reader.unmarshall(memoryBuffer, this);
        }
        return null;
    }

}
