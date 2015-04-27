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
package com.noctarius.tengi;

import com.noctarius.tengi.utils.Validate;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>The <tt>Identifier</tt> is a 128bit unique identifier
 * which can be used to uniquely identify resources like
 * connections, messages or objects.</p>
 * <p>Identifiers should be created using the static methods
 * of this class and can probably be pooled for resource
 * efficient programming models.</p>
 */
public final class Identifier {

    private static final ThreadLocal<Random> RANDOMIZERS = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            // Using the same way as the OpenJDK version just to
            // make sure this happens on every JDK implementation
            // since there are some out there that just use System.currentTimeMillis()
            return new Random(seedUniquifier() ^ System.nanoTime());
        }
    };

    private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);
    private static final long MOTHER_OF_MAGIC_NUMBERS = 181783497276652981L;

    private static final char[] CHARS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70};

    private final byte[] data;

    private Identifier(byte[] data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Identifier other = (Identifier) obj;
        if (!Arrays.equals(data, other.data))
            return false;
        return true;
    }

    @Override
    public String toString() {
        char[] chars = new char[36];
        int index = 0;
        for (int i = 0; i < 16; i++) {
            if (i == 4 || i == 6 || i == 8 || i == 10) {
                chars[index++] = '-';
            }
            chars[index++] = CHARS[(data[i] & 0xF0) >>> 4];
            chars[index++] = CHARS[(data[i] & 0x0F)];
        }
        return new String(chars);
    }

    /**
     * Generates and returns a new unique identifier consisting of 16 random bytes. The
     * bytes content corresponds to the v4 UUID specification but has a special serialization
     * strategy for higher speed and is optimized for lower overhead.
     *
     * @return a new Identifier instance with random bytes
     */
    public static Identifier randomIdentifier() {
        return new Identifier(randomBytes());
    }

    public static Identifier fromBytes(byte[] data) {
        Validate.notNull("data", data);
        Validate.equals("data.length", 16, data.length);
        return new Identifier(data);
    }

    private static long seedUniquifier() {
        // L'Ecuyer, "Tables of Linear Congruential Generators of
        // Different Sizes and Good Lattice Structure", 1999
        for (; ; ) {
            long current = SEED_UNIQUIFIER.get();
            long next = current * MOTHER_OF_MAGIC_NUMBERS;
            if (SEED_UNIQUIFIER.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    private static byte[] randomBytes() {
        byte[] data = new byte[16];
        RANDOMIZERS.get().nextBytes(data);
        /* clear version        */
        data[6] &= 0x0f;
        /* set to version 4     */
        data[6] |= 0x40;
        /* clear variant        */
        data[8] &= 0x3f;
        /* set to IETF variant  */
        data[8] |= 0x80;
        return data;
    }
}
