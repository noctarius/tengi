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
package com.noctarius.tengi.client;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.config.ConfigurationBuilder;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.transport.ClientTransport;
import org.junit.Test;

public class ApiTestCase {

    @Test
    public void testInitialization()
            throws Exception {

        // Create configuration using Builder
        Configuration configuration = new ConfigurationBuilder()

                // Configure custom Marshaller
                .addMarshaller(ApiTestCase::isMarshallable, (short) 100, ApiTestCase::read, ApiTestCase::write)

                        // Configure available transports
                .addTransport(ClientTransport.TCP_TRANSPORT)

                        // Build final configuration
                .build();

        // Create client instance using configuration
        Client client = Client.create(configuration);

        // Connect to remote server
        client.connect("127.0.0.1", System.out::println);
    }

    private static void write(Object object, WritableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        memoryBuffer.writeByte(10);
        protocol.writeNullable(object, memoryBuffer, (o, b, p) -> {
            ((MyWritable) object).write(memoryBuffer);
        });
    }

    private static Object read(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        int typeId = memoryBuffer.readByte();
        if (typeId != 10) {
            throw new IllegalStateException();
        }
        return protocol.readNullable(memoryBuffer, (b, p) -> {
            MyWritable myWritable = new MyWritable();
            myWritable.read(memoryBuffer);
            return myWritable;
        });
    }

    private static MarshallerFilter.Result isMarshallable(Object object) {
        if (object instanceof MyWritable) {
            return MarshallerFilter.Result.AcceptedAndCache;
        }
        return MarshallerFilter.Result.Next;
    }

    private static class MyWritable {
        void write(WritableMemoryBuffer memoryBuffer) {
        }

        void read(ReadableMemoryBuffer memoryBuffer) {
        }
    }

}
