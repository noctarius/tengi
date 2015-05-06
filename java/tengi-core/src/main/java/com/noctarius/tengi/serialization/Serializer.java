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

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.config.MarshallerConfiguration;
import com.noctarius.tengi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.serialization.impl.DefaultSerializer;

import java.util.Collection;

public interface Serializer {

    Protocol getProtocol();

    <O> O readObject(ReadableMemoryBuffer memoryBuffer)
            throws Exception;

    <O> MemoryBuffer writeObject(O object)
            throws Exception;

    <O> void writeObject(O object, WritableMemoryBuffer memoryBuffer)
            throws Exception;

    public static Serializer create(Collection<MarshallerConfiguration> marshallerConfigurations) {
        return create(new DefaultProtocol(marshallerConfigurations));
    }

    public static Serializer create(Protocol protocol) {
        return new DefaultSerializer(protocol);
    }

}
