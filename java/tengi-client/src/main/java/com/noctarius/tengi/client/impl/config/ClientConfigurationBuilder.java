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
package com.noctarius.tengi.client.impl.config;

import com.noctarius.tengi.client.impl.TransportHandler;
import com.noctarius.tengi.core.config.Configuration;
import com.noctarius.tengi.core.config.ConfigurationBuilder;
import com.noctarius.tengi.core.config.MarshallerConfiguration;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.Transport;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientConfigurationBuilder
        extends ConfigurationBuilder {

    private TransportHandler transportHandler;

    public ClientConfigurationBuilder transportHandler(TransportHandler transportHandler) {
        this.transportHandler = transportHandler;
        return this;
    }

    @Override
    public Configuration build() {
        return new ClientConfigurationImpl(marshallers, transports, transportPorts, sslEnabled, handshakeHandler,
                transportHandler);
    }

    protected static class ClientConfigurationImpl
            extends ConfigurationImpl
            implements ClientConfiguration {

        private final TransportHandler transportHandler;

        protected ClientConfigurationImpl(Set<MarshallerConfiguration> marshallers, List<Transport> transports,
                                          Map<Transport, Integer> transportPorts, boolean sslEnabled,
                                          HandshakeHandler handshakeHandler, TransportHandler transportHandler) {

            super(marshallers, transports, transportPorts, sslEnabled, handshakeHandler);
            this.transportHandler = transportHandler;
        }

        @Override
        public TransportHandler getTransportHandler() {
            return transportHandler;
        }
    }

}
