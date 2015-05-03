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
package com.noctarius.tengi.config;

import com.noctarius.tengi.serialization.marshaller.Marshaller;
import com.noctarius.tengi.serialization.marshaller.MarshallerFilter;

public final class MarshallerConfiguration {
    private final MarshallerFilter marshallerFilter;
    private final Marshaller marshaller;

    MarshallerConfiguration(MarshallerFilter marshallerFilter, Marshaller marshaller) {
        this.marshallerFilter = marshallerFilter;
        this.marshaller = marshaller;
    }

    public MarshallerFilter getMarshallerFilter() {
        return marshallerFilter;
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }
}
