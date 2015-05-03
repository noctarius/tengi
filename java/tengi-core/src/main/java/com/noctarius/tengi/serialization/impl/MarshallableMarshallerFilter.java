package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.serialization.Cacheable;
import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;

enum MarshallableMarshallerFilter
        implements MarshallerFilter {

    INSTANCE;

    @Override
    public Result accept(Object object) {
        if (!(object instanceof Marshallable)) {
            return Result.Next;
        }
        return object.getClass().isAnnotationPresent(Cacheable.class) ? Result.AcceptedAndCache : Result.Accepted;
    }
}
