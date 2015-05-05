package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
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
    public Packet unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        Class<Packet> clazz = protocol.readTypeId(memoryBuffer);
        String packageName = memoryBuffer.readString();

        Construction constructor = constructors.computeIfAbsent(clazz, this::computeConstructor);
        Packet packet = constructor.create(packageName);
        packet.unmarshall(memoryBuffer, protocol);
        return packet;
    }

    @Override
    public void marshall(Packet packet, WritableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        String packageName = packet.getPacketName();

        protocol.writeTypeId(packet, memoryBuffer);
        memoryBuffer.writeString(packageName);

        packet.marshall(memoryBuffer, protocol);
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
    public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
        return protocol.readTypeId(memoryBuffer);
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
