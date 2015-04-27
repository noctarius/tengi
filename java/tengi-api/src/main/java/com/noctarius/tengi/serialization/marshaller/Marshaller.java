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
package com.noctarius.tengi.serialization.marshaller;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;

/**
 * The <tt>Marshaller</tt> interface defines a type to be used
 * to create external serialization mechanisms based on an
 * acceptance criteria (e.g. a special super-type or an annotation).
 */
public interface Marshaller extends MarshallerReader, MarshallerWriter {

    /**
     * This method is a convenience way to build a <tt>Marshaller</tt> instance from two
     * lambdas. It will wrapped the given two functions into a new <tt>Marshaller</tt>
     * instance.
     *
     * @param reader the marshall logic implementation (or function)
     * @param writer the un-marshall logic implementation (or function)
     * @return a wrapper Marshaller instance
     */
    public static Marshaller marshaller(MarshallerReader reader, MarshallerWriter writer) {
        return new Marshaller() {
            @Override
            public Object unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
                return reader.unmarshall(memoryBuffer, protocol);
            }

            @Override
            public void marshall(Object object, WritableMemoryBuffer memoryBuffer, Protocol protocol) {
                writer.marshall(object, memoryBuffer, protocol);
            }
        };
    }

}
