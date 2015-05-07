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

import com.noctarius.tengi.Identifier;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class UnsafeUtil {

    public static final Unsafe UNSAFE;

    public static final long OBJECT_ARRAY_BASE;
    public static final long OBJECT_ARRAY_INDEXSCALE;
    public static final long OBJECT_ARRAY_SHIFT;

    public static final long IDENTIFIER_DATA_OFFSET;

    static {
        Unsafe unsafe;

        try {
            unsafe = findUnsafe();
        } catch (RuntimeException e) {
            unsafe = null;
        }
        if (unsafe == null) {
            throw new RuntimeException("Incompatible JVM - sun.misc.Unsafe support is missing");
        }

        try {
            Field identifierData = Identifier.class.getDeclaredField("data");
            identifierData.setAccessible(true);
            IDENTIFIER_DATA_OFFSET = unsafe.objectFieldOffset(identifierData);

            OBJECT_ARRAY_BASE = unsafe.arrayBaseOffset(Object[].class);
            OBJECT_ARRAY_INDEXSCALE = unsafe.arrayIndexScale(Object[].class);
            OBJECT_ARRAY_SHIFT = 31 - Integer.numberOfLeadingZeros((int) OBJECT_ARRAY_INDEXSCALE);

        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException();
        }

        UNSAFE = unsafe;
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
