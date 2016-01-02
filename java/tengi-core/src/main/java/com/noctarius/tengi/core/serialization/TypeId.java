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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>The <tt>TypeId</tt> annotation class is used to assign a type id to any class that
 * needs type id information, like any subclass of
 * {@link com.noctarius.tengi.core.serialization.Marshallable} or
 * {@link com.noctarius.tengi.core.model.Packet}. Also
 * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}s
 * need to have a registered type id and their implementation might require
 * an additional one for serialized objects if the same <tt>Marshaller</tt>
 * is usable for multiple types.</p>
 * <p>As an alternative of using this annotation, users can choose to implement
 * {@link com.noctarius.tengi.core.serialization.Identifiable} with a generic type
 * of {@link java.lang.Short} to return a legal type id for their class.</p>
 * <p>This annotation is part of the bundled and default serialization framework
 * and might not be required or used for externally developed protocols and codecs.
 * It also always requires the primary type id to be of type short which can change
 * for other protocols, too.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeId {

    /**
     * Returns the assigned type id value.
     *
     * @return the type id
     */
    short value();
}
