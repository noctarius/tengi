package com.noctarius.tengi.serialization.debugger.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.debugger.SerializationDebugger;

public enum NoopSerializationDebugger
        implements SerializationDebugger {

    INSTANCE;

    @Override
    public void push(Protocol protocol, MemoryBuffer memoryBuffer, Process process, Object value) {
    }

    @Override
    public void pop() {
    }

    @Override
    public void fixFramesToStackTrace(Throwable throwable) {
    }

}
