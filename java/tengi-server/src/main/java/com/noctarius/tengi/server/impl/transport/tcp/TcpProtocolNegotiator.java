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
package com.noctarius.tengi.server.impl.transport.tcp;

import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.ServerConnectionProcessor;
import com.noctarius.tengi.server.impl.transport.base.AbstractBaseProtocolNegotiator;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.buffer.ByteBuf;

class TcpProtocolNegotiator
        extends AbstractBaseProtocolNegotiator {

    static final TcpProtocolNegotiator INSTANCE = new TcpProtocolNegotiator();

    private TcpProtocolNegotiator() {
    }

    @Override
    protected ServerConnectionProcessor<ByteBuf> getConnectionProcessor(ConnectionManager connectionManager,
                                                                        Serializer serializer) {

        return new TcpConnectionProcessor(connectionManager, serializer);
    }
}
