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

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.SystemException;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.config.MarshallerConfiguration;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.codec.Decoder;
import com.noctarius.tengi.serialization.codec.Encoder;
import com.noctarius.tengi.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.serialization.debugger.DebuggableProtocol;
import com.noctarius.tengi.serialization.marshaller.Identifiable;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.utils.ExceptionUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultProtocol
        implements Protocol, DebuggableProtocol, DefaultProtocolConstants {

    private final Map<Short, Class<?>> typeById = new ConcurrentHashMap<>();
    private final Map<Class<?>, Short> reverseTypeId = new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, Marshaller> marshallerCache = new ConcurrentHashMap<>();

    private final Map<MarshallerFilter, Marshaller> marshallers = new HashMap<>();
    private final Map<Short, Marshaller> marshallerById = new ConcurrentHashMap<>();
    private final Map<Marshaller, Short> reverseMarshallerId = new ConcurrentHashMap<>();

    public DefaultProtocol(Collection<MarshallerConfiguration> marshallerConfigurations) {
        this(null, marshallerConfigurations);
    }

    public DefaultProtocol(InputStream is, Collection<MarshallerConfiguration> marshallerConfigurations) {
        ClassLoader classLoader = getClass().getClassLoader();
        registerInternalTypes(classLoader);
        typesInitializer(classLoader.getResourceAsStream(TYPE_MANIFEST_FILENAME));
        if (is != null) {
            typesInitializer(is);
        }
        registerInternalMarshallers();
        registerMarshallers(marshallerConfigurations);
    }

    @Override
    public String getMimeType() {
        return PROTOCOL_MIME_TYPE;
    }

    @Override
    public void writeTypeId(Object value, Encoder encoder) {
        Class<?> type;
        if (value instanceof Class) {
            type = (Class<?>) value;
        } else {
            type = value.getClass();
        }
        Short typeId = reverseTypeId.get(type);

        if (typeId == null) {
            throw new SystemException("TypeId for type '" + type.getName() + "' not found. Not registered?");
        }
        encoder.writeShort("typeId", typeId);
    }

    @Override
    public <T> Class<T> readTypeId(Decoder decoder) {
        short typeId = decoder.readShort();
        return (Class<T>) typeById.get(typeId);
    }

    @Override
    public Object readTypeObject(Decoder decoder) {
        try {
            Class<?> clazz = readTypeId(decoder);
            return clazz.newInstance();
        } catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public Class<?> findType(Decoder decoder) {
        ReadableMemoryBuffer memoryBuffer = decoder.getReadableMemoryBuffer();
        int readerIndex = memoryBuffer.readerIndex();
        try {
            short typeId = decoder.readShort();
            Class<?> clazz = typeById.get(typeId);
            if (clazz != null) {
                return clazz;
            }
            Marshaller<?> marshaller = marshallerById.get(typeId);
            if (marshaller != null && marshaller instanceof DebuggableMarshaller) {
                return ((DebuggableMarshaller<?>) marshaller).findType(decoder, this);
            }
            return null;
        } finally {
            memoryBuffer.readerIndex(readerIndex);
        }
    }

    @Override
    public <O> O readObject(Decoder decoder)
            throws Exception {

        short typeId = decoder.readShort();
        Marshaller marshaller = marshallerById.get(typeId);
        return (O) marshaller.unmarshall(decoder, this);
    }

    @Override
    public <O> void writeObject(String fieldName, O object, Encoder encoder)
            throws Exception {

        Marshaller marshaller = computeMarshaller(object);
        encoder.writeShort("marshallerId", findMarshallerId(marshaller));
        marshaller.marshall(fieldName, object, encoder, this);
    }

    private void registerInternalTypes(ClassLoader classLoader) {
        try {
            Enumeration<URL> resources = classLoader.getResources(TYPE_DEFAULT_MANIFEST_FILENAME);
            while (resources.hasMoreElements()) {
                typesInitializer(resources.nextElement().openStream());
            }

        } catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void registerInternalMarshallers() {
        // External types
        registerMarshaller(PacketMarshallerFilter.INSTANCE, PacketMarshaller.INSTANCE);
        registerMarshaller(MarshallableMarshallerFilter.INSTANCE, MarshallableMarshaller.INSTANCE);
        registerMarshaller(EnumerableMarshallerFilter.INSTANCE, EnumerableMarshaller.INSTANCE);
        registerMarshaller(EnumMarshallerFilter.INSTANCE, EnumMarshaller.INSTANCE);

        // Internal types
        registerMarshaller(Message.class, CommonMarshaller.MessageMarshaller.INSTANCE);
        registerMarshaller(Identifier.class, CommonMarshaller.IdentifierMarshaller.INSTANCE);
        registerMarshaller(Byte.class, CommonMarshaller.ByteMarshaller.INSTANCE);
        registerMarshaller(byte.class, CommonMarshaller.ByteMarshaller.INSTANCE);
        registerMarshaller(Short.class, CommonMarshaller.ShortMarshaller.INSTANCE);
        registerMarshaller(short.class, CommonMarshaller.ShortMarshaller.INSTANCE);
        registerMarshaller(Character.class, CommonMarshaller.CharMarshaller.INSTANCE);
        registerMarshaller(char.class, CommonMarshaller.CharMarshaller.INSTANCE);
        registerMarshaller(Integer.class, CommonMarshaller.IntegerMarshaller.INSTANCE);
        registerMarshaller(int.class, CommonMarshaller.IntegerMarshaller.INSTANCE);
        registerMarshaller(Long.class, CommonMarshaller.LongMarshaller.INSTANCE);
        registerMarshaller(long.class, CommonMarshaller.LongMarshaller.INSTANCE);
        registerMarshaller(Float.class, CommonMarshaller.FloatMarshaller.INSTANCE);
        registerMarshaller(float.class, CommonMarshaller.FloatMarshaller.INSTANCE);
        registerMarshaller(Double.class, CommonMarshaller.DoubleMarshaller.INSTANCE);
        registerMarshaller(double.class, CommonMarshaller.DoubleMarshaller.INSTANCE);
        registerMarshaller(String.class, CommonMarshaller.StringMarshaller.INSTANCE);
        registerMarshaller(byte[].class, CommonMarshaller.ByteArrayMarshaller.INSTANCE);
    }

    private void registerMarshallers(Collection<MarshallerConfiguration> marshallerConfigurations) {
        marshallerConfigurations.forEach((config) -> registerMarshaller(config.getMarshallerFilter(), config.getMarshaller()));
    }

    private void registerMarshaller(MarshallerFilter filter, Marshaller marshaller) {
        short marshallerId = findMarshallerId(marshaller);
        marshallers.put(filter, marshaller);
        marshallerById.put(marshallerId, marshaller);
        reverseMarshallerId.put(marshaller, marshallerId);
    }

    private <O> void registerMarshaller(Class<O> clazz, Marshaller marshaller) {
        short marshallerId = findMarshallerId(marshaller);
        marshallerCache.put(clazz, marshaller);
        marshallerById.put(marshallerId, marshaller);
        reverseMarshallerId.put(marshaller, marshallerId);
    }

    private void typesInitializer(InputStream is) {
        if (is == null) {
            return;
        }

        try {
            Reader pipeReader = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new LineNumberReader(pipeReader);

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    registerTypeId(line);
                }
            }

        } catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void registerTypeId(String className)
            throws Exception {

        Class<?> clazz = Class.forName(className);

        TypeId annotation = clazz.getAnnotation(TypeId.class);
        if (annotation == null) {
            throw new SystemException("Registered serialization type is not annotated with @TypeId");
        }

        short typeId = annotation.value();

        reverseTypeId.put(clazz, typeId);
        typeById.put(typeId, clazz);
    }

    private <O> short findMarshallerId(Marshaller<O> marshaller) {
        Short marshallerId = reverseMarshallerId.get(marshaller);
        if (marshallerId != null) {
            return marshallerId;
        }

        if (marshaller instanceof Identifiable) {
            return ((Identifiable<Short>) marshaller).identifier();
        }

        TypeId annotation = marshaller.getClass().getAnnotation(TypeId.class);
        if (annotation == null) {
            throw new SystemException("Registered marshaller type is not annotated with @TypeId");
        }

        return annotation.value();
    }

    private Marshaller computeMarshaller(Object object) {
        Class<?> clazz = object.getClass();
        Marshaller marshaller = marshallerCache.get(clazz);
        if (marshaller != null) {
            return marshaller;
        }

        marshaller = testMarshaller(object, PacketMarshallerFilter.INSTANCE, PacketMarshaller.INSTANCE);
        if (marshaller != null) {
            return marshaller;
        }

        marshaller = testMarshaller(object, MarshallableMarshallerFilter.INSTANCE, MarshallableMarshaller.INSTANCE);
        if (marshaller != null) {
            return marshaller;
        }

        for (Map.Entry<MarshallerFilter, Marshaller> entry : marshallers.entrySet()) {
            marshaller = testMarshaller(object, entry.getKey(), entry.getValue());
            if (marshaller != null) {
                return marshaller;
            }
        }
        throw new SystemException("No suitable marshaller found for type '" + clazz.getName() + "'");
    }

    private Marshaller testMarshaller(Object object, MarshallerFilter filter, Marshaller marshaller) {
        MarshallerFilter.Result result = filter.accept(object);
        if (result == MarshallerFilter.Result.AcceptedAndCache) {
            marshallerCache.putIfAbsent(object.getClass(), marshaller);
            return marshaller;
        } else if (result == MarshallerFilter.Result.Accepted) {
            return marshaller;
        }
        return null;
    }
}
