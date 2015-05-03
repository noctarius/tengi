package com.noctarius.tengi.serialization.impl;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectIntMap;
import com.carrotsearch.hppc.ObjectIntOpenIdentityHashMap;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.SystemException;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.config.MarshallerConfiguration;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
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
        implements Protocol, DefaultProtocolConstants {

    private final IntObjectMap<Class<?>> types = new IntObjectOpenHashMap<>();
    private final ObjectIntMap<Class<?>> reverseTypes = new ObjectIntOpenIdentityHashMap<>();

    private final ConcurrentMap<Class<?>, Marshaller> marshallerCache = new ConcurrentHashMap<>();

    private final Map<MarshallerFilter, Marshaller> marshallers = new HashMap<>();
    private final IntObjectMap<Marshaller> marshallerById = new IntObjectOpenHashMap<>();

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

    @Override
    public String getMimeType() {
        return PROTOCOL_MIME_TYPE;
    }

    @Override
    public short typeId(Object object) {
        Class<?> clazz = object.getClass();
        int typeId = reverseTypes.getOrDefault(clazz, -1);

        if (typeId == -1) {
            throw new SystemException("TypeId for class '" + clazz.getName() + "' not found. Not registered?");
        }

        return (short) typeId;
    }

    @Override
    public Object objectFromTypeId(short typeId) {
        try {
            Class<?> clazz = types.get(typeId);
            return clazz.newInstance();
        } catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public <T> Class<T> fromTypeId(short typeId) {
        return (Class<T>) types.get(typeId);
    }

    @Override
    public <O> O readObject(ReadableMemoryBuffer memoryBuffer)
            throws Exception {

        short typeId = memoryBuffer.readShort();
        Marshaller marshaller = marshallerById.get(typeId);
        return (O) marshaller.unmarshall(memoryBuffer, this);
    }

    @Override
    public <O> void writeObject(O object, WritableMemoryBuffer memoryBuffer)
            throws Exception {

        Marshaller marshaller = computeMarshaller(object);
        memoryBuffer.writeShort(marshaller.getMarshallerId());
        marshaller.marshall(object, memoryBuffer, this);
    }

    private void registerInternalMarshallers() {
        // Extensible types
        registerMarshaller(PacketMarshallerFilter.INSTANCE, PacketMarshaller.INSTANCE);
        registerMarshaller(MarshallableMarshallerFilter.INSTANCE, MarshallableMarshaller.INSTANCE);

        // Hard wired types
        registerMarshaller(Message.class, MessageMarshaller.INSTANCE);
        registerMarshaller(Byte.class, CommonMarshaller.ByteMarshaller.INSTANCE);
        registerMarshaller(byte.class, CommonMarshaller.ByteMarshaller.INSTANCE);
        registerMarshaller(Short.class, CommonMarshaller.ShortMarshaller.INSTANCE);
        registerMarshaller(short.class, CommonMarshaller.ShortMarshaller.INSTANCE);
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
        marshallers.put(filter, marshaller);
        marshallerById.put(marshaller.getMarshallerId(), marshaller);
    }

    private <O> void registerMarshaller(Class<O> clazz, Marshaller marshaller) {
        marshallerById.put(marshaller.getMarshallerId(), marshaller);
        marshallerCache.put(clazz, marshaller);
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
                registerTypeId(line.trim());
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
            throw new SystemException("Registered serialization type is not annotation with @TypeId");
        }

        short typeId = annotation.value();

        reverseTypes.put(clazz, typeId);
        types.put(typeId, clazz);
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

        marshaller = testMarshaller(object, MessageMarshallerFilter.INSTANCE, MessageMarshaller.INSTANCE);
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
