package com.noctarius.tengi.serialization.codec.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Int32CompressorTestCase
        extends AbstractTestCase {

    @Test
    public void test_1_byte()
            throws Exception {

        int value = 0b1001;
        int result = encodeAndDecode(value, 1);
        assertEquals(value, result);
    }

    @Test
    public void test_2_bytes()
            throws Exception {

        int value = 0b1100_1001;
        int result = encodeAndDecode(value, 2);
        assertEquals(value, result);
    }

    @Test
    public void test_3_bytes()
            throws Exception {

        int value = 0b110_0100_1111_1011;
        int result = encodeAndDecode(value, 3);
        assertEquals(value, result);
    }

    @Test
    public void test_4_bytes()
            throws Exception {

        int value = 0b11_0010_0111_1101_1011_1011;
        int result = encodeAndDecode(value, 4);
        assertEquals(value, result);
    }

    @Test
    public void test_5_bytes()
            throws Exception {

        int value = Integer.MAX_VALUE;
        int result = encodeAndDecode(value, 5);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_1_byte()
            throws Exception {

        int value = Integer.MIN_VALUE;
        int result = encodeAndDecode(value, 1);
        assertEquals(value, result);

        value = Integer.MIN_VALUE + 1;
        result = encodeAndDecode(value, 1);
        assertEquals(value, result);

        value = Integer.MIN_VALUE + 2;
        result = encodeAndDecode(value, 1);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_2_bytes()
            throws Exception {

        int value = 0b1000_0000_0000_0000_0000_0000_1100_0000;
        int result = encodeAndDecode(value, 2);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_3_bytes()
            throws Exception {

        int value = 0b1000_0000_0000_0000_0100_0000_1100_0000;
        int result = encodeAndDecode(value, 3);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_4_bytes()
            throws Exception {

        int value = 0b1000_0001_0000_0000_0100_0000_1100_0000;
        int result = encodeAndDecode(value, 4);
        assertEquals(value, result);
    }

    @Test
    public void test_negative_5_bytes()
            throws Exception {

        int value = 0b1100_0001_0000_0000_0100_0000_1100_0000;
        int result = encodeAndDecode(value, 5);
        assertEquals(value, result);
    }

    private static int encodeAndDecode(int value, int expectedSize)
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        Serializer serializer = createSerializer();

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeCompressedInt32("value", value);
        }

        assertEquals(expectedSize, memoryBuffer.writerIndex());

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            return decoder.readCompressedInt32();
        }
    }

}