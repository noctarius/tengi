package com.noctarius.tengi.utils.pooling;

public interface PoolValidator<T> {

    boolean isValid(T pooledObject);

}
