/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.tengi.core.serialization.debugger;

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.debugger.impl.DefaultSerializationDebugger;
import com.noctarius.tengi.core.serialization.debugger.impl.NoopSerializationDebugger;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.codec.Codec;

/**
 * The <tt>SerializationDebugger</tt> interface defines the common contract for debugging
 * the serialization and deserialization process. It keeps track of the stack frames whenever
 * an element or value is written or read and can exchange certain thread stacktrace elements
 * in case of an exception or error to visualize the problematic element (de-)serialization.
 */
public interface SerializationDebugger {

    /**
     * Pushes a new deserialization stack frame onto the serialization stack.
     *
     * @param decoder  the <tt>Decoder</tt> to read from
     * @param protocol the <tt>Protocol</tt> instance for additional protocol complexity
     */
    default void push(Protocol protocol, Decoder decoder) {
        push(protocol, (Codec) decoder, Process.DESERIALIZE, null);
    }

    /**
     * Pushes a new serialization stack frame onto the serialization stack.
     *
     * @param protocol the <tt>Protocol</tt> instance for additional protocol complexity
     * @param encoder  the <tt>Encoder</tt> that the value will be written to
     * @param value    the value to write
     */
    default void push(Protocol protocol, Encoder encoder, Object value) {
        push(protocol, (Codec) encoder, Process.SERIALIZE, value);
    }

    /**
     * Pushes a new serialization or deserialization stack frame onto the serialization stack.
     *
     * @param protocol the <tt>Protocol</tt> instance for additional protocol complexity
     * @param codec    the <tt>Codec</tt> to read from or the value will be written to
     * @param process  the <tt>Process</tt> type (serialization or deserialization)
     * @param value    the value to write or null when deserialization
     */
    void push(Protocol protocol, Codec codec, Process process, Object value);

    /**
     * Removes the first element from serialization stack.
     */
    void pop();

    /**
     * Tries to fix the given stacktrace and enriches all matching stacktrace frames
     * with the corresponding serialization stack elements.
     *
     * @param throwable the <tt>Throwable</tt> element to fix
     */
    void fixFramesToStackTrace(Throwable throwable);

    /**
     * Returns the static instance for the <tt>SerializationDebugger</tt> depending on
     * serialization debugging is activated or not (later case returns a no_op implementation
     * for the JIT compiler to remove from calls).
     *
     * @return the <tt>SerializationDebugger</tt> instance
     */
    public static SerializationDebugger instance() {
        if (Debugger.ENABLED) {
            return DefaultSerializationDebugger.Holder.INSTANCE;
        }
        return NoopSerializationDebugger.INSTANCE;
    }

    /**
     * The <tt>Process</tt> enum defines the process type for a serialization stack frame.
     */
    public static enum Process {

        /**
         * A serialization stack frame
         */
        SERIALIZE,

        /**
         * A deserialization stack frame
         */
        DESERIALIZE
    }

    /**
     * The <tt>Debugger</tt> class is a simple static configuration element to enable or disable
     * serialization debugging. No finer control is possible but also debugging should only be
     * enabled to find serialization or deserialization issues, it is not meant to run in a
     * production environment.
     */
    public static final class Debugger {

        /**
         * Depending on the value to be <tt>true</tt> or <tt>false</tt> serialization debugging
         * is either activated or deactivated.
         */
        public static boolean ENABLED = false;

        /**
         * Depending on the value to be <tt>true</tt> or <tt>false</tt> the serialization stack frame
         * will not only contain the type of the value and the stacktrace position but also a short
         * debug value which is retrieved using
         * {@link com.noctarius.tengi.core.serialization.debugger.DebuggableMarshaller#debugValue(Object)}.
         */
        public static boolean STORE_VALUES = false;
    }

}
