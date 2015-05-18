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
package com.noctarius.tengi.core.serialization;

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.serialization.Protocol;

/**
 * <p>The <tt>Marshallable</tt> interface defines a common type
 * for objects that have knowledge about how to be serialized
 * or de-serialized. This mechanism can be used for high performance
 * and low latency serialization since this special system is
 * optimized internally.</p>
 * <p>Another option is to implement external serialization using the
 * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} interface which
 * supports external serialization to implement immutable types or integrate
 * other serialization frameworks.</p>
 */
public interface Marshallable {

    /**
     * This method implements logic to marshall (serialize) this object
     * into a stream of bytes using the given {@link com.noctarius.tengi.core.serialization.codec.Encoder}.
     * The given {@link com.noctarius.tengi.spi.serialization.Protocol} instance
     * might be used to build complex stream graphs.
     *
     * @param encoder  Encoder to write to
     * @param protocol Protocol instance for additional protocol complexity
     */
    void marshall(Encoder encoder, Protocol protocol)
            throws Exception;

    /**
     * This method implements logic to un-marshall (de-serialize) this
     * object from a stream of bytes using the given {@link com.noctarius.tengi.core.serialization.codec.Decoder}.
     * The given {@link Protocol} instance
     * might be used to handle complex stream graphs.
     *
     * @param decoder  Decoder to read from
     * @param protocol Protocol instance for additional protocol complexity
     */
    void unmarshall(Decoder decoder, Protocol protocol)
            throws Exception;

}
