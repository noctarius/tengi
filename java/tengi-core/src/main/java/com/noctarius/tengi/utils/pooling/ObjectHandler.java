package com.noctarius.tengi.utils.pooling;

public interface ObjectHandler<T> {

    T create();

    default void activateObject(T object) {
    }

    default void passivateObject(T object) {
    }

}
