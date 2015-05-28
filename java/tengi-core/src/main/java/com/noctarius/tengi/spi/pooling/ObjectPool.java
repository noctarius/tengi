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

import com.noctarius.tengi.spi.pooling.impl.NonBlockingObjectPool;

import java.util.function.Consumer;

/**
 * The <tt>ObjectPool</tt> interface describes the actual object pool implementation
 * itself. It is used to pre-allocate costly or slow creation processes. The object
 * that is pooled needs to be reusable however it is guaranteed to only be used by
 * one acquirer at a given time. After acquisition, if the object is thread-safe,
 * it might be passed to multiple threads and the thread releasing the object must
 * not be the acquisition thread.
 *
 * @param <T> the type of the pooled object
 */
public interface ObjectPool<T> {

    /**
     * <p>Acquires a pooled object instance. The returns pooled instance might be tested
     * using a configured {@link com.noctarius.tengi.spi.pooling.ObjectValidator} and
     * activated using a configured {@link com.noctarius.tengi.spi.pooling.ObjectHandler}
     * before being returned.</p>
     * <p>The object that is returned is guaranteed to be valid at the time of testing
     * however, if based on network connection or other unreliable system, it still might
     * become invalid at any given point after acquisition.</p>
     *
     * @return the pooled element wrapped into a <tt>PooledObject</tt> instance
     */
    default PooledObject<T> acquire() {
        return acquire(null);
    }

    /**
     * <p>Acquires a pooled object instance. The returns pooled instance might be tested
     * using a configured {@link com.noctarius.tengi.spi.pooling.ObjectValidator} and
     * activated using a configured
     * {@link com.noctarius.tengi.spi.pooling.ObjectHandler#activateObject(Object)}
     * before being returned. In addition the given <tt>activator</tt> will be called and
     * can contain additional runtime based logic.</p>
     * <p>The passed in <tt>activator</tt> is always called before the configured
     * {@link com.noctarius.tengi.spi.pooling.ObjectHandler#activateObject(Object)}
     * method.</p>
     * <p>The object that is returned is guaranteed to be valid at the time of testing
     * however, if based on network connection or other unreliable system, it still might
     * become invalid at any given point after acquisition.</p>
     *
     * @param activator the runtime activator to execute against the object
     * @return the pooled element wrapped into a <tt>PooledObject</tt> instance
     */
    PooledObject<T> acquire(Consumer<T> activator);

    /**
     * <p>Releases a <tt>PooledObject</tt> to the pool. The returned object instance might be
     * tested using a configured {@link com.noctarius.tengi.spi.pooling.ObjectValidator} but
     * it might also be delayed to the next acquisition phase. However the object is passivated
     * by calling {@link com.noctarius.tengi.spi.pooling.ObjectHandler#passivateObject(Object)}
     * before returning it to the available pool again.</p>
     *
     * @param object the pooled object to release
     */
    default void release(PooledObject<T> object) {
        release(object, null);
    }

    /**
     * <p>Releases a <tt>PooledObject</tt> to the pool. The returned object instance might be
     * tested using a configured {@link com.noctarius.tengi.spi.pooling.ObjectValidator} but
     * it might also be delayed to the next acquisition phase. However the object is passivated
     * by calling {@link com.noctarius.tengi.spi.pooling.ObjectHandler#passivateObject(Object)}
     * before returning it to the available pool again. In addition the given <tt>passivator</tt>
     * will also be called and can contain additional runtime based logic.</p>
     * <p>The passed in <tt>passivator</tt> is always called before the configured
     * {@link com.noctarius.tengi.spi.pooling.ObjectHandler#passivateObject(Object)}
     * method.</p>
     *
     * @param object     the pooled object to release
     * @param passivator the runtime passivator to execute against the object
     */
    void release(PooledObject<T> object, Consumer<T> passivator);

    /**
     * Closes the pool and destroys all pre-allocated object instances. It will also call the
     * {@link com.noctarius.tengi.spi.pooling.ObjectHandler#destroy(Object)} method with
     * each of the objects to free any externally acquired or allocated resources assigned to
     * the object instance.
     */
    void close();

    /**
     * Creates a new instance of a non-blocking <tt>ObjectPool</tt> using the given parameters.
     * The returned instance is fully thread-safe and can also create more elements than the
     * given size however the internal size is never growing and over-capacity elements are only
     * temporary and will be destroyed immediately when released.
     *
     * @param handler the <tt>ObjectHandler</tt> instance to bind
     * @param size    the internal capacity (not maximum capacity)
     * @param <T>     the type of the pooled object
     * @return the new <tt>ObjectPool</tt> instance
     */
    public static <T> ObjectPool<T> create(ObjectHandler<T> handler, int size) {
        return create(handler, null, size);
    }

    /**
     * Creates a new instance of a non-blocking <tt>ObjectPool</tt> using the given parameters.
     * The returned instance is fully thread-safe and can also create more elements than the
     * given size however the internal size is never growing and over-capacity elements are only
     * temporary and will be destroyed immediately when released.
     *
     * @param handler   the <tt>ObjectHandler</tt> instance to bind
     * @param validator the <tt>ObjectValidator</tt> instance to bind
     * @param size      the internal capacity (not maximum capacity)
     * @param <T>       the type of the pooled object
     * @return the new <tt>ObjectPool</tt> instance
     */
    public static <T> ObjectPool<T> create(ObjectHandler<T> handler, ObjectValidator<T> validator, int size) {
        return new NonBlockingObjectPool<T>(handler, validator, size);
    }

}
