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

/**
 * <p>The <tt>server</tt> package contains classes necessary to create a server instance inside applications.
 * It also defines a the server side {@link com.noctarius.tengi.core.connection.Transport}
 * implementations.</p>
 * <p>A server can be created by creating a {@link com.noctarius.tengi.core.config.Configuration}
 * and passing it to the static factory method.</p>
 * <pre>
 *     Configuration configuration = new ConfigurationBuilder()
 *         .addTransport(ServerTransport.TCP_TRANSPORT)
 *         .build();
 *
 *     Server server = Server.create(configuration);
 * </pre>
 * <p>For more information please see {@link com.noctarius.tengi.server.Server}.</p>
 */
package com.noctarius.tengi.server;