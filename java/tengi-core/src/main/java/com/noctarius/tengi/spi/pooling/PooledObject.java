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
 * <p>The <tt>PooledObject</tt> interface describes a wrapper type, wrapping
 * the original object. The real pooled object itself can be retrieved by
 * calling {@link #getObject()} on the returned <tt>PooledObject</tt>
 * instance.</p>
 * <p>This interface is used to simplify and speedup internals of the
 * {@link com.noctarius.tengi.spi.pooling.ObjectPool} implementation and commonly
 * stores additional internal properties.</p>
 *
 * @param <T> the type of the pooled object
 */
public interface PooledObject<T> {

    /**
     * Returns the real pooled object as created by {@link ObjectHandler#create()}.
     *
     * @return the internal pooled object
     */
    T getObject();

}
