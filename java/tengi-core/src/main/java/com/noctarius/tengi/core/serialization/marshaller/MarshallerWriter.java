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
package com.noctarius.tengi.core.serialization.marshaller;

import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.serialization.Protocol;

/**
 * <p>The <tt>MarshallerWriter</tt> interface defines the marshalling
 * contract for serializing objects to a given byte-stream. Values
 * are written to a {@link com.noctarius.tengi.core.serialization.codec.Encoder}
 * implementation and additional type information can be written using the
 * provided {@link com.noctarius.tengi.spi.serialization.Protocol} instance.</p>
 * <p>The <tt>Encoder</tt> and <tt>Protocol</tt> implementations rely on the
 * configured serialization framework and implemented un-marshalling should
 * rely on implementation details of one or another serialization.</p>
 *
 * @param <O> the generic value type to be marshalled
 */
public interface MarshallerWriter<O> {

    /**
     * /**
     * <p>This method implements the marshalling algorithm to write the given value's
     * internal state to the provided {@link com.noctarius.tengi.core.serialization.codec.Encoder}
     * instance. Additional protocol complexity such as required type information or child objects
     * can be written with the given <tt>Protocol</tt> instance.</p>
     * <p>Implementations of this method must be fully thread-safe and stateless for multi-threaded
     * usage pattern.</p>
     *
     * @param object   the value to be marshalled
     * @param encoder  the <tt>Encoder</tt> to write to
     * @param protocol the <tt>Protocol</tt> instance for additional protocol complexity
     * @throws Exception whenever any kind of unexpected situation happened while marshalling
     */
    void marshall(O object, Encoder encoder, Protocol protocol)
            throws Exception;

    default void marshall(String fieldName, O object, Encoder encoder, Protocol protocol)
            throws Exception {

        // TODO store field name information
        marshall(object, encoder, protocol);
    }

}
