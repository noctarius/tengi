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
package com.noctarius.tengi.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class UnsafeUtil {

    public static final Unsafe UNSAFE;
    public static final boolean UNSAFE_AVAILABLE;

    static {
        Unsafe unsafe;

        try {
            unsafe = findUnsafe();
        } catch (RuntimeException e) {
            unsafe = null;
        }
        UNSAFE = unsafe;
        UNSAFE_AVAILABLE = UNSAFE != null;
    }

    private static Unsafe findUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException se) {
            return AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
                @Override
                public Unsafe run() {
                    try {
                        Class<Unsafe> type = Unsafe.class;
                        try {
                            Field field = type.getDeclaredField("theUnsafe");
                            field.setAccessible(true);
                            return type.cast(field.get(type));

                        } catch (Exception e) {
                            for (Field field : type.getDeclaredFields()) {
                                if (type.isAssignableFrom(field.getType())) {
                                    field.setAccessible(true);
                                    return type.cast(field.get(type));
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Unsafe unavailable", e);
                    }
                    throw new RuntimeException("Unsafe unavailable");
                }
            });
        }
    }

    private UnsafeUtil() {
    }

}
