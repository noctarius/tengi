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
package com.noctarius.tengi.client.impl.transport.tcp;

import com.noctarius.tengi.Message;
import com.noctarius.tengi.Packet;
import com.noctarius.tengi.client.Client;
import com.noctarius.tengi.client.ClientTransport;
import com.noctarius.tengi.client.impl.transport.AbstractClientTransportTestCase;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.core.listener.MessageListener;
import com.noctarius.tengi.core.listener.connection.ConnectedListener;
import com.noctarius.tengi.server.ServerTransport;
import com.noctarius.tengi.Connection;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TcpTransportTestCase
        extends AbstractClientTransportTestCase {

    @Test
    public void test_simple_tcp_connection()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransport.TCP_TRANSPORT).build();
        Client client = Client.create(configuration);

        Connection connection = null;
        try {
            CompletableFuture<Connection> f = new CompletableFuture<>();
            connection = practice(client, f::complete, f::get, false, ServerTransport.TCP_TRANSPORT);
            assertNotNull(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Test
    public void test_simple_tcp_simple_round_trip()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransport.TCP_TRANSPORT).build();
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

        Message result = practice(client, listener, messageFuture::get, false, ServerTransport.TCP_TRANSPORT);
        assertNotNull(result);

        Packet p = result.getBody();
        assertNotNull(p);
        assertEquals(packet, p);
    }

    @Test
    public void test_simple_tcp_multi_round_trip()
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(ClientTransport.TCP_TRANSPORT).build();
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

        Message result = practice(client, listener, messageFuture::get, false, ServerTransport.HTTP_TRANSPORT);
        assertNotNull(result);

        Packet p = result.getBody();
        assertNotNull(p);
        assertEquals(4, (int) p.getValue("counter"));
    }

}
