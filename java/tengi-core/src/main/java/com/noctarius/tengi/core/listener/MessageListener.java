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
package com.noctarius.tengi.core.listener;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Message;

/**
 * <p>The <tt>MessageListener</tt> interfaces describes a handler that is
 * able to capture arriving messages and handle them accordingly.
 * Together with the {@link com.noctarius.tengi.core.model.Message} it will retrieve
 * the {@link com.noctarius.tengi.core.connection.Connection} the message is retrieved on.</p>
 * <p>MessageListeners are executed inside a special thread-pool and expected
 * to not execute long running operations. If an operation needs to do heavy
 * calculation or waiting it should be offloaded to another thread-pool to
 * prevent the incoming messages from being stuck.</p>
 */
public interface MessageListener {

    /**
     * This method is called whenever a new message arrives on one of the transports.
     * The method is not meant to execute long running operations but may offload those
     * to another thread-pool to free the listener thread-pool as quickly as possible.
     *
     * @param connection the connection that retrieved the message
     * @param message    the message instance that was retrieved
     */
    void onMessage(Connection connection, Message message);

}
