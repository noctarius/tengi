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
package com.noctarius.tengi.core.config;

import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.serialization.marshaller.Marshaller;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerReader;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.noctarius.tengi.core.serialization.marshaller.Marshaller.marshaller;

/**
 * <p>The <tt>ConfigurationBuilder</tt> is designed to create an immutable instance of the
 * {@link com.noctarius.tengi.core.config.Configuration} interface. It supports a fluent
 * configuration style and collects all necessary properties or uses some meaningful
 * defaults.</p>
 * <p>The internal design of the <tt>ConfigurationBuilder</tt> is not thread-safe and
 * therefore it is not recommended to use if from multiple threads concurrently.</p>
 */
public class ConfigurationBuilder {

    protected final Set<MarshallerConfiguration> marshallers = new HashSet<>();
    protected final List<Transport> transports = new ArrayList<>();
    protected final Map<Transport, Integer> transportPorts = new HashMap<>();
    protected boolean sslEnabled = false;
    protected boolean gzipEnabled = false;
    protected boolean snappyEnabled = false;
    protected HandshakeHandler handshakeHandler = null;

    /**
     * Configures a new {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} and
     * a corresponding {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter}
     * to be injected into the internal serializer framework.
     *
     * @param marshallerFilter the <tt>MarshallerFilter</tt> to be bound
     * @param marshaller       the <tt>Marshaller</tt> to be bound
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder addMarshaller(MarshallerFilter marshallerFilter, Marshaller marshaller) {
        marshallers.add(new MarshallerConfiguration(marshallerFilter, marshaller));
        return this;
    }

    /**
     * <p>Configures a new {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} and
     * a corresponding {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter}
     * to be injected into the internal serializer framework. This method supports Java 8 Lambdas and
     * the bound <tt>Marshaller</tt> is internally created by the given
     * {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerReader} and
     * {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerWriter}.</p>
     * <p>The given <tt>marshallerId</tt> will be written to the byte-stream to identify
     * the created <tt>Marshaller</tt>.</p>
     *
     * @param marshallerFilter the <tt>MarshallerFilter</tt> to be bound
     * @param marshallerId     the id for the generated <tt>Marshaller</tt>
     * @param reader           the <tt>MarshallerReader</tt> to be delegated to for un-marshalling
     * @param writer           the <tt>MarshallerWriter</tt> to be delegated to for marshalling
     * @param <O>              the value type to be marshalled and un-marshalled
     * @param <I>              the type of the marshaller ID
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public <O, I> ConfigurationBuilder addMarshaller(MarshallerFilter marshallerFilter, I marshallerId, //
                                                     MarshallerReader<O> reader, MarshallerWriter<O> writer) {

        marshallers.add(new MarshallerConfiguration(marshallerFilter, marshaller(marshallerId, reader, writer)));
        return this;
    }

    /**
     * <p>Adds a {@link com.noctarius.tengi.core.connection.Transport} to the possible transports.
     * The transport will be added to the current index of the internal list. Transport will later
     * be connected in this very order.</p>
     * <p>If the same transport is added multiple times to the configuration it will be tried multiple
     * times as well. There is no duplication check and duplicates are fully supported.</p>
     *
     * @param transport the <tt>Transport to be added</tt>
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder addTransport(Transport transport) {
        transports.add(transport);
        return this;
    }

    /**
     * <p>Adds one or more {@link com.noctarius.tengi.core.connection.Transport}s to the possible transports.
     * The transports will be added to the current index of the internal list. Transport will later
     * be connected in this very order.</p>
     * <p>If the same transport is added multiple times to the configuration it will be tried multiple
     * times as well. There is no duplication check and duplicates are fully supported.</p>
     *
     * @param transports the <tt>Transport</tt>s to be added
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder addTransport(Transport... transports) {
        this.transports.addAll(Arrays.asList(transports));
        return this;
    }

    /**
     * Configures an explicit mapping of a port (for example TCP or UDP) to the given
     * {@link com.noctarius.tengi.core.connection.Transport}. Any previously configured
     * port for this very transport is overridden. Any configuration also overrides the
     * default port from the transport. The port must be between 1 and 65535, restriction
     * from the operating system might apply and define a different range of usable port
     * numbers.
     *
     * @param transport the <tt>Transport</tt> to bind to the port
     * @param port      the port to bind the <tt>Transport</tt> to
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder transportPort(Transport transport, int port) {
        Validate.greaterOrEqual("port", 1, port);
        Validate.lowerOrEqual("port", 65535, port);
        transportPorts.put(transport, port);
        return this;
    }

    /**
     * Defines if the {@link com.noctarius.tengi.core.connection.Transport}s should use SSL.
     * This only applies to transports that support SSL encrypted connection. Please refer to
     * any <tt>Transport</tt> documentation to find out if SSL is supported or not. Calling this
     * method multiple times will override any previously set value.
     *
     * @param sslEnabled true to enable SSL, false to disable it
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder ssl(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
        return this;
    }

    /**
     * Defines if the {@link com.noctarius.tengi.core.connection.Transport}s should use GZIP compression.
     * This only applies to transports that support GZIP compression connection. Please refer to
     * any <tt>Transport</tt> documentation to find out if GZIP compression is supported or not.
     * Calling this method multiple times will override any previously set value.
     *
     * @param gzipEnabled true to enable GZIP compression, false to disable it
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder gzip(boolean gzipEnabled) {
        this.gzipEnabled = gzipEnabled;
        return this;
    }

    /**
     * Defines if the {@link com.noctarius.tengi.core.connection.Transport}s should use Snappy compression.
     * This only applies to transports that support Snappy compression connection. Please refer to
     * any <tt>Transport</tt> documentation to find out if Snappy compression is supported or not.
     * Calling this method multiple times will override any previously set value.
     *
     * @param snappyEnabled true to enable Snappy compression, false to disable it
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder snappy(boolean snappyEnabled) {
        this.snappyEnabled = snappyEnabled;
        return this;
    }

    /**
     * Defines the {@link com.noctarius.tengi.core.connection.HandshakeHandler} instance
     * to verify, accept or deny new connection handshakes. On client-side additional information can be
     * extracted from the handshake response retrieved from the server.
     *
     * @param handshakeHandler the <tt>HandshakeHandler</tt> instance to be configured
     * @return this instance of the <tt>ConfigurationBuilder</tt> for fluent programing style
     */
    public ConfigurationBuilder handshakeHandler(HandshakeHandler handshakeHandler) {
        this.handshakeHandler = handshakeHandler;
        return this;
    }

    /**
     * Build the {@link com.noctarius.tengi.core.config.Configuration} instance with any values currently
     * set in this <tt>ConfigurationBuilder</tt> instance. The created configuration is immutable and
     * can't be changed afterwards.
     *
     * @return an immutable <tt>Configuration</tt> instance bound to the prior configured settings
     */
    public Configuration build() {
        return new ConfigurationImpl( //
                marshallers, transports, transportPorts, sslEnabled, gzipEnabled, snappyEnabled, handshakeHandler);
    }

    protected static class ConfigurationImpl
            implements Configuration {

        private final Set<MarshallerConfiguration> marshallers;
        private final List<Transport> transports;
        private final Map<Transport, Integer> transportPorts;
        private final boolean sslEnabled;
        private boolean gzipEnabled = false;
        private boolean snappyEnabled = false;
        private final HandshakeHandler handshakeHandler;

        protected ConfigurationImpl(Set<MarshallerConfiguration> marshallers, List<Transport> transports,
                                    Map<Transport, Integer> transportPorts, boolean sslEnabled, boolean gzipEnabled,
                                    boolean snappyEnabled, HandshakeHandler handshakeHandler) {

            this.marshallers = Collections.unmodifiableSet(new HashSet<>(marshallers));
            this.transports = Collections.unmodifiableList(new ArrayList<>(transports));
            this.transportPorts = Collections.unmodifiableMap(new HashMap<>(transportPorts));
            this.sslEnabled = sslEnabled;
            this.gzipEnabled = gzipEnabled;
            this.snappyEnabled = snappyEnabled;
            this.handshakeHandler = handshakeHandler;
        }

        @Override
        public Set<MarshallerConfiguration> getMarshallers() {
            return marshallers;
        }

        @Override
        public List<Transport> getTransports() {
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

        @Override
        public boolean isSslEnabled() {
            return sslEnabled;
        }

        @Override
        public boolean isGzipEnabled() {
            return gzipEnabled;
        }

        @Override
        public boolean isSnappyEnabled() {
            return snappyEnabled;
        }

        @Override
        public HandshakeHandler getHandshakeHandler() {
            return handshakeHandler;
        }
    }

}
