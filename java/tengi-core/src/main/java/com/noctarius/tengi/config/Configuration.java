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
package com.noctarius.tengi.config;

import com.noctarius.tengi.Transport;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.serialization.marshaller.MarshallerReader;
import com.noctarius.tengi.serialization.marshaller.MarshallerWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.noctarius.tengi.serialization.marshaller.Marshaller.marshaller;

public interface Configuration {

    Set<MarshallerConfiguration> getMarshallers();

    Set<Transport> getTransports();

    public static final class Builder {

        private final Set<MarshallerConfiguration> marshallers = new HashSet<>();
        private final Set<Transport> transports = new HashSet<>();

        public Builder addMarshaller(MarshallerFilter marshallerFilter, Marshaller marshaller) {
            marshallers.add(new MarshallerConfiguration(marshallerFilter, marshaller));
            return this;
        }

        public <O, I> Builder addMarshaller(MarshallerFilter marshallerFilter, I marshallerId, //
                                         MarshallerReader<O> reader, MarshallerWriter<O> writer) {

            marshallers.add(new MarshallerConfiguration(marshallerFilter, marshaller(marshallerId, reader, writer)));
            return this;
        }

        public Builder addTransport(Transport transport) {
            transports.add(transport);
            return this;
        }

        public Builder addTransport(Transport... transports) {
            this.transports.addAll(Arrays.asList(transports));
            return this;
        }

        public Configuration build() {
            return new ConfigurationImpl(marshallers, transports);
        }
    }

    static class ConfigurationImpl
            implements Configuration {

        private final Set<MarshallerConfiguration> marshallers;
        private final Set<Transport> transports;

        public ConfigurationImpl(Set<MarshallerConfiguration> marshallers, Set<Transport> transports) {
            this.marshallers = Collections.unmodifiableSet(marshallers);
            this.transports = Collections.unmodifiableSet(transports);
        }

        @Override
        public Set<MarshallerConfiguration> getMarshallers() {
            return marshallers;
        }

        @Override
        public Set<Transport> getTransports() {
            return transports;
        }
    }
}
