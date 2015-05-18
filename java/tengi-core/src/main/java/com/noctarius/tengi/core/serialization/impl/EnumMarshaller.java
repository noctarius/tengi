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
package com.noctarius.tengi.core.serialization.impl;

import com.noctarius.tengi.core.serialization.Protocol;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.core.serialization.marshaller.Marshaller;
import com.noctarius.tengi.core.exception.UnknownTypeException;

@TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_ENUM)
enum EnumMarshaller
        implements Marshaller<Enum>, DebuggableMarshaller<Enum> {

    INSTANCE;

    @Override
    public Enum unmarshall(Decoder decoder, Protocol protocol)
            throws Exception {

        Class<Enum> clazz = protocol.readTypeId(decoder);

        String name = decoder.readString();
        Enum[] constants = clazz.getEnumConstants();

        for (Enum constant : constants) {
            if (constant.name().equals(name)) {
                return constant;
            }
        }
        throw new UnknownTypeException("Enum type not found");
    }

    @Override
    public void marshall(String fieldName, Enum constant, Encoder encoder, Protocol protocol)
            throws Exception {

        protocol.writeTypeId(constant, encoder);
        encoder.writeString("name", constant.name());
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
