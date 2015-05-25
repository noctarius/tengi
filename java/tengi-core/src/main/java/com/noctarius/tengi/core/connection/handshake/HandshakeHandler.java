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
package com.noctarius.tengi.core.connection.handshake;

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.spi.connection.packets.Handshake;

/**
 * The <tt>HandshakeHandler</tt> interface describes a handler implementation
 * that, on server-side, is able to verify connection requests and can either
 * accept a handshake or deny it. On client-side it can extract additional
 * information (like data to preload or additional connection-  or version-
 * information).
 */
public interface HandshakeHandler {

    /**
     * <p>Verifies or handles a handshake request or response. On server-side the
     * verification is successful when another instance of <tt>Handshake</tt> is
     * returned (in this case a handshake response) or it is denied if <tt>null</tt>
     * is returned, in the latter case, the connection is closed. If the same handshake
     * instance is returned an {@link java.lang.IllegalStateException} will be thrown
     * to the connection and the connection is closed.</p>
     * <p>On client-side, there is no additional verification step and the return
     * value is ignored. It does not matter what the actual return value will be.</p>
     *
     * @param connectionId the connectionId for the handshake operation
     * @param handshake    the <tt>Handshake</tt> object to handle
     * @return another handshake instance to accept the handshake request, otherwise <tt>null</tt>
     */
    Handshake handleHandshake(Identifier connectionId, Handshake handshake);

}
