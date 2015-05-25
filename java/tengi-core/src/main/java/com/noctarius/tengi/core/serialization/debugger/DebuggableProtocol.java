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
package com.noctarius.tengi.core.serialization.debugger;

import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.spi.serialization.Protocol;

/**
 * The <tt>DebuggableProtocol</tt> is a special type of
 * {@link com.noctarius.tengi.spi.serialization.Protocol} that is able
 * to identify types in a byte-stream before the actual deserialization happens.
 */
public interface DebuggableProtocol
        extends Protocol {

    /**
     * <p>Returns the real type of the next object in the stream which can be the simple type
     * for {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} implementations
     * that handle just one type or it can read additional information from the stream to gather
     * necessary type information.</p>
     * <p>If additional read operations are executed, the stream is automatically reset to the
     * position before calling this method. No additional preparation is necessary.</p>
     *
     * @param decoder the <tt>Decoder</tt> to read from
     * @param <T>     the type of the object following up in the stream
     * @return the type of the next object in the stream
     */
    <T> Class<T> findType(Decoder decoder);

}
