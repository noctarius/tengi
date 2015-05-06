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
package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.utils.ExceptionUtil;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_MARSHALLABLE)
enum MarshallableMarshaller
        implements Marshaller<Marshallable>, DebuggableMarshaller<Marshallable> {

    INSTANCE;

    private final ConcurrentMap<Class<Marshallable>, Constructor<Marshallable>> constructors = new ConcurrentHashMap<>();

    @Override
    public Marshallable unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        Class<Marshallable> clazz = protocol.readTypeId(memoryBuffer);
        Constructor<Marshallable> constructor = constructors.computeIfAbsent(clazz, this::computeConstructor);
        Marshallable marshallable = constructor.newInstance();
        marshallable.unmarshall(memoryBuffer, protocol);
        return marshallable;
    }

    @Override
    public void marshall(Marshallable marshallable, WritableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        protocol.writeTypeId(marshallable, memoryBuffer);
        marshallable.marshall(memoryBuffer, protocol);
    }

    private Constructor<Marshallable> computeConstructor(Class<Marshallable> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
        return protocol.readTypeId(memoryBuffer);
    }

    @Override
    public String debugValue(Object value) {
        return value.toString();
    }
}
