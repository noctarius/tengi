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
package com.noctarius.tengi.core.serialization.impl;

import com.noctarius.tengi.core.exception.SystemException;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.core.config.MarshallerConfiguration;
import com.noctarius.tengi.core.serialization.Marshallable;
import com.noctarius.tengi.core.serialization.Protocol;
import com.noctarius.tengi.core.serialization.Serializer;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.core.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.debugger.DebuggableProtocol;
import com.noctarius.tengi.core.serialization.marshaller.Identifiable;
import com.noctarius.tengi.core.serialization.marshaller.Marshaller;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DefaultProtocolTestCase
        extends AbstractTestCase {

    @Test
    public void test_creation_with_custom_input_stream()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeObject("test", new TestClass());
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            Object result = decoder.readObject();
            assertNotNull(result);
            assertTrue(result instanceof TestClass);
        }
    }

    @Test(expected = SystemException.class)
    public void test_registration_type_without_typeid()
            throws Exception {

        InputStream is = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase2");

        createProtocol(is);
    }

    @Test
    public void test_creation_with_custom_marshaller_accept_all_filter()
            throws Exception {

        Collection<MarshallerConfiguration> marshallerConfigurations = Collections.singletonList(
                new MarshallerConfiguration((v) -> MarshallerFilter.Result.AcceptedAndCache, new TestIdentifiableMarshaller()));

        DebuggableProtocol protocol = (DebuggableProtocol) createProtocol(marshallerConfigurations);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeObject("test", new TestClass3());
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            Class<?> type = protocol.findType(decoder);
            assertSame(TestIdentifiableMarshaller.class, type);
        }
    }

    @Test(expected = SystemException.class)
    public void test_creation_with_custom_marshaller_without_id()
            throws Exception {

        Collection<MarshallerConfiguration> marshallerConfigurations = Collections.singletonList(
                new MarshallerConfiguration((v) -> MarshallerFilter.Result.Accepted, new TestNonIdentifiableMarshaller()));

        createProtocol(marshallerConfigurations);
    }

    @Test
    public void test_write_type_id_object_parameter()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeTypeId(new TestClass(), encoder);
        }

        assertEquals(2, memoryBuffer.writerIndex());
    }

    @Test
    public void test_write_type_id_class_parameter()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeTypeId(TestClass.class, encoder);
        }

        assertEquals(2, memoryBuffer.writerIndex());
    }

    @Test(expected = SystemException.class)
    public void test_write_type_id_wrong_parameter()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeTypeId(DefaultProtocolTestCase.class, encoder);
        }

        assertEquals(2, memoryBuffer.writerIndex());
    }

    @Test
    public void test_read_type_id()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeTypeId(new TestClass(), encoder);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            Class<?> typeClass = protocol.readTypeId(decoder);
            assertEquals(TestClass.class, typeClass);
        }
    }

    @Test
    public void test_read_type_Object()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeTypeId(new TestClass(), encoder);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            Object result = protocol.readTypeObject(decoder);
            assertNotNull(result);
            assertTrue(result instanceof TestClass);
        }
    }

    @Test
    public void test_read_object()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        Integer value = 12345;

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeObject("test", value, encoder);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            Integer result = protocol.readObject(decoder);
            assertNotNull(result);
            assertEquals(value, result);
        }
    }

    @Test
    public void test_write_object()
            throws Exception {

        InputStream testClassStream = DefaultProtocolTestCase.class.getResourceAsStream("DefaultProtocolTestCase");
        Protocol protocol = createProtocol(testClassStream);
        Serializer serializer = createSerializer(protocol);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            protocol.writeObject("test", 12345, encoder);
        }

        assertEquals(6, memoryBuffer.writerIndex());
    }

    @TypeId(9999)
    public static class TestClass
            implements Marshallable {

        @Override
        public void marshall(Encoder encoder, Protocol protocol)
                throws Exception {
        }

        @Override
        public void unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {
        }
    }

    public static class TestClass2
            implements Marshallable {

        @Override
        public void marshall(Encoder encoder, Protocol protocol)
                throws Exception {
        }

        @Override
        public void unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {
        }
    }

    public static class TestClass3 {
    }

    public static class TestIdentifiableMarshaller
            implements Marshaller<TestClass3>, Identifiable<Short> {

        @Override
        public Short identifier() {
            return 9998;
        }

        @Override
        public TestClass3 unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {
            return null;
        }

        @Override
        public void marshall(String fieldName, TestClass3 object, Encoder encoder, Protocol protocol)
                throws Exception {

        }
    }

    public static class TestNonIdentifiableMarshaller
            implements Marshaller<TestClass3> {

        @Override
        public TestClass3 unmarshall(Decoder decoder, Protocol protocol)
                throws Exception {
            return null;
        }

        @Override
        public void marshall(String fieldName, TestClass3 object, Encoder encoder, Protocol protocol)
                throws Exception {

        }
    }

}
