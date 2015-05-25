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

import com.noctarius.tengi.core.serialization.Marshallable;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;

/**
 * The <tt>PollingRequest</tt> class describes a basic packet to request
 * cached messages on non-streaming transports.
 */
@TypeId(DefaultProtocolConstants.TYPEID_POLLING_REQUEST)
public final class PollingRequest
        implements Marshallable {

    @Override
    public void marshall(Encoder encoder, Protocol protocol)
            throws Exception {
    }

    @Override
    public void unmarshall(Decoder decoder, Protocol protocol)
            throws Exception {
    }
}
