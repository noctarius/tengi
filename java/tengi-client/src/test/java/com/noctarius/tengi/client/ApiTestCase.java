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

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.core.serialization.Protocol;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.spi.connection.Connection;

import java.util.concurrent.CompletableFuture;

public class ApiTestCase {

    public static void main(String[] args)
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
        CompletableFuture<Connection> future = client.connect("127.0.0.1", System.out::println);
        future.get();
    }

    private static void write(String fieldName, Object object, Encoder encoder, Protocol protocol)
            throws Exception {

        encoder.writeByte("length", 10);
        protocol.writeNullable("value", object, encoder, (n, o, e, p) -> {
            ((MyWritable) object).write(encoder);
        });
    }

    private static Object read(Decoder decoder, Protocol protocol)
            throws Exception {

        int typeId = decoder.readByte();
        if (typeId != 10) {
            throw new IllegalStateException();
        }
        return protocol.readNullable(decoder, (d, p) -> {
            MyWritable myWritable = new MyWritable();
            myWritable.read(decoder);
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
        void write(Encoder encoder) {
        }

        void read(Decoder decoder) {
        }
    }

}
