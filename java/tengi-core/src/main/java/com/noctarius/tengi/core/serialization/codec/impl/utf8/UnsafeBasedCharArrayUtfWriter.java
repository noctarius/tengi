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

import com.noctarius.tengi.core.impl.UnsafeUtil;
import sun.misc.Unsafe;

class UnsafeBasedCharArrayUtfWriter
        extends AbstractCharArrayUtfWriter {

    private static final Unsafe UNSAFE = UnsafeUtil.UNSAFE;
    private static final long VALUE_FIELD_OFFSET;

    static {
        long offset = -1;
        try {
            offset = UNSAFE.objectFieldOffset(String.class.getDeclaredField("value"));
        } catch (Throwable t) {
            // Ignore
        }
        VALUE_FIELD_OFFSET = offset;
    }

    @Override
    public boolean isAvailable() {
        return VALUE_FIELD_OFFSET != -1;
    }

    @Override
    protected char[] getCharArray(String value) {
        char[] chars = (char[]) UNSAFE.getObject(value, VALUE_FIELD_OFFSET);
        if (chars.length > value.length()) {
            // substring detected!
            // jdk6 substring shares the same value array
            // with the original string (this is not the case for jdk7+)
            // we need to get copy of substring array
            chars = value.toCharArray();
        }
        return chars;
    }

}
