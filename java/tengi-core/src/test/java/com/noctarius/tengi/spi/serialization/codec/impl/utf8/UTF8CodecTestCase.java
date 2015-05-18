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
package com.noctarius.tengi.spi.serialization.codec.impl.utf8;

import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.testing.AbstractTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.UTFDataFormatException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class UTF8CodecTestCase
        extends AbstractTestCase {

    private static final Random RANDOM = new Random();

    @Parameterized.Parameters(name = "fastString={0}, utfWriterType={1}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{ //
                                             {false, UtfWriterType.DEFAULT}, {false, UtfWriterType.UNSAFE}, //
                                             {false, UtfWriterType.REFLECTION}, {true, UtfWriterType.DEFAULT}, //
                                             {true, UtfWriterType.UNSAFE}, {true, UtfWriterType.REFLECTION}});
    }

    private final boolean fastStringEnabled;
    private final UtfWriterType utfWriterType;

    public UTF8CodecTestCase(boolean fastStringEnabled, UtfWriterType utfWriterType) {
        this.fastStringEnabled = fastStringEnabled;
        this.utfWriterType = utfWriterType;
    }

    @Test
    public void test_empty_text()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();
        MemoryBuffer memoryBuffer = createMemoryBuffer();

        byte[] buffer = new byte[1024];
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            utf8Codec.writeUTF0(encoder, "", buffer);
            utf8Codec.writeUTF0(encoder, "some other value", buffer);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            String result1 = utf8Codec.readUTF0(decoder, buffer);
            String result2 = utf8Codec.readUTF0(decoder, buffer);

            assertEquals("", result1);
            assertEquals("some other value", result2);
        }
    }

    @Test
    public void test_multiple_texts_in_a_row()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];
        for (int i = 0; i < 100; i++) {
            MemoryBuffer memoryBuffer = createMemoryBuffer();

            String[] values = new String[10];
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                for (int o = 0; o < values.length; o++) {
                    values[o] = randomString(i);
                    utf8Codec.writeUTF0(encoder, values[o], buffer);
                }
            }

            try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
                for (int o = 0; o < values.length; o++) {
                    String result = utf8Codec.readUTF0(decoder, buffer);
                    assertEquals(values[o], result);
                }
            }
        }
    }

    @Test
    public void test_short_sized_text_1_chunk()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];
        for (int i = 2; i < 100; i += 2) {
            MemoryBuffer memoryBuffer = createMemoryBuffer();

            String randomString = randomString(i * 100);
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                utf8Codec.writeUTF0(encoder, randomString, buffer);
            }

            try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
                String result = utf8Codec.readUTF0(decoder, buffer);
                assertEquals(randomString, result);
            }
        }
    }

    @Test
    public void test_mid_sized_text_2_chunks()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];
        for (int i = 170; i < 300; i += 2) {
            MemoryBuffer memoryBuffer = createMemoryBuffer();

            String randomString = randomString(i * 100);
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                utf8Codec.writeUTF0(encoder, randomString, buffer);
            }

            try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
                String result = utf8Codec.readUTF0(decoder, buffer);
                assertEquals(randomString, result);
            }
        }
    }

    @Test
    public void test_long_sized_text_3_chunks()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];
        for (int i = 330; i < 900; i += 5) {
            MemoryBuffer memoryBuffer = createMemoryBuffer();

            String randomString = randomString(i * 100);
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                utf8Codec.writeUTF0(encoder, randomString, buffer);
            }

            try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
                String result = utf8Codec.readUTF0(decoder, buffer);
                assertEquals(randomString, result);
            }
        }
    }

    @Test
    public void test_multibyte_char_at_position_that_even_multiple_of_buffer_size()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];
        for (int i : new int[]{50240, 100240, 80240}) {
            MemoryBuffer memoryBuffer = createMemoryBuffer();

            String originalString = createString(i);
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                utf8Codec.writeUTF0(encoder, originalString, buffer);
            }

            try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
                String result = utf8Codec.readUTF0(decoder, buffer);
                assertEquals(originalString, result);
            }
        }
    }

    @Test(expected = NegativeArraySizeException.class)
    public void test_integer_overflow_on_broken_packet()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        // Create broken packet (byte stream)!
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeInt32("length1", Integer.MAX_VALUE + 1);
            encoder.writeInt32("length2", Integer.MAX_VALUE + 1);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            utf8Codec.readUTF0(decoder, buffer);
        }
    }

    @Test(expected = UTFDataFormatException.class)
    public void test_length_check_on_broken_packet()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];

        MemoryBuffer memoryBuffer = createMemoryBuffer();

        // Create broken packet (byte stream)!
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("null", false);
            encoder.writeInt32("length1", Integer.MAX_VALUE + 1);
            encoder.writeInt32("length2", Integer.MAX_VALUE);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            utf8Codec.readUTF0(decoder, buffer);
        }
    }

    @Test
    public void test_substring()
            throws Exception {

        UTF8Codec utf8Codec = newUTF8Codec(fastStringEnabled, utfWriterType);
        if (utf8Codec == null) {
            System.err.println("Ignoring test... " + utfWriterType + " is not available!");
            return;
        }

        Serializer serializer = createSerializer();

        byte[] buffer = new byte[1024];
        String original = "1234abcd";
        String substring = original.substring(4);

        MemoryBuffer memoryBuffer = createMemoryBuffer();
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            utf8Codec.writeUTF0(encoder, substring, buffer);
        }

        try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
            String result = decoder.readString();
            assertEquals(substring, result);
        }
    }

    private static UTF8Codec newUTF8Codec(boolean fastStringEnabled, UtfWriterType utfWriterType) {
        UtfWriter utfWriter;
        switch (utfWriterType) {
            case UNSAFE:
                UnsafeBasedCharArrayUtfWriter unsafeBasedWriter = new UnsafeBasedCharArrayUtfWriter();
                if (!unsafeBasedWriter.isAvailable()) {
                    return null;
                }
                utfWriter = unsafeBasedWriter;
                break;

            case REFLECTION:
                ReflectionBasedCharArrayUtfWriter reflectionBasedWriter = new ReflectionBasedCharArrayUtfWriter();
                if (!reflectionBasedWriter.isAvailable()) {
                    return null;
                }
                utfWriter = reflectionBasedWriter;
                break;

            default:
                utfWriter = new StringBasedUtfWriter();
        }

        StringCreator stringCreator = UTF8Codec.createStringCreator(fastStringEnabled);
        if (fastStringEnabled) {
            assertTrue(stringCreator.getClass().toString().contains("FastStringCreator"));
        }

        return new UTF8Codec(stringCreator, utfWriter);
    }

    private static String createString(int length) {
        char[] c = new char[length];
        for (int i = 0; i < c.length; i++) {
            c[i] = 'a';
        }
        c[10240] = 'å';
        return new String(c);
    }

    private static String randomString(int count) {
        return randomString(count, 0, 0, false, false, null, RANDOM);
    }

    private static String randomAlphaNumeric(int count) {
        return randomString(count, 0, 0, true, true, null, RANDOM);
    }

    /*
     * Thanks to Apache Commons:
     * org.apache.commons.lang.RandomStringUtils
     */
    private static String randomString(int count, int start, int end, boolean letters, //
                                       boolean numbers, char[] chars, Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (start == 0 && end == 0) {
            end = 'z' + 1;
            start = ' ';
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }

        char[] buffer = new char[count];
        int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (letters && Character.isLetter(ch) || numbers && Character.isDigit(ch) || !letters && !numbers) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

    private static enum UtfWriterType {
        UNSAFE,
        REFLECTION,
        DEFAULT
    }

}
