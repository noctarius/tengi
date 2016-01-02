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
package com.noctarius.tengi.spi.pooling;

/**
 * The <tt>ObjectValidator</tt> interface describes an optional validation
 * logic to test if an pre-allocated (pooled) object is still valid. Over
 * the objects lifetime some object types (like database connections) might
 * become invalid and need to be recreated and freed. This can be achieved
 * by assigning a validator to the {@link com.noctarius.tengi.spi.pooling.ObjectPool}
 * instance upon creation.
 *
 * @param <T> the type of the pooled object
 */
public interface ObjectValidator<T> {

    /**
     * Returns <tt>true</tt> when the given object instance is still valid
     * (for example if the database connection in still connected) and
     * <tt>false</tt> if not. Returning <tt>false</tt> instructs the
     * {@link com.noctarius.tengi.spi.pooling.ObjectPool} instance to
     * automatically recreate an object instance and replace the invalid one.
     *
     * @param object the pooled object instance to test
     * @return <tt>true</tt> if given object instance is valid, otherwise false
     */
    boolean isValid(T object);

}
