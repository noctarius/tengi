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

import com.noctarius.tengi.core.impl.Validate;
import com.noctarius.tengi.core.serialization.marshaller.Marshaller;
import com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter;

/**
 * <p>This final class represents a configured {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
 * bound to its according {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter}.</p>
 * <p>This class is immutable to conform to the configuration contract of unmodifiable configurations.</p>
 */
public final class MarshallerConfiguration {
    private final MarshallerFilter marshallerFilter;
    private final Marshaller marshaller;

    /**
     * Constructs a new immutable instance of this <tt>MarshallerConfiguration</tt> class and stores
     * a {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter} that selects elements
     * to be serialized with the bound {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}.
     *
     * @param marshallerFilter the <tt>MarshallerFilter</tt> to select marshallable elements
     * @param marshaller       the <tt>Marshaller</tt> to marshall the selected elements
     * @throws java.lang.NullPointerException when the given <tt>MarshallerFilter</tt> or <tt>Marshaller</tt> is null
     */
    public MarshallerConfiguration(MarshallerFilter marshallerFilter, Marshaller marshaller) {
        Validate.notNull("marshallerFilter", marshallerFilter);
        Validate.notNull("marshaller", marshaller);
        this.marshallerFilter = marshallerFilter;
        this.marshaller = marshaller;
    }

    /**
     * Returns the bound {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter}.
     *
     * @return the bound <tt>MarshallerFilter</tt>
     */
    public MarshallerFilter getMarshallerFilter() {
        return marshallerFilter;
    }

    /**
     * Returns the bound {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}.
     *
     * @return the bound <tt>Marshaller</tt>
     */
    public Marshaller getMarshaller() {
        return marshaller;
    }

}
