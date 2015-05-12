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
package com.noctarius.tengi.serialization.codec.impl.utf8;

import java.lang.reflect.Constructor;

class FastStringCreator
        implements StringCreator {

    private final Constructor<String> constructor;
    private final boolean useOldStringConstructor;

    public FastStringCreator(Constructor<String> constructor) {
        this.constructor = constructor;
        this.useOldStringConstructor = constructor.getParameterTypes().length == 3;
    }

    @Override
    public String buildString(final char[] chars) {
        try {
            if (useOldStringConstructor) {
                return constructor.newInstance(0, chars.length, chars);
            } else {
                return constructor.newInstance(chars, Boolean.TRUE);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
