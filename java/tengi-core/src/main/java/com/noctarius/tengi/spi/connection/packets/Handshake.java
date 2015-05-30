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
package com.noctarius.tengi.spi.connection.packets;

import com.noctarius.tengi.core.model.Packet;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;

/**
 * The <tt>Handshake</tt> is the internal packet to start a new connection handshake
 * or to respond to a previous handshake request. A <tt>Handshake</tt> instance can
 * be customized (sub-classed) to add additional values (like a version number or
 * anything) and handled by a {@link com.noctarius.tengi.core.connection.HandshakeHandler}.
 */
@TypeId(DefaultProtocolConstants.TYPEID_HANDSHAKE)
public class Handshake
        extends Packet {

    /**
     * Constructs a new <tt>Handshake</tt> instance.
     */
    public Handshake() {
        super("Handshake");
    }
}
