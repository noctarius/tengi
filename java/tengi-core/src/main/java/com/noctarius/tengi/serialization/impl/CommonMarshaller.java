package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.marshaller.Marshaller;

final class CommonMarshaller {

    private CommonMarshaller() {
    }

    static enum ByteMarshaller
            implements Marshaller<Byte> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_BYTE;
        }

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
    }

    static enum ShortMarshaller
            implements Marshaller<Short> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_SHORT;
        }

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
    }

    static enum IntegerMarshaller
            implements Marshaller<Integer> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_INTEGER;
        }

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
    }

    static enum LongMarshaller
            implements Marshaller<Long> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_LONG;
        }

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
    }

    static enum FloatMarshaller
            implements Marshaller<Float> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_FLOAT;
        }

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
    }

    static enum DoubleMarshaller
            implements Marshaller<Double> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_DOUBLE;
        }

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
    }

    static enum StringMarshaller
            implements Marshaller<String> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_STRING;
        }

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
    }

    static enum ByteArrayMarshaller
            implements Marshaller<byte[]> {

        INSTANCE;

        @Override
        public short getMarshallerId() {
            return DefaultProtocolConstants.SERIALIZED_TYPE_BYTE_ARRAY;
        }

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
    }

}
