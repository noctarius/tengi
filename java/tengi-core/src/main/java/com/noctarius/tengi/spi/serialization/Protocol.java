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

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerReader;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerWriter;

/**
 * <p>The <tt>Protocol</tt> interface describes a common wire-protocol for
 * object translation. It handles writing and translation of type-ids as
 * well as the common contract for nullable and non-nullable objects.</p>
 * <p>In addition, protocols normally take care of registered
 * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}s
 * and type registration.</p>
 */
public interface Protocol {

    /**
     * Returns the mime-type of the <tt>Protocol</tt> for HTTP based protocols.
     *
     * @return the protocol's mime-type
     */
    String getMimeType();

    /**
     * Writes the type-id of the given object to the encoder. The default
     * protocol uses the type <tt>short</tt> for type-ids but other protocols
     * are free to use any other type.
     *
     * @param value   the value of which the type-id will be written
     * @param encoder the <tt>Encoder</tt> to write the type-id to
     */
    void writeTypeId(Object value, Encoder encoder);

    /**
     * <p>Resolves the next type-id in the stream (represented by the <tt>Decoder</tt>)
     * to a class. If the class cannot be found an {@link java.lang.ClassNotFoundException}
     * is thrown.</p>
     * <p>The default protocol uses the type <tt>short</tt> for type-ids but other protocols
     * are free to use any other type.</p>
     *
     * @param decoder the <tt>Decoder</tt> to read the type-id from
     * @param <T>     the type of the following up element
     * @return the class of the following up type
     */
    <T> Class<T> readTypeId(Decoder decoder);

    /**
     * <p>Resolves the next type-id in the stream (represented by the <tt>Decoder</tt>)
     * to a class first and then tries to instantiate it. This can be used from
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} implementations
     * to first create an instance of the following up element and then read back in the
     * values.</p>
     *
     * @param decoder the <tt>Decoder</tt> to read the type-id from
     * @param <T>     the type of the following up element
     * @return the instance of the following up type
     */
    <T> T readTypeObject(Decoder decoder);

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
     * <p>Transfers a <b>nullable</b> object to the underlying byte-stream buffer. A marker bit is written
     * to the stream to identify the object was <tt>null</tt> or actual object content is about to follow up.
     * The content itself will be serialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object
     * type. It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to
     * tag the type of the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param <O>     the type of the object to write
     * @param object  the object value to be written to the buffer
     * @param encoder the <tt>Encoder</tt> to write to
     * @param writer  the <tt>MarshallerWriter</tt> to write the object's content
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default <O> void writeNullable(O object, Encoder encoder, MarshallerWriter<O> writer)
            throws Exception {

        if (object == null) {
            encoder.writeByte(0);
            return;
        }
        encoder.writeByte(1);
        writer.marshall(object, encoder, this);
    }

    /**
     * <p>Reads the content of a <b>nullable</b> object from the underlying byte-stream buffer. A marker bit is read
     * from the stream to identify the object was <tt>null</tt> or actual object content is about to follow up. The
     * content itself will be deserialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object type.
     * It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type
     * tag for the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     *
     * @param <O>     the type of the object to read
     * @param decoder the <tt>Decoder</tt> to read the object from
     * @param reader  the <tt>MarshallerReader</tt> to read the object's content
     * @return the an object value read from the buffer or null
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default <O> O readNullable(Decoder decoder, MarshallerReader<O> reader)
            throws Exception {

        if (decoder.readByte() == 1) {
            return reader.unmarshall(decoder, this);
        }
        return null;
    }

    /**
     * <p>Transfers a <b>nullable</b> object to the underlying byte-stream buffer. A marker bit is written
     * to the stream to identify the object was <tt>null</tt> or actual object content is about to follow up.
     * The content itself will be serialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object
     * type. It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to
     * tag the type of the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to store all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might decide
     * to write the value to the stream but is not required to.</p>
     *
     * @param <O>       the type of the object to write
     * @param fieldName the name of the field to be written, strictly for debugging purpose only
     * @param object    the object value to be written to the buffer
     * @param encoder   the <tt>Encoder</tt> to write to
     * @param writer    the <tt>MarshallerWriter</tt> to write the object's content
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to store all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default <O> void writeNullable(String fieldName, O object, Encoder encoder, MarshallerWriter<O> writer)
            throws Exception {

        if (object == null) {
            encoder.writeByte("nullable", 0);
            return;
        }
        encoder.writeByte("nullable", 1);
        writer.marshall(fieldName, object, encoder, this);
    }

    /**
     * <p>Reads the content of a <b>nullable</b> object from the underlying byte-stream buffer. A marker bit is read
     * from the stream to identify the object was <tt>null</tt> or actual object content is about to follow up. The
     * content itself will be deserialized using a registered
     * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} or as an internally handled object type.
     * It is up to the {@link com.noctarius.tengi.spi.serialization.codec.Codec} implementation on how to read a type
     * tag for the object inside the data stream.</p>
     * <p>If the underlying buffer is to small to read all of the content, an
     * {@link java.lang.IndexOutOfBoundsException} is thrown.</p>
     * <p><b>The given <tt>fieldName</tt> is strictly used for debugging purpose.</b> The implementation
     * of the {@link com.noctarius.tengi.core.serialization.debugger.SerializationDebugger} might have
     * decided to write the value to the stream but this is not required.</p>
     *
     * @param <O>       the type of the object to read
     * @param fieldName the name of the field to be read, strictly for debugging purpose only
     * @param decoder   the <tt>Decoder</tt> to read the object from
     * @param reader    the <tt>MarshallerReader</tt> to read the object's content
     * @return the an object value read from the buffer or null
     * @throws java.lang.IndexOutOfBoundsException whenever the buffer is too small to read all elements
     * @throws java.lang.Exception                 whenever any other unexpected situation occurs
     */
    default <O> O readNullable(String fieldName, Decoder decoder, MarshallerReader<O> reader)
            throws Exception {

        if (decoder.readByte("nullable") == 1) {
            return reader.unmarshall(fieldName, decoder, this);
        }
        return null;
    }

}
