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
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.codec.Decoder;
import com.noctarius.tengi.serialization.codec.Encoder;
import com.noctarius.tengi.serialization.debugger.DebuggableMarshaller;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.utils.UnsafeUtil;
import sun.misc.Unsafe;

final class CommonMarshaller {

    private static final Unsafe UNSAFE = UnsafeUtil.UNSAFE;
    private static final long IDENTIFIER_DATA_OFFSET = UnsafeUtil.IDENTIFIER_DATA_OFFSET;

    private CommonMarshaller() {
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_BYTE)
    static enum ByteMarshaller
            implements Marshaller<Byte>, DebuggableMarshaller<Byte> {

        INSTANCE;

        @Override
        public Byte unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readByte());
        }

        @Override
        public void marshall(String fieldName, Byte value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeByte("value", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Short unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readShort());
        }

        @Override
        public void marshall(String fieldName, Short value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeShort("value", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Integer unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readInt());
        }

        @Override
        public void marshall(String fieldName, Integer value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeInt("value", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Long unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readLong());
        }

        @Override
        public void marshall(String fieldName, Long value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeLong("value", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Float unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readFloat());
        }

        @Override
        public void marshall(String fieldName, Float value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeFloat("value", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Double unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readDouble());
        }

        @Override
        public void marshall(String fieldName, Double value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeDouble("value", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public String unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> d.readString());
        }

        @Override
        public void marshall(String fieldName, String value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> e.writeString("utf8", v));
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
            return String.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

    @TypeId(DefaultProtocolConstants.SERIALIZED_TYPE_BYTE_ARRAY)
    static enum ByteArrayMarshaller
            implements Marshaller<byte[]>, DebuggableMarshaller<byte[]> {

        INSTANCE;

        @Override
        public byte[] unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            return protocol.readNullable(decoder, (d, p) -> {
                int length = d.readInt();
                byte[] array = new byte[length];
                d.readBytes(array);
                return array;
            });
        }

        @Override
        public void marshall(String fieldName, byte[] value, Encoder encoder, Protocol protocol)
                throws Exception {

            protocol.writeNullable(fieldName, value, encoder, (n, v, e, p) -> {
                e.writeInt("length", v.length);
                e.writeBytes("data", v);
            });
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Message unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            Identifier messageId = decoder.readObject();
            Object body = decoder.readObject();
            return Message.create(messageId, body);
        }

        @Override
        public void marshall(String fieldName, Message message, Encoder encoder, Protocol protocol)
                throws Exception {

            Identifier messageId = message.getMessageId();
            Object body = message.getBody();

            encoder.writeObject("messageId", messageId);
            encoder.writeObject("body", body);
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
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
        public Identifier unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {

            byte[] data = new byte[16];
            decoder.readBytes(data);
            return Identifier.fromBytes(data);
        }

        @Override
        public void marshall(String fieldName, Identifier identifier, Encoder encoder, Protocol protocol)
                throws Exception {

            byte[] data = (byte[]) UNSAFE.getObject(identifier, IDENTIFIER_DATA_OFFSET);
            encoder.writeBytes("identifier", data);
        }

        @Override
        public Class<?> findType(Decoder decoder, Protocol protocol) {
            return Identifier.class;
        }

        @Override
        public String debugValue(Object value) {
            return value.toString();
        }
    }

}
