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
package com.noctrius.tengi.transport.client.impl.http;

import com.noctarius.tengi.client.Client;
import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.config.ConfigurationBuilder;
import com.noctarius.tengi.server.transport.ServerTransport;
import com.noctarius.tengi.transport.ClientTransport;
import com.noctrius.tengi.transport.client.impl.AbstractClientTransportTestCase;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class HttpTransportTestCase
        extends AbstractClientTransportTestCase {

    @Test
    public void test_simple_tcp_connection()
            throws Exception {
        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransport.HTTP_TRANSPORT).build();
        Client client = Client.create(configuration);

        try {
            Client result = practice(() -> {
                CompletableFuture<Client> future = client.connect("localhost", System.out::println);

                return future.get();
            }, false, ServerTransport.TCP_TRANSPORT);

            assertNotNull(result);
            assertSame(client, result);
        } finally {
            client.disconnect();
        }
    }

}
