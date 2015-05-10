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
package com.noctarius.tengi.serialization.codec.impl;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;

final class Int32Compressor {

    private static final int MASK_SEVEN_BITS = 0b0111_1111;
    private static final int MASK_FIVE_BITS = 0b0001_1111;

    private static final int MASK_NON_SIGNED_VALUE = 0b0111_1111_1111_1111_1111_1111_1111_1111;

    private static final int MASK_SIGNED = 0x80;
    private static final int MASK_INVERTED = 0x40;

    static void writeInt32(int value, WritableMemoryBuffer memoryBuffer) {

        boolean signed = ((value >>> 31) & 0x1) == 1;
        boolean inverted = storeInverted(value);

        if (inverted) {
            value = ~value;
        }

        int bits = (value & MASK_NON_SIGNED_VALUE);
        int leadingZeros = Integer.numberOfLeadingZeros(bits);
        int writeableBits = 32 - leadingZeros;

        int chunks = 1;
        if (writeableBits - 5 > 0) {
            int furtherBits = writeableBits - 5;
            int mostSignificantBits = furtherBits % 7;
            chunks += furtherBits / 7 + (mostSignificantBits != 0 ? 1 : 0);
        }

        byte[] data = new byte[chunks];

        // Store signed and inverted information into the first byte
        data[0] = (byte) ((signed ? 1 : 0) << 7);
        data[0] |= (byte) ((inverted ? 1 : 0) << 6);

        for (int i = chunks - 1; i >= 0; i--) {
            if (i == 0) {
                data[i] |= (byte) ((bits & MASK_FIVE_BITS) << 1);
            } else {
                data[i] = (byte) ((bits & MASK_SEVEN_BITS) << 1);
            }
            if (i < chunks - 1) {
                data[i] |= 0x1;
            }
            bits >>= i == 0 ? 5 : 7;
        }
        memoryBuffer.writeBytes(data);
    }

    static int readInt32(ReadableMemoryBuffer memoryBuffer) {
        int value = 0;
        boolean signed = false;
        boolean inverted = false;
        for (int i = 0; i < 5; i++) {
            byte chunk = memoryBuffer.readByte();

            int bits;
            if (i == 0) {
                signed = (chunk & MASK_SIGNED) == MASK_SIGNED;
                inverted = (chunk & MASK_INVERTED) == MASK_INVERTED;
                bits = ((chunk >> 1) & MASK_FIVE_BITS);
            } else {
                bits = ((chunk >> 1) & MASK_SEVEN_BITS);
            }

            value |= (byte) bits;
            if ((chunk & 0x1) == 0) {
                break;
            }

            value <<= 7;
        }
        if (inverted) {
            // Invert and clean most signification bit again
            value = (~value) & MASK_NON_SIGNED_VALUE;
        }
        return (value | ((signed ? 1 : 0) << 31));
    }

    private static boolean storeInverted(int value) {
        int leadingZerosNonInverted = Integer.numberOfLeadingZeros(value & MASK_NON_SIGNED_VALUE);
        int leadingZerosInverted = Integer.numberOfLeadingZeros((~value) & MASK_NON_SIGNED_VALUE);
        return leadingZerosInverted > leadingZerosNonInverted;
    }

    private Int32Compressor() {
    }

}
