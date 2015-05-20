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

public interface Protocol {

    String getMimeType();

    void writeTypeId(Object value, Encoder encoder);

    <T> Class<T> readTypeId(Decoder decoder);

    <T> T readTypeObject(Decoder decoder);

    <O> O readObject(Decoder decoder)
            throws Exception;

    <O> void writeObject(String fieldName, O object, Encoder encoder)
            throws Exception;

    default <O> void writeNullable(O object, Encoder encoder, MarshallerWriter<O> writer)
            throws Exception {

        if (object == null) {
            encoder.writeByte(0);
            return;
        }
        encoder.writeByte(1);
        writer.marshall(object, encoder, this);
    }

    default <O> O readNullable(Decoder decoder, MarshallerReader<O> reader)
            throws Exception {

        if (decoder.readByte() == 1) {
            return reader.unmarshall(decoder, this);
        }
        return null;
    }

    default <O> void writeNullable(String fieldName, O object, Encoder encoder, MarshallerWriter<O> writer)
            throws Exception {

        if (object == null) {
            encoder.writeByte("nullable", 0);
            return;
        }
        encoder.writeByte("nullable", 1);
        writer.marshall(fieldName, object, encoder, this);
    }

    default <O> O readNullable(String fieldName, Decoder decoder, MarshallerReader<O> reader)
            throws Exception {

        if (decoder.readByte("nullable") == 1) {
            return reader.unmarshall(fieldName, decoder, this);
        }
        return null;
    }

}
