package com.noctarius.tengi.config;

import com.noctarius.tengi.Transport;
import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.serialization.marshaller.MarshallerReader;
import com.noctarius.tengi.serialization.marshaller.MarshallerWriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.noctarius.tengi.serialization.marshaller.Marshaller.marshaller;

public final class ConfigurationBuilder {
    private final Set<MarshallerConfiguration> marshallers = new HashSet<>();
    private final Set<Transport> transports = new HashSet<>();
    private final Map<Transport, Integer> transportPorts = new HashMap<>();

    public ConfigurationBuilder addMarshaller(MarshallerFilter marshallerFilter, Marshaller marshaller) {
        marshallers.add(new MarshallerConfiguration(marshallerFilter, marshaller));
        return this;
    }

    public <O, I> ConfigurationBuilder addMarshaller(MarshallerFilter marshallerFilter, I marshallerId, //
                                                     MarshallerReader<O> reader, MarshallerWriter<O> writer) {

        marshallers.add(new MarshallerConfiguration(marshallerFilter, marshaller(marshallerId, reader, writer)));
        return this;
    }

    public ConfigurationBuilder addTransport(Transport transport) {
        transports.add(transport);
        return this;
    }

    public ConfigurationBuilder addTransport(Transport... transports) {
        this.transports.addAll(Arrays.asList(transports));
        return this;
    }

    public ConfigurationBuilder transportPort(Transport transport, int port) {
        transportPorts.put(transport, port);
        return this;
    }

    public Configuration build() {
        return new ConfigurationImpl(marshallers, transports, transportPorts);
    }

    private static class ConfigurationImpl
            implements Configuration {

        private final Set<MarshallerConfiguration> marshallers;
        private final Set<Transport> transports;
        private final Map<Transport, Integer> transportPorts;

        public ConfigurationImpl(Set<MarshallerConfiguration> marshallers, Set<Transport> transports,
                                 Map<Transport, Integer> transportPorts) {

            this.marshallers = Collections.unmodifiableSet(marshallers);
            this.transports = Collections.unmodifiableSet(transports);
            this.transportPorts = Collections.unmodifiableMap(transportPorts);
        }

        @Override
        public Set<MarshallerConfiguration> getMarshallers() {
            return marshallers;
        }

        @Override
        public Set<Transport> getTransports() {
            return transports;
        }

        @Override
        public Map<Transport, Integer> getTransportPorts() {
            return transportPorts;
        }

        @Override
        public int getTransportPort(Transport transport) {
            Integer port = transportPorts.get(transport);
            if (port != null) {
                return port;
            }
            return transport.getDefaultPort();
        }
    }

}
