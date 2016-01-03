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
package com.noctarius.tengi.client.impl.transport;

import com.noctarius.tengi.client.Client;
import com.noctarius.tengi.client.impl.TransportHandler;
import com.noctarius.tengi.client.impl.config.ClientConfigurationBuilder;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.server.ServerTransports;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.noctarius.tengi.client.ClientTransports.HTTP_TRANSPORT;
import static com.noctarius.tengi.client.ClientTransports.TCP_TRANSPORT;
import static com.noctarius.tengi.client.ClientTransports.WEBSOCKET_TRANSPORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleTransportTestCase
        extends AbstractClientTransportTestCase {

    @Test
    public void test_client_multi_protocol_connection_order()
            throws Exception {

        Transport[] transports = new Transport[]{TCP_TRANSPORT, WEBSOCKET_TRANSPORT, HTTP_TRANSPORT};

        AtomicInteger index = new AtomicInteger(0);
        AtomicReference<Transport>[] foundTransports = new AtomicReference[transports.length];
        AtomicBoolean[] successes = new AtomicBoolean[transports.length];

        TransportHandler transportHandler = (connector, success, throwable) -> {
            int i = index.getAndIncrement();
            foundTransports[i] = new AtomicReference<>(connector);
            successes[i] = new AtomicBoolean(success);
        };

        Configuration configuration = new ClientConfigurationBuilder() //
                .transportHandler(transportHandler) //
                .addTransport(transports) //
                .build();

        Client client = Client.create(configuration);

        Connection connection = null;
        try {
            CompletableFuture<Connection> f = new CompletableFuture<>();
            connection = practice(client, f::complete, f::get, false, ServerTransports.HTTP_TRANSPORT);
            assertNotNull(connection);

            for (int i = 0; i < transports.length; i++) {
                assertEquals(transports[i].getName(), foundTransports[i].get().getName());
            }

            assertFalse(successes[0].get());
            assertFalse(successes[1].get());
            assertTrue(successes[2].get());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

}
