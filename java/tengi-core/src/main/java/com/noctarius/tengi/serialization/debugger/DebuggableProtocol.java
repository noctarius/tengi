package com.noctarius.tengi.serialization.debugger;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;

public interface DebuggableProtocol
        extends Protocol {

    <T> Class<T> findType(ReadableMemoryBuffer memoryBuffer);

}
