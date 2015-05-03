package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.utils.ExceptionUtil;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentMap;

enum MarshallableMarshaller
        implements Marshaller<Marshallable> {

    INSTANCE;

    private final ConcurrentMap<Class<Marshallable>, Constructor<Marshallable>> constructors = new ConcurrentHashMapV8<>();

    @Override
    public short getMarshallerId() {
        return DefaultProtocolConstants.SERIALIZED_TYPE_MARSHALLABLE;
    }

    @Override
    public Marshallable unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        short typeId = memoryBuffer.readShort();

        Class<Marshallable> clazz = protocol.fromTypeId(typeId);
        Constructor<Marshallable> constructor = constructors.computeIfAbsent(clazz, this::computeConstructor);
        Marshallable marshallable = constructor.newInstance();
        marshallable.unmarshall(memoryBuffer, protocol);
        return marshallable;
    }

    @Override
    public void marshall(Marshallable marshallable, WritableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        short typeId = protocol.typeId(marshallable);

        memoryBuffer.writeShort(typeId);
        marshallable.marshall(memoryBuffer, protocol);
    }

    private Constructor<Marshallable> computeConstructor(Class<Marshallable> clazz) {
        try {
            return clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}
