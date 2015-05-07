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

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.codec.Decoder;
import com.noctarius.tengi.serialization.codec.Encoder;
import com.noctarius.tengi.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.utils.ExceptionUtil;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_PACKET)
enum PacketMarshaller
        implements Marshaller<Packet>, DebuggableMarshaller<Packet> {

    INSTANCE;

    private final ConcurrentMap<Class<Packet>, Construction> constructors = new ConcurrentHashMap<>();

    @Override
    public Packet unmarshall(Decoder decoder, Protocol protocol)
            throws Exception {

        Class<Packet> clazz = protocol.readTypeId(decoder);
        String packageName = decoder.readString();

        Construction constructor = constructors.computeIfAbsent(clazz, this::computeConstructor);
        Packet packet = constructor.create(packageName);
        packet.unmarshall(decoder, protocol);
        return packet;
    }

    @Override
    public void marshall(String fieldName, Packet packet, Encoder encoder, Protocol protocol)
            throws Exception {

        String packageName = packet.getPacketName();

        protocol.writeTypeId(packet, encoder);
        encoder.writeString("packageName", packageName);

        packet.marshall(encoder, protocol);
    }

    private Construction computeConstructor(Class<Packet> clazz) {
        try {
            return new PackageNameConstruction(clazz.getConstructor(String.class));
        } catch (NoSuchMethodException e) {
            // Ignore for now probably there is a default constructor
        }
        try {
            return new DefaultConstruction(clazz.getConstructor());
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

    private static interface Construction {
        Packet create(String packageName)
                throws Exception;
    }

    private static final class PackageNameConstruction
            implements Construction {

        private final Constructor<Packet> constructor;

        public PackageNameConstruction(Constructor<Packet> constructor) {
            this.constructor = constructor;
        }

        @Override
        public Packet create(String packageName)
                throws Exception {

            return constructor.newInstance(packageName);
        }
    }

    private static final class DefaultConstruction
            implements Construction {

        private final Constructor<Packet> constructor;

        public DefaultConstruction(Constructor<Packet> constructor) {
            this.constructor = constructor;
        }

        @Override
        public Packet create(String packageName)
                throws Exception {

            return constructor.newInstance();
        }
    }
}
