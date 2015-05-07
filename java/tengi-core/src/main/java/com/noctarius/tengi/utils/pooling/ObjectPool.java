package com.noctarius.tengi.utils.pooling;

import java.util.function.Consumer;

public interface ObjectPool<T> {

    default PooledObject<T> acquire() {
        return acquire(null);
    }

    PooledObject<T> acquire(Consumer<T> activator);

    default void release(PooledObject<T> object) {
        release(object, null);
    }

    void release(PooledObject<T> object, Consumer<T> passivator);

    void close();

}
