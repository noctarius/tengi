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
package com.noctarius.tengi.core.serialization.codec.impl.utf8;

import com.noctarius.tengi.core.impl.MathUtil;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerManager;

import java.io.UTFDataFormatException;
import java.lang.reflect.Constructor;

/**
 * Patched and broke down version from Hazelcast UTFEncoderDecoder written
 * by @noctarius, @serkan-ozal and the people who probably fixed the bugs :-)
 * <p>
 * https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/main/java/com/hazelcast/nio/UTFEncoderDecoder.java
 */
public class UTF8Codec {

    private static final Logger LOGGER = LoggerManager.getLogger(UTF8Codec.class);

    private static final int STRING_CHUNK_SIZE = 16 * 1024;

    private static final UTF8Codec INSTANCE;

    static {
        INSTANCE = buildUTF8Codec();
    }

    private final StringCreator stringCreator;
    private final UtfWriter utfWriter;

    UTF8Codec(StringCreator stringCreator, UtfWriter utfWriter) {
        this.stringCreator = stringCreator;
        this.utfWriter = utfWriter;
    }

    public static void writeUTF(Encoder encoder, String value, byte[] buffer)
            throws Exception {

        INSTANCE.writeUTF0(encoder, value, buffer);
    }

    public static String readUTF(Decoder decoder, byte[] buffer)
            throws Exception {

        return INSTANCE.readUTF0(decoder, buffer);
    }

    void writeUTF0(Encoder encoder, String value, byte[] buffer)
            throws Exception {

        if (!MathUtil.isPowerOfTwo(buffer.length)) {
            throw new IllegalArgumentException("Size of the buffer has to be power of two, was " + buffer.length);
        }

        int length = value.length();
        encoder.writeInt32("length1", length);
        encoder.writeInt32("length2", length);
        if (length > 0) {
            int chunkSize = (length / STRING_CHUNK_SIZE) + 1;
            for (int i = 0; i < chunkSize; i++) {
                int beginIndex = Math.max(0, i * STRING_CHUNK_SIZE - 1);
                int endIndex = Math.min((i + 1) * STRING_CHUNK_SIZE - 1, length);
                utfWriter.writeShortUTF(encoder, value, beginIndex, endIndex, buffer);
            }
        }
    }

    String readUTF0(Decoder decoder, byte[] buffer)
            throws Exception {

        if (!MathUtil.isPowerOfTwo(buffer.length)) {
            throw new IllegalArgumentException("Size of the buffer has to be power of two, was " + buffer.length);
        }

        int length = decoder.readInt32();
        int lengthCheck = decoder.readInt32();
        if (length != lengthCheck) {
            throw new UTFDataFormatException("Length check failed, maybe broken bytestream or wrong stream position");
        }
        final char[] data = new char[length];
        if (length > 0) {
            int chunkSize = length / STRING_CHUNK_SIZE + 1;
            for (int i = 0; i < chunkSize; i++) {
                int beginIndex = Math.max(0, i * STRING_CHUNK_SIZE - 1);
                readShortUTF(decoder, data, beginIndex, buffer);
            }
        }
        return stringCreator.buildString(data);
    }

    private void readShortUTF(Decoder decoder, char[] data, int beginIndex, byte[] buffer)
            throws Exception {

        final int utfLength = decoder.readShort() & 0xFFFF;
        // buffer[0] is used to hold read data
        // so actual useful length of buffer is as "length - 1"
        final int minUtfLength = Math.min(utfLength, buffer.length - 1);
        final int bufferLimit = minUtfLength + 1;
        int readCount = 0;
        // We use buffer[0] to hold read data, so position starts from 1
        int bufferPos = 1;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int cTemp = 0;
        int charArrCount = beginIndex;

        // The first readable data is at 1. index since 0. index is used to hold read data.
        decoder.readBytes(buffer, 1, minUtfLength);

        c1 = buffer[bufferPos++] & 0xFF;
        while (bufferPos != bufferLimit) {
            if (c1 > 127) {
                break;
            }
            data[charArrCount++] = (char) c1;
            c1 = buffer[bufferPos++] & 0xFF;
        }

        bufferPos--;
        readCount = bufferPos - 1;

        while (readCount < utfLength) {
            bufferPos = buffered(buffer, bufferPos, utfLength, readCount++, decoder);
            c1 = buffer[0] & 0xFF;
            cTemp = c1 >> 4;
            if (cTemp >> 3 == 0) {
                // ((cTemp & 0xF8) == 0) or (cTemp <= 7 && cTemp >= 0)
                    /* 0xxxxxxx */
                data[charArrCount++] = (char) c1;
            } else if (cTemp == 12 || cTemp == 13) {
                    /* 110x xxxx 10xx xxxx */
                if (readCount + 1 > utfLength) {
                    throw new UTFDataFormatException("malformed input: partial character at end");
                }
                bufferPos = buffered(buffer, bufferPos, utfLength, readCount++, decoder);
                c2 = buffer[0] & 0xFF;
                if ((c2 & 0xC0) != 0x80) {
                    throw new UTFDataFormatException("malformed input around byte " + beginIndex + readCount + 1);
                }
                data[charArrCount++] = (char) (((c1 & 0x1F) << 6) | (c2 & 0x3F));
            } else if (cTemp == 14) {
                    /* 1110 xxxx 10xx xxxx 10xx xxxx */
                if (readCount + 2 > utfLength) {
                    throw new UTFDataFormatException("malformed input: partial character at end");
                }
                bufferPos = buffered(buffer, bufferPos, utfLength, readCount++, decoder);
                c2 = buffer[0] & 0xFF;
                bufferPos = buffered(buffer, bufferPos, utfLength, readCount++, decoder);
                c3 = buffer[0] & 0xFF;
                if (((c2 & 0xC0) != 0x80) || ((c3 & 0xC0) != 0x80)) {
                    throw new UTFDataFormatException("malformed input around byte " + (beginIndex + readCount + 1));
                }
                data[charArrCount++] = (char) (((c1 & 0x0F) << 12) | ((c2 & 0x3F) << 6) | ((c3 & 0x3F)));
            } else {
                    /* 10xx xxxx, 1111 xxxx */
                throw new UTFDataFormatException("malformed input around byte " + (beginIndex + readCount));
            }
        }
    }

    private int buffered(byte[] buffer, int pos, int utfLength, int readCount, Decoder decoder)
            throws Exception {

        try {
            // 0. index of buffer is used to hold read data
            // so copy read data to there.
            buffer[0] = buffer[pos];
            return pos + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Array bounds check by programmatically is not needed like
            // "if (pos < buffer.length)".
            // JVM checks instead of us, so it is unnecessary.
            decoder.readBytes(buffer, 1, Math.min(buffer.length - 1, utfLength - readCount));
            // The first readable data is at 1. index since 0. index is used to
            // hold read data.
            // So the next one will be 2. index.
            buffer[0] = buffer[1];
            return 2;
        }
    }

    static StringCreator createStringCreator() {
        return createStringCreator(true);
    }

    static StringCreator createStringCreator(boolean fastStringEnabled) {
        return fastStringEnabled ? buildFastStringCreator() : new DefaultStringCreator();
    }

    static UtfWriter createUtfWriter() {
        // Try Unsafe based implementation
        UnsafeBasedCharArrayUtfWriter unsafeBasedUtfWriter = new UnsafeBasedCharArrayUtfWriter();
        if (unsafeBasedUtfWriter.isAvailable()) {
            return unsafeBasedUtfWriter;
        }

        // If Unsafe based implementation is not available for usage
        // Try Reflection based implementation
        ReflectionBasedCharArrayUtfWriter reflectionBasedUtfWriter = new ReflectionBasedCharArrayUtfWriter();
        if (reflectionBasedUtfWriter.isAvailable()) {
            return reflectionBasedUtfWriter;
        }

        // If Reflection based implementation is not available for usage
        return new StringBasedUtfWriter();
    }

    private static UTF8Codec buildUTF8Codec() {
        UtfWriter utfWriter = createUtfWriter();
        StringCreator stringCreator = createStringCreator();
        return new UTF8Codec(stringCreator, utfWriter);
    }

    private static StringCreator buildFastStringCreator() {
        try {
            // Give access to the package private String constructor
            Constructor<String> constructor;
            if (UTF8Codec.useOldStringConstructor()) {
                constructor = String.class.getDeclaredConstructor(int.class, int.class, char[].class);
            } else {
                constructor = String.class.getDeclaredConstructor(char[].class, boolean.class);
            }
            if (constructor != null) {
                constructor.setAccessible(true);
                return new FastStringCreator(constructor);
            }
        } catch (Throwable t) {
            LOGGER.trace(t, "No fast string creator seems to available, falling back to reflection");
        }
        return null;
    }

    private static boolean useOldStringConstructor() {
        try {
            Class<String> clazz = String.class;
            clazz.getDeclaredConstructor(int.class, int.class, char[].class);
            return true;
        } catch (Throwable t) {
            LOGGER.trace(t, "Old String constructor doesn't seem available");
        }
        return false;
    }
}
