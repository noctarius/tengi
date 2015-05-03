package com.noctarius.tengi.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class CompletableFutureUtil {

    private CompletableFutureUtil() {
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
                throws Exception;
    }

}
