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

/**
 * The <tt>ExceptionListener</tt> defines handles that are able to
 * handle exceptions thrown from any operation based on a known
 * connection.
 */
public interface ExceptionListener {

    /**
     * <p>This method is called whenever an operations, that is assigned to
     * known connection, has thrown an exception.</p>
     * <p>Event handlers are called in an internal event thread-pool which
     * is not meant to handle long running operations. If long operations
     * need to be executed, offloading to another thread-pool is strongly
     * recommended.</p>
     *
     * @param connection the connected <tt>Connection</tt>
     * @param throwable  the thrown <tt>Throwable</tt>
     */
    void onExceptionally(Connection connection, Throwable throwable);

}
