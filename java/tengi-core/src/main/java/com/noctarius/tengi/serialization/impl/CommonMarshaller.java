package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.utils.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

final class CommonMarshaller {

    private static final Unsafe UNSAFE = UnsafeUtil.UNSAFE;

    private static final long IDENTIFIER_DATA_OFFSET;

    static {
        if (!UnsafeUtil.UNSAFE_AVAILABLE) {
            throw new RuntimeException("Incompatible JVM - sun.misc.Unsafe support is missing");
        }

        try {
            Field identifierData = Identifier.class.getDeclaredField("data");
            identifierData.setAccessible(true);
            IDENTIFIER_DATA_OFFSET = UNSAFE.objectFieldOffset(identifierData);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException();
        }
    }

    private CommonMarshaller() {
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_BYTE)
    static enum ByteMarshaller
            implements Marshaller<Byte>, DebuggableMarshaller<Byte> {

        INSTANCE;

        @Override
        public Byte unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readByte());
        }

        @Override
        public void marshall(Byte value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeByte(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Byte.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }

    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_SHORT)
    static enum ShortMarshaller
            implements Marshaller<Short>, DebuggableMarshaller<Short> {

        INSTANCE;

        @Override
        public Short unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readShort());
        }

        @Override
        public void marshall(Short value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeShort(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Short.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_INTEGER)
    static enum IntegerMarshaller
            implements Marshaller<Integer>, DebuggableMarshaller<Integer> {

        INSTANCE;

        @Override
        public Integer unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readInt());
        }

        @Override
        public void marshall(Integer value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeInt(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Integer.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_LONG)
    static enum LongMarshaller
            implements Marshaller<Long>, DebuggableMarshaller<Long> {

        INSTANCE;

        @Override
        public Long unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readLong());
        }

        @Override
        public void marshall(Long value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeLong(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Long.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_FLOAT)
    static enum FloatMarshaller
            implements Marshaller<Float>, DebuggableMarshaller<Float> {

        INSTANCE;

        @Override
        public Float unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readFloat());
        }

        @Override
        public void marshall(Float value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeFloat(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Float.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_DOUBLE)
    static enum DoubleMarshaller
            implements Marshaller<Double>, DebuggableMarshaller<Double> {

        INSTANCE;

        @Override
        public Double unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readDouble());
        }

        @Override
        public void marshall(Double value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeDouble(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Double.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_STRING)
    static enum StringMarshaller
            implements Marshaller<String>, DebuggableMarshaller<String> {

        INSTANCE;

        @Override
        public String unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> b.readString());
        }

        @Override
        public void marshall(String value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> b.writeString(v));
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return String.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_MARSHALLABLE)
    static enum ByteArrayMarshaller
            implements Marshaller<byte[]>, DebuggableMarshaller<byte[]> {

        INSTANCE;

        @Override
        public byte[] unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            return protocol.readNullable(memoryBuffer, (b, p) -> {
                int length = b.readCompressedInt();
                byte[] array = new byte[length];
                b.readBytes(array);
                return array;
            });
        }

        @Override
        public void marshall(byte[] value, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            protocol.writeNullable(value, memoryBuffer, (v, b, p) -> {
                b.writeCompressedInt(v.length);
                b.writeBytes(v);
            });
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return byte[].class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }

    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_MESSAGE)
    enum MessageMarshaller
            implements Marshaller<Message>, DebuggableMarshaller<Message> {

        INSTANCE;

        @Override
        public Message unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            Identifier messageId = memoryBuffer.readObject();
            Object body = memoryBuffer.readObject();
            return Message.create(messageId, body);
        }

        @Override
        public void marshall(Message message, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            Identifier messageId = message.getMessageId();
            Object body = message.getBody();

            memoryBuffer.writeObject(messageId);
            memoryBuffer.writeObject(body);
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Message.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_IDENTIFIER)
    enum IdentifierMarshaller
            implements Marshaller<Identifier>, DebuggableMarshaller<Identifier> {

        INSTANCE;

        @Override
        public Identifier unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            byte[] data = new byte[16];
            memoryBuffer.readBytes(data);
            return Identifier.fromBytes(data);
        }

        @Override
        public void marshall(Identifier identifier, WritableMemoryBuffer memoryBuffer, Protocol protocol)
                throws Exception {

            byte[] data = (byte[]) UNSAFE.getObject(identifier, IDENTIFIER_DATA_OFFSET);
            memoryBuffer.writeBytes(data);
        }

        @Override
        public Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
            return Identifier.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

}
