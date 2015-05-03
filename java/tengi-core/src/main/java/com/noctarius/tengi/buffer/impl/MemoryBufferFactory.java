package com.noctarius.tengi.buffer.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;

public class MemoryBufferFactory {

    public static MemoryBuffer unpooled(ByteBuf buffer) {
        return new NettyMemoryBuffer((AbstractReferenceCountedByteBuf) buffer);
    }

}
