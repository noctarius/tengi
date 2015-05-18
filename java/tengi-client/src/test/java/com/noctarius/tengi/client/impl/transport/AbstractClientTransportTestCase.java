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
package com.noctarius.tengi.client.impl.transport;

import com.noctarius.tengi.client.Client;
import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.core.listener.ConnectedListener;
import com.noctarius.tengi.server.Server;
import com.noctarius.tengi.core.connection.Transport;

public abstract class AbstractClientTransportTestCase {

    protected static <T> T practice(Client client, ConnectedListener listener, //
                                    Runner<T> runner, boolean ssl, Transport... serverTransports)
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(serverTransports).ssl(ssl).build();
        Server server = Server.create(configuration);
        server.start(AbstractClientTransportTestCase::onConnection);

        try {
            Connection connection = client.connect("localhost", listener).get();
            try {
                T result = runner.run();
                return result;
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } finally {
            server.stop().get();
        }
    }

    private static void onConnection(Connection connection) {
        connection.addMessageListener(AbstractClientTransportTestCase::onMessage);
    }

    private static void onMessage(Connection connection, Message message) {
        try {
            connection.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static interface Runner<T> {
        T run()
                throws Exception;
    }

}
