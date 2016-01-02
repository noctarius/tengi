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
package com.noctarius.tengi.spi.buffer;

/**
 * The <tt>MemoryBuffer</tt> interface describes the combination of
 * a readable and writeable memory buffer. It supports reading and writing
 * at the same time by keeping two independent index positions but is not
 * implemented in a thread-safe way, therefore it should be used by multiple
 * threads concurrently.
 */
public interface MemoryBuffer
        extends ReadableMemoryBuffer, WritableMemoryBuffer {

    /**
     * Returns the current capacity of this buffer.
     *
     * @return the current capacity
     */
    int capacity();

    /**
     * Returns the maximum capacity of this buffer.
     *
     * @return the maximum capacity
     */
    int maxCapacity();

    /**
     * Removes all content from this buffer and resets the index positions for
     * reader and writer index to 0.
     */
    void clear();

    /**
     * Returns a duplicate of this buffer, sharing the same underlying content
     * area. It can be used as an independent view of the content but changes
     * to the original or the duplicate will also be reflected to the other buffer.
     *
     * @return a new view of the same content with independent indexes
     */
    MemoryBuffer duplicate();

    /**
     * Acquires a lock on the buffer to prevent garbage collection or eager freeing
     * of native memory areas.
     */
    void lock();

    /**
     * Releases a previously acquired lock.
     */
    void release();

    /**
     * Returns <tt>true</tt> if the buffer is already released, otherwise it returns
     * <tt>false</tt>.
     *
     * @return true if buffer is released, otherwise false
     */
    boolean isReleased();

    /**
     * Returns <tt>true</tt> if all locks are either freed or no lock was hold on the buffer,
     * otherwise it returns <tt>false</tt>.
     *
     * @return true if the buffer is releasable, otherwise false
     */
    boolean isReleasable();

}
