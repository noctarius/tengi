package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Message;
import com.noctarius.tengi.serialization.Cacheable;
import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;

enum MessageMarshallerFilter
        implements MarshallerFilter {

    INSTANCE;

    @Override
    public Result accept(Object object) {
        return object instanceof Message ? Result.AcceptedAndCache : Result.Next;
    }
}
