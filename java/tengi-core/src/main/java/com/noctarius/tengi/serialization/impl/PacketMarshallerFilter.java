package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;

enum PacketMarshallerFilter
        implements MarshallerFilter {

    INSTANCE;

    @Override
    public Result accept(Object object) {
        return object instanceof Packet ? Result.AcceptedAndCache : Result.Next;
    }
}
