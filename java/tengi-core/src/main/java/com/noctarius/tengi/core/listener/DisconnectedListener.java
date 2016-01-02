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
package com.noctarius.tengi.core.listener;

import com.noctarius.tengi.core.connection.Connection;

/**
 * The <tt>DisconnectedListener</tt> defines handlers that are able to
 * handle disconnect events of connections to clean up application state,
 * free resources or similar actions.
 */
public interface DisconnectedListener extends Listener {

    /**
     * <p>This method is called whenever a connection was disconnected to
     * clean up application state, free internal resources or similar
     * actions.</p>
     * <p>Event handlers are called in an internal event thread-pool which
     * is not meant to handle long running operations. If long operations
     * need to be executed, offloading to another thread-pool is strongly
     * recommended.</p>
     *
     * @param connection the disconnected <tt>Connection</tt>
     */
    void onDisconnect(Connection connection);

}
