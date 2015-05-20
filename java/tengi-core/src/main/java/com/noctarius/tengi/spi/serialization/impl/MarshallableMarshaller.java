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
package com.noctarius.tengi.spi.serialization.impl;

import com.noctarius.tengi.core.impl.ExceptionUtil;
import com.noctarius.tengi.core.serialization.Marshallable;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.core.serialization.marshaller.Marshaller;
import com.noctarius.tengi.spi.serialization.Protocol;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_MARSHALLABLE)
enum MarshallableMarshaller
        implements Marshaller<Marshallable>, DebuggableMarshaller<Marshallable> {

    INSTANCE;

    private final ConcurrentMap<Class<Marshallable>, Constructor<Marshallable>> constructors = new ConcurrentHashMap<>();

    @Override
    public Marshallable unmarshall(Decoder decoder, Protocol protocol)
            throws Exception {

        Class<Marshallable> clazz = protocol.readTypeId(decoder);
        Constructor<Marshallable> constructor = constructors.computeIfAbsent(clazz, this::computeConstructor);
        Marshallable marshallable = constructor.newInstance();
        marshallable.unmarshall(decoder, protocol);
        return marshallable;
    }

    @Override
    public void marshall(Marshallable marshallable, Encoder encoder, Protocol protocol)
            throws Exception {

        protocol.writeTypeId(marshallable, encoder);
        marshallable.marshall(encoder, protocol);
    }

    private Constructor<Marshallable> computeConstructor(Class<Marshallable> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public Class<?> findType(Decoder decoder, Protocol protocol) {
        return protocol.readTypeId(decoder);
    }

    @Override
    public String debugValue(Object value) {
        return value.toString();
    }
}
