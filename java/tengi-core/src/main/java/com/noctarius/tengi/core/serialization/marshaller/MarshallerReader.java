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

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.spi.serialization.Protocol;

/**
 * <p>The <tt>MarshallerReader</tt> interface defines the un-marshalling
 * contract for de-serializing objects from a given byte-stream. Values
 * are read from a {@link com.noctarius.tengi.core.serialization.codec.Decoder}
 * implementation and additional type information are available using the
 * provided {@link com.noctarius.tengi.spi.serialization.Protocol} instance.</p>
 * <p>The <tt>Decoder</tt> and <tt>Protocol</tt> implementations rely on the
 * configured serialization framework and implemented un-marshalling should
 * rely on implementation details of one or another serialization.</p>
 *
 * @param <O> the generic value type to be un-marshalled
 */
public interface MarshallerReader<O> {

    /**
     * <p>This method implements the un-marshalling algorithm to read a previously marshalled
     * internal state from the given {@link com.noctarius.tengi.core.serialization.codec.Decoder}
     * instance. Additional protocol complexity such as required type information or child objects
     * can be retrieved from the given <tt>Protocol</tt> instance.</p>
     * <p>Implementations of this method must be fully thread-safe and stateless for multi-threaded
     * usage pattern.</p>
     *
     * @param decoder  the <tt>Decoder</tt> to read from
     * @param protocol the <tt>Protocol</tt> instance for additional protocol complexity
     * @return the un-marshalled value or null
     * @throws Exception whenever any kind of unexpected situation happened while un-marshalling
     */
    O unmarshall(Decoder decoder, Protocol protocol)
            throws Exception;

    default O unmarshall(String fieldName, Decoder decoder, Protocol protocol)
            throws Exception {

        // TODO store field name information
        return unmarshall(decoder, protocol);
    }

}
