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
package com.noctarius.tengi.server.impl.transport;

import com.noctarius.tengi.Message;
import com.noctarius.tengi.spi.connection.Transport;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.server.Server;
import com.noctarius.tengi.Connection;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class AbstractLongPollingTransportTestCase {

    protected static <T> T practice(Runner<T> runner, boolean ssl, Transport... serverTransports)
            throws Exception {

        Configuration configuration = new ConfigurationBuilder().addTransport(serverTransports).ssl(ssl).build();
        Server server = Server.create(configuration);
        server.start(AbstractLongPollingTransportTestCase::onConnection);

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            return runner.run();
        } finally {
            group.shutdownGracefully();
            server.stop().get();
        }
    }

    private static void onConnection(Connection connection) {
        connection.addMessageListener(AbstractLongPollingTransportTestCase::onMessage);
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
