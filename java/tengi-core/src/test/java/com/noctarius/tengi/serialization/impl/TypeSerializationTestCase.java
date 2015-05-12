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
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;

import static com.noctarius.tengi.serialization.impl.SerializationClasses.TestEnum;
import static com.noctarius.tengi.serialization.impl.SerializationClasses.TestEnumerable;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TypeSerializationTestCase
        extends AbstractTestCase {

    @Test
    public void test_byte_round_trip()
            throws Exception {

        Byte value1 = Byte.MAX_VALUE;
        Byte response1 = encodeAndDecode(value1, 3);
        assertEquals(value1, response1);

        byte value2 = Byte.MAX_VALUE;
        byte response2 = encodeAndDecode(value2, 3);
        assertEquals(value2, response2);
    }

    @Test
    public void test_unsigned_byte_round_trip()
            throws Exception {

        short value = 254;
        MemoryBuffer memoryBuffer = encode((encoder, protocol) -> encoder.writeUnsignedByte("test", value));
        assertEquals(1, memoryBuffer.writerIndex());
        short response = decode(memoryBuffer, (decoder, protocol) -> decoder.readUnsignedByte());
        assertEquals(value, response);
    }

    @Test
    public void test_byte_array_simple_round_trip()
            throws Exception {

        byte[] value = {(byte) 't', (byte) 'e', (byte) 'n', (byte) 'g', (byte) 'i'};
        byte[] response = encodeAndDecode(value, 11);
        assertArrayEquals(value, response);
    }

    @Test
    public void test_byte_array_offset_round_trip()
            throws Exception {

        byte[] value = {(byte) 't', (byte) 'e', (byte) 'n', (byte) 'g', (byte) 'i'};
        MemoryBuffer memoryBuffer = encode((encoder, protocol) -> {
            encoder.writeInt32("length", 3);
            encoder.writeBytes("test", value, 2, 3);
        });
        assertEquals(7, memoryBuffer.writerIndex());
        byte[] response = decode(memoryBuffer, (decoder, protocol) -> {
            int length = decoder.readInt32();
            byte[] data = new byte[length];
            decoder.readBytes(data);
            return data;
        });
        assertEquals(3, response.length);
        assertEquals((byte) 'n', response[0]);
        assertEquals((byte) 'g', response[1]);
        assertEquals((byte) 'i', response[2]);
    }

    @Test
    public void test_short_round_trip()
            throws Exception {

        Short value1 = Short.MAX_VALUE;
        Short response1 = encodeAndDecode(value1, 4);
        assertEquals(value1, response1);

        short value2 = Short.MAX_VALUE;
        short response2 = encodeAndDecode(value2, 4);
        assertEquals(value2, response2);
    }

    @Test
    public void test_char_round_trip()
            throws Exception {

        Character value1 = Character.MAX_VALUE;
        Character response1 = encodeAndDecode(value1, 4);
        assertEquals(value1, response1);

        char value2 = Character.MAX_VALUE;
        char response2 = encodeAndDecode(value2, 4);
        assertEquals(value2, response2);
    }

    @Test
    public void test_int_round_trip()
            throws Exception {

        Integer value1 = Integer.MAX_VALUE;
        Integer response1 = encodeAndDecode(value1, 6);
        assertEquals(value1, response1);

        int value2 = Integer.MAX_VALUE;
        int response2 = encodeAndDecode(value2, 6);
        assertEquals(value2, response2);
    }

    @Test
    public void test_long_round_trip()
            throws Exception {

        Long value1 = Long.MAX_VALUE;
        Long response1 = encodeAndDecode(value1, 10);
        assertEquals(value1, response1);

        long value2 = Long.MAX_VALUE;
        long response2 = encodeAndDecode(value2, 10);
        assertEquals(value2, response2);
    }

    @Test
    public void test_float_round_trip()
            throws Exception {

        Float value1 = Float.MAX_VALUE;
        Float response1 = encodeAndDecode(value1, 6);
        assertEquals(value1, response1, 0.);

        float value2 = Float.MAX_VALUE;
        float response2 = encodeAndDecode(value2, 6);
        assertEquals(value2, response2, 0.);
    }

    @Test
    public void test_double_round_trip()
            throws Exception {

        Double value1 = Double.MAX_VALUE;
        Double response1 = encodeAndDecode(value1, 10);
        assertEquals(value1, response1);

        double value2 = Double.MAX_VALUE;
        double response2 = encodeAndDecode(value2, 10);
        assertEquals(value2, response2, 0.);
    }

    @Test
    public void test_string_round_trip()
            throws Exception {

        String value = "test-value";
        String response = encodeAndDecode(value, 22);
        assertEquals(value, response);
    }

    @Test
    public void test_identifier_round_trip()
            throws Exception {

        Identifier value = Identifier.randomIdentifier();
        Identifier response = encodeAndDecode(value, 18);
        assertEquals(value, response);
    }

    @Test
    public void test_message_round_trip()
            throws Exception {

        Message value = Message.create("test");
        Message response = encodeAndDecode(value, 36);
        assertEquals(value, response);
    }

    @Test
    public void test_enum_round_trip()
            throws Exception {

        TestEnum value1 = TestEnum.Value1;
        TestEnum response1 = encodeAndDecode(value1, 20);
        assertEquals(value1, response1);

        TestEnum value2 = TestEnum.Value2;
        TestEnum response2 = encodeAndDecode(value2, 20);
        assertEquals(value2, response2);
    }

    @Test
    public void test_enumerable_round_trip()
            throws Exception {

        TestEnumerable value1 = TestEnumerable.Value1;
        TestEnumerable response1 = encodeAndDecode(value1, 8);
        assertEquals(value1, response1);

        TestEnumerable value2 = TestEnumerable.Value2;
        TestEnumerable response2 = encodeAndDecode(value2, 8);
        assertEquals(value2, response2);
    }

    @Test
    public void test_nullable_with_null_round_trip()
            throws Exception {

        Object response = encodeAndDecodeNullable(null, 1);
        assertNull(response);
    }

    @Test
    public void test_nullable_with_non_null_round_trip()
            throws Exception {

        TestEnumerable value = TestEnumerable.Value1;
        TestEnumerable response = encodeAndDecodeNullable(value, 9);
        assertEquals(value, response);
    }

    @Test(expected = NullPointerException.class)
    public void test_non_nullable_with_null_round_trip()
            throws Exception {

        Object response = encodeAndDecode(null, 0);
        assertNull(response);
    }

}
