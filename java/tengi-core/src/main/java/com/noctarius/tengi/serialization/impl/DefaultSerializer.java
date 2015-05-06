/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.serialization.debugger.SerializationDebugger;
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

        if (!SerializationDebugger.Debugger.ENABLED) {
            return memoryBuffer.readObject();

        } else {
            try {
                return memoryBuffer.readObject();

            } catch (Exception e) {
                SerializationDebugger debugger = SerializationDebugger.create();
                debugger.fixFramesToStackTrace(e);
                throw e;
            }
        }
    }

    @Override
    public <O> MemoryBuffer writeObject(O object)
            throws Exception {

        if (!SerializationDebugger.Debugger.ENABLED) {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);
            writeObject(object, memoryBuffer);
            return memoryBuffer;

        } else {
            try {
                ByteBuf buffer = Unpooled.buffer();
                MemoryBuffer memoryBuffer = MemoryBufferFactory.unpooled(buffer, protocol);
                writeObject(object, memoryBuffer);
                return memoryBuffer;

            } catch (Exception e) {
                SerializationDebugger debugger = SerializationDebugger.create();
                debugger.fixFramesToStackTrace(e);
                throw e;
            }
        }
    }

    @Override
    public <O> void writeObject(O object, WritableMemoryBuffer memoryBuffer)
            throws Exception {

        if (!SerializationDebugger.Debugger.ENABLED) {
            memoryBuffer.writeObject(object);

        } else {
            try {
                memoryBuffer.writeObject(object);

            } catch (Exception e) {
                SerializationDebugger debugger = SerializationDebugger.create();
                debugger.fixFramesToStackTrace(e);
                throw e;
            }
        }
    }
}
