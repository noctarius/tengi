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
package com.noctarius.tengi.spi.ringbuffer.impl;

import com.noctarius.tengi.core.impl.UnsafeUtil;
import com.noctarius.tengi.spi.ringbuffer.RingBuffer;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NonBlockingRingBuffer<E>
        implements RingBuffer<E> {

    private static final long ARRAY_BASE = UnsafeUtil.OBJECT_ARRAY_BASE;
    private static final long ARRAY_SHIFT = UnsafeUtil.OBJECT_ARRAY_SHIFT;
    private static final Unsafe UNSAFE = UnsafeUtil.UNSAFE;

    private static final long READER_OFFSET;
    private static final long WRITER_OFFSET;

    static {
        try {
            Field field = NonBlockingRingBuffer.class.getDeclaredField("readerIndex");
            field.setAccessible(true);
            READER_OFFSET = UNSAFE.objectFieldOffset(field);
            field = NonBlockingRingBuffer.class.getDeclaredField("writerIndex");
            field.setAccessible(true);
            WRITER_OFFSET = UNSAFE.objectFieldOffset(field);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Lock readerLock = new ReentrantLock();
    private final Condition readCondition = readerLock.newCondition();

    private final Lock writerLock = new ReentrantLock();
    private final Condition writeCondition = writerLock.newCondition();

    private final int capacity;
    private final Object[] elements;

    // Only updated through sun.misc.Unsafe
    private volatile int readerIndex = 0;
    private volatile int writerIndex = 0;

    public NonBlockingRingBuffer(int capacity) {
        this.capacity = capacity;
        this.elements = new Object[capacity];
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean write(E element) {
        while (true) {
            int writerIndex = this.writerIndex;
            int readerIndex = this.readerIndex;

            int writerSlot = slot(writerIndex);
            int readerSlot = slot(readerIndex);

            if (!slotAvailable(writerIndex, readerIndex, writerSlot, readerSlot)) {
                return false;
            }

            if (UNSAFE.compareAndSwapInt(this, WRITER_OFFSET, writerIndex, writerIndex + 1)) {
                long arrayOffset = offset(writerSlot);

                if (!UNSAFE.compareAndSwapObject(elements, arrayOffset, null, element)) {
                    throw new ConcurrentModificationException("Illegal concurrent ringbuffer update");
                }
                notifyWaitingReaders();
                return true;
            }
        }
    }

    private boolean slotAvailable(int writerIndex, int readerIndex, int writerSlot, int readerSlot) {
        if (writerIndex == 0 && readerIndex == 0) {
            return true;
        }
        if (writerIndex == capacity && readerIndex == 0) {
            return false;
        }
        if (writerIndex > readerIndex && writerSlot == readerSlot) {
            return false;
        }
        return true;
    }

    @Override
    public boolean write(E element, long timeout, TimeUnit timeUnit)
            throws InterruptedException {

        long deadline = System.nanoTime() + timeUnit.toNanos(timeout);
        while (true) {
            if (write(element)) {
                return true;
            }

            if (deadlineReached(deadline)) {
                return false;
            }

            writerLock.lock();
            try {
                writeCondition.await(remainingDeadline(deadline), TimeUnit.NANOSECONDS);
            } finally {
                writerLock.unlock();
            }

            if (deadlineReached(deadline)) {
                return false;
            }
        }
    }

    @Override
    public E read() {
        while (true) {
            int writerIndex = this.writerIndex;
            int readerIndex = this.readerIndex;

            if (readerIndex == -1 && writerIndex == 0) {
                return null;
            }

            if (readerIndex >= writerIndex) {
                return null;
            }

            int position = readerIndex == -1 ? 0 : readerIndex;
            if (UNSAFE.compareAndSwapInt(this, READER_OFFSET, readerIndex, position + 1)) {

                int readerSlot = slot(position);
                long arrayOffset = offset(readerSlot);

                E element = (E) UNSAFE.getObjectVolatile(elements, arrayOffset);
                if (element == null) {
                    // Try until object is available
                    do {
                        element = (E) UNSAFE.getObjectVolatile(elements, arrayOffset);
                    } while (element == null);
                }

                if (!UNSAFE.compareAndSwapObject(elements, arrayOffset, element, null)) {
                    throw new ConcurrentModificationException("Illegal concurrent ringbuffer update");
                }
                notifyWaitingWriters();
                return element;
            }
        }
    }

    @Override
    public E read(long timeout, TimeUnit timeUnit)
            throws InterruptedException {

        long deadline = System.nanoTime() + timeUnit.toNanos(timeout);
        while (true) {
            E element = read();
            if (element != null) {
                return element;
            }

            if (deadlineReached(deadline)) {
                return null;
            }

            readerLock.lock();
            try {
                readCondition.await(remainingDeadline(deadline), TimeUnit.NANOSECONDS);
            } finally {
                readerLock.unlock();
            }

            if (deadlineReached(deadline)) {
                return null;
            }
        }
    }

    @Override
    public void clear() {

    }

    private long offset(long index) {
        return (index << ARRAY_SHIFT) + ARRAY_BASE;
    }

    private int slot(int index) {
        return index % capacity;
    }

    long remainingDeadline(long deadline) {
        return System.nanoTime() - deadline;
    }

    boolean deadlineReached(long deadline) {
        return System.nanoTime() - deadline > 0;
    }

    private void notifyWaitingWriters() {
        writerLock.lock();
        try {
            writeCondition.signal();
        } finally {
            writerLock.unlock();
        }
    }

    private void notifyWaitingReaders() {
        readerLock.lock();
        try {
            readCondition.signal();
        } finally {
            readerLock.unlock();
        }
    }

}
