package com.noctarius.tengi.serialization.codec.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BitSetCompressorTestCase
        extends AbstractTestCase {

    @Test
    public void test_chunk_type_null_passed_null()
            throws Exception {

        boolean[] result = encodeAndDecode(null, 1);
        assertNull(result);
    }

    @Test
    public void test_chunk_type_null_passed_0_length()
            throws Exception {

        boolean[] result = encodeAndDecode(new boolean[0], 1);
        assertNull(result);
    }

    @Test
    public void test_chunk_type_single_1_value()
            throws Exception {

        boolean[] values = {true};
        boolean[] result = encodeAndDecode(values, 1);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_single_2_values()
            throws Exception {

        boolean[] values = {false, true};
        boolean[] result = encodeAndDecode(values, 1);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_single_3_values()
            throws Exception {

        boolean[] values = {true, false, true};
        boolean[] result = encodeAndDecode(values, 1);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_double_4_values()
            throws Exception {

        boolean[] values = {true, false, false, true};
        boolean[] result = encodeAndDecode(values, 2);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_double_5_values()
            throws Exception {

        boolean[] values = {true, false, false, true, true};
        boolean[] result = encodeAndDecode(values, 2);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_double_10_values()
            throws Exception {

        boolean[] values = {true, false, false, true, false, false, false, true, true, true};
        boolean[] result = encodeAndDecode(values, 2);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_quad_11_values()
            throws Exception {

        boolean[] values = {true, false, false, true, false, false, false, true, true, false, true};
        boolean[] result = encodeAndDecode(values, 3);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_quad_14_values()
            throws Exception {

        boolean[] values = {true, false, false, true, false, false, false, true, true, false, true, false, true, true};
        boolean[] result = encodeAndDecode(values, 4);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_quad_25_values()
            throws Exception {

        boolean[] values = {true, false, false, true, false, false, false, true, true, //
                            false, true, false, true, true, false, true, true, true, false, //
                            true, false, true, true, false, true};

        boolean[] result = encodeAndDecode(values, 4);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_quad_26_values()
            throws Exception {

        boolean[] values = {true, false, false, true, false, false, false, true, true, //
                            false, true, false, true, true, false, true, true, true, false, //
                            true, false, true, true, false, true, true};

        boolean[] result = encodeAndDecode(values, 5);
        assertTrue(Arrays.equals(values, result));
    }

    @Test
    public void test_chunk_type_quad_37_values()
            throws Exception {

        boolean[] values = {true, false, false, true, false, false, false, true, true, //
                            false, true, false, true, true, false, true, true, true, false, //
                            true, false, true, true, false, true, true, false, false, true, false, //
                            true, true, false, true, true, false, true};

        boolean[] result = encodeAndDecode(values, 7);
        assertTrue(Arrays.equals(values, result));
    }

    private static boolean[] encodeAndDecode(boolean[] bitSet, int expectedSize)
            throws Exception {

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        Serializer serializer = createSerializer();

        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBitSet("test", bitSet);
        }

        assertEquals(expectedSize, memoryBuffer.writerIndex());

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            return decoder.readBitSet();
        }
    }

}
