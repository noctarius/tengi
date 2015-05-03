package com.noctarius.tengi.serialization;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.config.MarshallerConfiguration;
import com.noctarius.tengi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.serialization.impl.DefaultSerializer;

import java.util.Collection;

public interface Serializer {

    Protocol getProtocol();

    <O> O readObject(ReadableMemoryBuffer memoryBuffer)
            throws Exception;

    <O> MemoryBuffer writeObject(O object)
            throws Exception;

    <O> void writeObject(O object, WritableMemoryBuffer memoryBuffer)
            throws Exception;

    public static Serializer create(Collection<MarshallerConfiguration> marshallerConfigurations) {
        return create(new DefaultProtocol(marshallerConfigurations));
    }

    public static Serializer create(Protocol protocol) {
        return new DefaultSerializer(protocol);
    }

}
