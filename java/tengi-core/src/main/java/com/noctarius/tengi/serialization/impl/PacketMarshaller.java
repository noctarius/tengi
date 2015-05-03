package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.utils.ExceptionUtil;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

enum PacketMarshaller
        implements Marshaller<Packet> {

    INSTANCE;

    private final ConcurrentMap<Class<Packet>, Construction> constructors = new ConcurrentHashMap<>();

    @Override
    public short getMarshallerId() {
        return DefaultProtocolConstants.SERIALIZED_TYPE_PACKET;
    }

    @Override
    public Packet unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        short typeId = memoryBuffer.readShort();
        String packageName = memoryBuffer.readString();

        Class<Packet> clazz = protocol.fromTypeId(typeId);
        Construction constructor = constructors.computeIfAbsent(clazz, this::computeConstructor);
        Packet packet = constructor.create(packageName);
        packet.unmarshall(memoryBuffer, protocol);
        return packet;
    }

    @Override
    public void marshall(Packet packet, WritableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        short typeId = protocol.typeId(packet);
        String packageName = packet.getPacketName();

        memoryBuffer.writeShort(typeId);
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
