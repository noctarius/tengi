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
package com.noctarius.tengi.client.impl.transport.websocket;

import com.noctarius.tengi.client.Client;
import com.noctarius.tengi.client.ClientTransports;
import com.noctarius.tengi.client.impl.transport.AbstractClientTransportTestCase;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.exception.ConnectionFailedException;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.core.listener.MessageListener;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.model.Packet;
import com.noctarius.tengi.server.ServerTransports;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebsocketTransportTestCase
        extends AbstractClientTransportTestCase {

    @Test
    public void test_simple_tcp_connection()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransports.WEBSOCKET_TRANSPORT).build();
        Client client = Client.create(configuration);

        Connection connection = null;
        try {
            CompletableFuture<Connection> f = new CompletableFuture<>();
            connection = practice(client, f::complete, f::get, false, ServerTransports.WEBSOCKET_TRANSPORT);
            assertNotNull(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test(expected = ConnectionFailedException.class)
    public void test_disconnect_http_connection_not_enabled()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransports.WEBSOCKET_TRANSPORT).build();
        Client client = Client.create(configuration);

        Connection connection = null;
        try {
            CompletableFuture<Connection> f = new CompletableFuture<>();
            connection = practice(client, f::complete, f::get, false, ServerTransports.TCP_TRANSPORT);
            assertNotNull(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test(expected = ConnectionFailedException.class)
    public void test_port_not_open()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransports.WEBSOCKET_TRANSPORT).build();
        Client client = Client.create(configuration);

        try {
            CompletableFuture<Connection> future = client.connect("localhost", Assert::assertNull);
            future.get();
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            throw new RuntimeException(cause);
        }
    }

    @Test
    public void test_simple_tcp_simple_round_trip()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransports.WEBSOCKET_TRANSPORT).build();
        Client client = Client.create(configuration);

        CompletableFuture<Message> messageFuture = new CompletableFuture<>();

        Packet packet = new Packet("login");
        packet.setValue("username", "Stan");
        Message message = Message.create(packet);

        MessageListener messageListener = (c, m) -> {
            messageFuture.complete(m);
        };

        ConnectedListener listener = (c) -> {
            try {
                c.addMessageListener(messageListener);
                c.writeObject(message);
            } catch (Exception e) {
                messageFuture.completeExceptionally(e);
            }
        };

        Message result = practice(client, listener, messageFuture::get, false, ServerTransports.WEBSOCKET_TRANSPORT);
        assertNotNull(result);

        Packet p = result.getBody();
        assertNotNull(p);
        assertEquals(packet, p);
    }

    @Test
    public void test_simple_tcp_multi_round_trip()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransports.WEBSOCKET_TRANSPORT).build();
        Client client = Client.create(configuration);

        CompletableFuture<Message> messageFuture = new CompletableFuture<>();

        Packet packet = new Packet("counter");
        packet.setValue("counter", 1);
        Message message = Message.create(packet);

        MessageListener messageListener = (c, m) -> {
            Packet p = m.getBody();
            int counter = p.getValue("counter");
            if (counter == 4) {
                messageFuture.complete(m);
            } else {
                p.setValue("counter", counter + 1);
                try {
                    c.writeObject(p);
                } catch (Exception e) {
                    messageFuture.completeExceptionally(e);
                }
            }
        };

        ConnectedListener listener = (c) -> {
            try {
                c.addMessageListener(messageListener);
                c.writeObject(message);
            } catch (Exception e) {
                messageFuture.completeExceptionally(e);
            }
        };

        Message result = practice(client, listener, messageFuture::get, false, ServerTransports.WEBSOCKET_TRANSPORT);
        assertNotNull(result);

        Packet p = result.getBody();
        assertNotNull(p);
        assertEquals(4, (int) p.getValue("counter"));
    }

}
