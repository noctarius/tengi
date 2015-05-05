package com.noctarius.tengi.serialization.debugger;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.marshaller.Marshaller;

public interface DebuggableMarshaller<O>
        extends Marshaller<O> {

    Class<?> findType(ReadableMemoryBuffer memoryBuffer, Protocol protocol);

    String debugValue(Object value);

}
