package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.utils.pooling.ObjectHandler;

class CodecObjectHandler
        implements ObjectHandler<DefaultCodec> {

    private final Protocol protocol;

    CodecObjectHandler(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public DefaultCodec create() {
        return new DefaultCodec(protocol);
    }

    @Override
    public void passivateObject(DefaultCodec object) {
        object.setMemoryBuffer(null);
    }
}
