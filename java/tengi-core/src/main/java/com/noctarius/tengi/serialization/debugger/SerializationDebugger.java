package com.noctarius.tengi.serialization.debugger;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.debugger.impl.DefaultSerializationDebugger;
import com.noctarius.tengi.serialization.debugger.impl.NoopSerializationDebugger;

public interface SerializationDebugger {

    default void push(Protocol protocol, ReadableMemoryBuffer memoryBuffer) {
        push(protocol, (MemoryBuffer) memoryBuffer, Process.DESERIALIZE, null);
    }

    default void push(Protocol protocol, WritableMemoryBuffer memoryBuffer, Object value) {
        push(protocol, (MemoryBuffer) memoryBuffer, Process.SERIALIZE, value);
    }

    void push(Protocol protocol, MemoryBuffer memoryBuffer, Process process, Object value);

    void pop();

    void fixFramesToStackTrace(Throwable throwable);

    public static SerializationDebugger create() {
        if (Debugger.ENABLED) {
            return new DefaultSerializationDebugger();
        }
        return NoopSerializationDebugger.INSTANCE;
    }

    public static enum Process {
        SERIALIZE,
        DESERIALIZE
    }

    public static final class Debugger {
        public static boolean ENABLED = false;
        public static boolean STORE_VALUES = false;
    }

}
