package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DefaultSerializer
        implements Serializer {

    private final Protocol protocol;

    public DefaultSerializer(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public <O> O readObject(ReadableMemoryBuffer memoryBuffer)
            throws Exception {

        return protocol.readObject(memoryBuffer);
    }

    @Override
    public <O> MemoryBuffer writeObject(O object)
            throws Exception {

        ByteBuf buffer = Unpooled.buffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer);
        writeObject(object, memoryBuffer);
        return memoryBuffer;
    }

    @Override
    public <O> void writeObject(O object, WritableMemoryBuffer memoryBuffer)
            throws Exception {

        protocol.writeObject(object, memoryBuffer);
    }
}
