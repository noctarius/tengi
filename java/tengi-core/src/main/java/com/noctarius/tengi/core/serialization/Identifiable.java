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
package com.noctarius.tengi.core.serialization;

/**
 * <p>The <tt>Identifiable</tt> interface is used to assign a type id to any class that
 * needs type id information, like any subclass of
 * {@link com.noctarius.tengi.core.serialization.Marshallable} or
 * {@link com.noctarius.tengi.core.model.Packet}. Also
 * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}s
 * need to have a registered type id and their implementation might require
 * an additional one for serialized objects if the same <tt>Marshaller</tt>
 * is usable for multiple types.</p>
 * <p>As an alternative of using this interface, users can choose to annotate their types
 * with the {@link com.noctarius.tengi.core.serialization.TypeId} annotation
 * to return a legal type id for their class.</p>
 * <p>The bundled and default serialization framework requires a generic type of
 * {@link java.lang.Short} which might not be required or used for externally developed
 * protocols and codecs.</p>
 *
 * @param <I> the generic type of the type id
 */
public interface Identifiable<I> {

    /**
     * Returns the assigned type id value.
     *
     * @return the type id
     */
    I identifier();

}
