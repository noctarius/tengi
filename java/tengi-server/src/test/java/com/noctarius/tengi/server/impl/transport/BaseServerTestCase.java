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

import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.server.Server;
import com.noctarius.tengi.server.ServerTransport;
import org.junit.Test;

import java.net.BindException;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.fail;

public class BaseServerTestCase {

    @Test(expected = BindException.class)
    public void test_server_port_blocked()
            throws Exception {

        ServerSocket socket = new ServerSocket(8080);
        try {
            Configuration configuration = new ConfigurationBuilder().addTransport(ServerTransport.HTTP_TRANSPORT).build();

            Server server = Server.create(configuration);

            try {
                server.start(System.out::println).get();

                fail();

            } catch (ExecutionException e) {
                if (e.getCause() instanceof BindException) {
                    throw (BindException) e.getCause();
                }
            } finally {
                server.stop();
            }
        } finally {
            socket.close();
        }
    }

}
