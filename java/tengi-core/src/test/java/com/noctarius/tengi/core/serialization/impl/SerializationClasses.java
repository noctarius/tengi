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
package com.noctarius.tengi.core.serialization.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.core.serialization.Protocol;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.marshaller.Enumerable;

public final class SerializationClasses {

    private SerializationClasses() {
    }

    @TypeId(1000)
    public static class SubPacketWithDefaultConstructor
            extends Packet {

        public SubPacketWithDefaultConstructor() {
            super("SubPacketWithDefaultConstructor");
        }
    }

    @TypeId(1001)
    public static class SubPacketWithoutDefaultConstructor
            extends Packet {

        public SubPacketWithoutDefaultConstructor(String packageName) {
            super(packageName);
        }
    }

    @TypeId(1002)
    public static class SubPacketMarshallException
            extends Packet {

        public SubPacketMarshallException(String packageName) {
            super(packageName);
        }

        @Override
        protected void marshall0(Encoder encoder, Protocol protocol) {
            throw new NullPointerException();
        }
    }

    @TypeId(1003)
    public static class SubPacketUnmarshallException
            extends Packet {

        public SubPacketUnmarshallException(String packageName) {
            super(packageName);
        }

        @Override
        protected void unmarshall0(Decoder decoder, Protocol protocol) {
            throw new NullPointerException();
        }
    }

    @TypeId(1005)
    public static enum TestEnum {
        Value1,
        Value2
    }

    @TypeId(1004)
    public static enum TestEnumerable
            implements Enumerable<TestEnumerable> {

        Value1(10),
        Value2(20);

        private final int flag;

        private TestEnumerable(int flag) {
            this.flag = flag;
        }

        @Override
        public int flag() {
            return flag;
        }
    }

}
