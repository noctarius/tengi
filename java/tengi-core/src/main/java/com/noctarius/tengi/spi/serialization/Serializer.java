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
package com.noctarius.tengi.spi.serialization;

import com.noctarius.tengi.core.config.MarshallerConfiguration;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultSerializer;

import java.util.Collection;

/**
 * The <tt>Serializer</tt> interface is the entry point into any serialization process.
 * It provides functionality to directly read or write objects as well as offering access
 * to {@link com.noctarius.tengi.core.serialization.codec.Encoder}s and
 * {@link com.noctarius.tengi.core.serialization.codec.Decoder}s, both in an auto-closable
 * fashion.
 */
public interface Serializer {

    /**
     * Returns the bound <tt>Protocol</tt> implementation.
     *
     * @return the underlying <tt>Protocol</tt>
     */
    Protocol getProtocol();

    /**
     * <p>Reads the content of a <b>non-null</b> object from the underlying byte-stream buffer. The content itself
     * will be deserialized using a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or as an internally handled object type. It is up to the
     * {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type tag for the
     * object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param <O>       the type of the object to write
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @param decoder   the <tt>Decoder</tt> to read the object from
     * @return the non-null object value read from the buffer
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    <O> O readObject(String fieldName, Decoder decoder)
            throws Exception;

    /**
     * <p>Transfers a <b>non-null</b> object to a new {@link com.noctarius.tengi.spi.buffer.MemoryBuffer} and
     * returns this buffer instance. The content itself will be serialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} ir as an internally handled object
     * type. It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how
     * to tag the type of the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param <O>       the type of the object to write
     * @param object    the object value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @return the <tt>MemoryBuffer</tt> instance that contains the objects content
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.NullPointerException      whenever the given object is null
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    <O> MemoryBuffer writeObject(String fieldName, O object)
            throws Exception;

    /**
     * <p>Transfers a <b>non-null</b> object to the underlying byte-stream buffer. The content itself will
     * be serialized using a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or as an internally handled object type. It is up to the
     * {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to tag the type of the
     * object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param <O>       the type of the object to write
     * @param object    the object value to be written to the buffer
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @param encoder   the <tt>Encoder</tt> to write the object to
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.NullPointerException      whenever the given object is null
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    <O> void writeObject(String fieldName, O object, Encoder encoder)
            throws Exception;

    /**
     * Returns an {@link com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder} instance bound
     * to the underlying protocol and the given {@link com.noctarius.tengi.spi.buffer.MemoryBuffer}.
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to bind
     * @return the auto-closable <tt>Encoder</tt> instance
     */
    AutoClosableEncoder retrieveEncoder(MemoryBuffer memoryBuffer);

    /**
     * Returns an {@link com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder} instance bound
     * to the underlying protocol and the given {@link com.noctarius.tengi.spi.buffer.MemoryBuffer}.
     *
     * @param memoryBuffer the <tt>MemoryBuffer</tt> to bind
     * @return the auto-closable <tt>Decoder</tt> instance
     */
    AutoClosableDecoder retrieveDecoder(MemoryBuffer memoryBuffer);

    /**
     * Creates a new <tt>Serializer</tt> instance with the default
     * {@link com.noctarius.tengi.spi.serialization.Protocol} implementation and binds the given
     * {@link com.noctarius.tengi.core.config.MarshallerConfiguration}s collection.
     *
     * @param marshallerConfigurations the <tt>MarshallerConfiguration</tt> collection to bind
     * @return the <tt>Serializer</tt> instance with bound <tt>Marshaller</tt>s
     */
    public static Serializer create(Collection<MarshallerConfiguration> marshallerConfigurations) {
        return create(new DefaultProtocol(marshallerConfigurations));
    }

    /**
     * Creates a new <tt>Serializer</tt> instance with the given
     * {@link com.noctarius.tengi.spi.serialization.Protocol} implementation.
     *
     * @param protocol the <tt>Protocol</tt> instance to bind
     * @return the <tt>Serializer</tt> instance bound to the given <tt>Protocol</tt>
     */
    public static Serializer create(Protocol protocol) {
        return new DefaultSerializer(protocol);
    }

}
