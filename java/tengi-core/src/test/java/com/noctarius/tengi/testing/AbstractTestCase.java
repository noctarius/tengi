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
package com.noctarius.tengi.testing;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.config.MarshallerConfiguration;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.serialization.codec.Decoder;
import com.noctarius.tengi.serialization.codec.Encoder;
import com.noctarius.tengi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.serialization.impl.DefaultSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public abstract class AbstractTestCase {

    public static MemoryBuffer createMemoryBuffer() {
        ByteBuf buffer = Unpooled.buffer();
        return createMemoryBuffer(buffer);
    }

    public static MemoryBuffer createMemoryBuffer(ByteBuf buffer) {
        return MemoryBufferFactory.create(buffer);
    }

    public static Protocol createProtocol() {
        return new DefaultProtocol(Collections.<MarshallerConfiguration>emptyList());
    }

    public static Serializer createSerializer() {
        return createSerializer(createProtocol());
    }

    public static Serializer createSerializer(Protocol protocol) {
        return new DefaultSerializer(protocol);
    }

    public static MemoryBuffer encode(SerializationConsumer<Encoder> consumer)
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        Serializer serializer = createSerializer();
        Protocol protocol = serializer.getProtocol();

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            consumer.consume(encoder, protocol);
        }
        return memoryBuffer;
    }

    public static MemoryBuffer encode(Object value)
            throws Exception {

        return encode((encoder, protocol) -> encoder.writeObject("test", value));
    }

    public static <R> R decode(MemoryBuffer memoryBuffer, SerializationProducer<Decoder, R> consumer)
            throws Exception {

        Serializer serializer = createSerializer();
        Protocol protocol = serializer.getProtocol();

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            return consumer.produce(decoder, protocol);
        }
    }

    public static <R> R decode(MemoryBuffer memoryBuffer)
            throws Exception {

        return decode(memoryBuffer, (decoder, protocol) -> decoder.readObject());
    }

    public static <R> R encodeAndDecode(R object, int expectedSize)
            throws Exception {

        MemoryBuffer memoryBuffer = encode(object);
        assertEquals(expectedSize, memoryBuffer.writerIndex());
        return decode(memoryBuffer);
    }

    protected interface SerializationConsumer<C> {
        void consume(C codec, Protocol protocol)
                throws Exception;
    }

    protected interface SerializationProducer<C, R> {
        R produce(C codec, Protocol protocol)
                throws Exception;
    }

}
