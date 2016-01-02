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
package com.noctarius.tengi.core.impl;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class FutureUtil {

    private FutureUtil() {
    }

    public static <T> CompletableFuture<T> executeAsync(CatchingSupplier<T> supplier) {
        return new CompletableFuture<T>().supplyAsync(supplier);
    }

    public static interface CatchingSupplier<T>
            extends Supplier<T> {

        default T get() {
            try {
                return getOrThrow();
            } catch (Throwable e) {
                e.printStackTrace();
                UnsafeUtil.UNSAFE.throwException(e);
            }
            // Never happens
            return null;
        }

        T getOrThrow()
                throws Throwable;
    }

}
