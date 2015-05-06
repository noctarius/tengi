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
public interface Marshaller<O>
        extends MarshallerReader<O>, MarshallerWriter<O> {

    /**
     * This method is a convenience way to build a <tt>Marshaller</tt> instance from two
     * lambdas. It will wrapped the given two functions into a new <tt>Marshaller</tt>
     * instance.
     *
     * @param marshallerId the protocol id of the marshaller
     * @param reader       the marshall logic implementation (or function)
     * @param writer       the un-marshall logic implementation (or function)
     * @return a wrapper Marshaller instance
     */
    public static <O, I> Marshaller<O> marshaller(I marshallerId,  //
                                                  MarshallerReader<O> reader, MarshallerWriter<O> writer) {

        class JitMarshaller
                implements Marshaller<O>, Identifiable<I> {

            @Override
            public I identifier() {
                return marshallerId;
            }

            @Override
            public O unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                    throws Exception {

                return reader.unmarshall(memoryBuffer, protocol);
            }

            @Override
            public void marshall(O object, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                    throws Exception {

                writer.marshall(object, memoryBuffer, protocol);
            }
        }
        return new JitMarshaller();
    }

}