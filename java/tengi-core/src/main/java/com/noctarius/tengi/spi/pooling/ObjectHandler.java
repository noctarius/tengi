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
package com.noctarius.tengi.spi.pooling;

/**
 * <p>The <tt>ObjectHandler</tt> interface describes the object factory
 * as well as a system to add additional logic on acquisition or
 * release of an object.</p>
 * <p>Additional logic can range from simple state preparation and cleanup
 * to complex resource allocation or de-allocation.</p>
 *
 * @param <T> the type of the pooled object
 */
public interface ObjectHandler<T> {

    /**
     * Returns a <b>new</b> instance of the object type. This is a typical
     * factory method and is called by the
     * {@link com.noctarius.tengi.spi.pooling.ObjectPool} on creation to
     * pre-allocate object instances or whenever an object instance is
     * invalid and a new instance has to be retrieved.
     *
     * @return the <b>new</b> object instance
     */
    T create();

    /**
     * Destroys the given pooled object instance. This method is used to
     * free any kind of external resource before leaving the object itself
     * to the Garbage Collector to be collected.
     *
     * @param object the object instance to destroy
     */
    default void destroy(T object) {
    }

    /**
     * Handles activation process for a pre-allocated object. The object might
     * allocate additional resources or setup / clean internal state.
     *
     * @param object the pooled object instance to activate
     */
    default void activateObject(T object) {
    }

    /**
     * Handled deactivation process for a pre-allocated object. The object must
     * de-allocate any previously allocated resource ({@link #activateObject(Object)}
     * and might cleanup internal state.
     *
     * @param object the pooled object instance to passivate
     */
    default void passivateObject(T object) {
    }

}
