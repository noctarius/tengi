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
package com.noctarius.tengi.spi.serialization.codec.impl.utf8;

import java.lang.reflect.Field;

class ReflectionBasedCharArrayUtfWriter
        extends AbstractCharArrayUtfWriter {

    private static final Field VALUE_FIELD;

    static {
        Field field;
        try {
            field = String.class.getDeclaredField("value");
            field.setAccessible(true);
        } catch (Throwable t) {
            field = null;
        }
        VALUE_FIELD = field;
    }

    @Override
    public boolean isAvailable() {
        return VALUE_FIELD != null;
    }

    @Override
    protected char[] getCharArray(String value) {
        try {
            char[] chars = (char[]) VALUE_FIELD.get(value);
            if (chars.length > value.length()) {
                // substring detected!
                // jdk6 substring shares the same value array
                // with the original string (this is not the case for jdk7+)
                // we need to get copy of substring array
                chars = value.toCharArray();
            }
            return chars;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

}
