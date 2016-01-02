/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.spi.serialization.codec.impl;

import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Int64CompressorTestCase
        extends AbstractTestCase {

    @Test
    public void test_1_byte()
            throws Exception {

        long value = 0b1001;
        long result = encodeAndDecode(value, 1);
        assertEquals(value, result);

        value = Long.MAX_VALUE;
        result = encodeAndDecode(value, 1);
        assertEquals(value, result);
    }

    @Test
    public void test_2_bytes()
            throws Exception {

        long value = 0b1100_1001;
        long result = encodeAndDecode(value, 2);
        assertEquals(value, result);
    }

    @Test
    public void test_3_bytes()
            throws Exception {

        long value = 0b110_0100_1111_1011;
        long result = encodeAndDecode(value, 3);
        assertEquals(value, result);
    }

    @Test
    public void test_4_bytes()
            throws Exception {

        long value = 0b11_0010_0111_1101_1011_1011;
        long result = encodeAndDecode(value, 4);
        assertEquals(value, result);
    }

    @Test
    public void test_5_bytes()
            throws Exception {

        long value = 0b1010_0101_0101_0101_0101_0101_0101_0101;
        long result = encodeAndDecode(value, 5);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_1_byte()
            throws Exception {

        long value = Long.MIN_VALUE;
        long result = encodeAndDecode(value, 1);
        assertEquals(value, result);

        value = Long.MIN_VALUE + 1;
        result = encodeAndDecode(value, 1);
        assertEquals(value, result);

        value = Long.MIN_VALUE + 2;
        result = encodeAndDecode(value, 1);
        assertEquals(value, result);

        value = -10;
        result = encodeAndDecode(value, 1);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_2_bytes()
            throws Exception {

        long value = 0b1000_1100_0000;
        long result = encodeAndDecode(value, 2);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_3_bytes()
            throws Exception {

        long value = 0b10_0100_0000_1100_0000;
        long result = encodeAndDecode(value, 3);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_4_bytes()
            throws Exception {

        long value = 0b10_0000_0000_0100_0000_1100_0000;
        long result = encodeAndDecode(value, 4);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_5_bytes()
            throws Exception {

        long value = 0b1100_0001_0000_0000_0100_0000_1100_0000;
        long result = encodeAndDecode(value, 5);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_10_bytes()
            throws Exception {

        long value = 0b1100_0001_0000_0000_0100_0000_1100_0000_1100_0001_0000_0000_0100_0000_1100_0000L;
        long result = encodeAndDecode(value, 10);
        assertEquals(value, result);
    }

    private static long encodeAndDecode(long value, int expectedSize)
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        Serializer serializer = createSerializer();

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeCompressedInt64("value", value);
        }

        assertEquals(expectedSize, memoryBuffer.writerIndex());

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            return decoder.readCompressedInt64();
        }
    }

}