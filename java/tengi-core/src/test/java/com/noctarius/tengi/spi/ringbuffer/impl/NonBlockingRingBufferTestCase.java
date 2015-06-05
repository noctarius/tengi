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
package com.noctarius.tengi.spi.ringbuffer.impl;

import com.noctarius.tengi.spi.ringbuffer.RingBuffer;
import org.junit.Test;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class NonBlockingRingBufferTestCase {

    @Test
    public void test_write_read_write_read()
            throws Exception {

        Object value1 = new Object();
        Object value2 = new Object();

        RingBuffer<Object> ringBuffer = RingBuffer.create(1);

        assertTrue(ringBuffer.write(value1));
        assertSame(value1, ringBuffer.read());

        assertTrue(ringBuffer.write(value2));
        assertSame(value2, ringBuffer.read());
    }

    @Test
    public void test_read_empty_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);
        assertNull(ringBuffer.read());
    }

    @Test
    public void test_write_reader_greater_writer_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(5);

        for (int i = 0; i < 4; i++) {
            assertTrue(ringBuffer.write(new Object()));
            assertNotNull(ringBuffer.read());
        }
        assertTrue(ringBuffer.write(new Object()));
        assertTrue(ringBuffer.write(new Object()));
    }

    @Test
    public void test_write_reader_greater_writer_full_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(5);

        for (int i = 0; i < 5; i++) {
            assertTrue(ringBuffer.write(new Object()));
        }
        assertNotNull(ringBuffer.read());
        assertTrue(ringBuffer.write(new Object()));
        assertFalse(ringBuffer.write(new Object()));
    }

    @Test
    public void test_write_reader_greater_writer_full_cycle_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(4);

        for (int i = 0; i < 3; i++) {
            assertTrue(ringBuffer.write(new Object()));
            assertNotNull(ringBuffer.read());
        }
        assertTrue(ringBuffer.write(new Object()));
        assertTrue(ringBuffer.write(new Object()));
    }

    @Test
    public void test_read_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);
        Object value = new Object();
        ringBuffer.write(value);

        assertSame(value, ringBuffer.read());
    }

    @Test
    public void test_read_timeout_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);
        Object value = new Object();
        ringBuffer.write(value);

        assertSame(value, ringBuffer.read(1, TimeUnit.NANOSECONDS));
    }

    @Test
    public void test_read_timeout_ringbuffer_become_available()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);

        Semaphore start = new Semaphore(1);
        Semaphore end = new Semaphore(1);

        start.acquire();

        AtomicReference<Object> result = new AtomicReference<>();
        new Thread() {
            @Override
            public void run() {
                try {
                    // Prepare end semaphore
                    end.acquire();

                    // Wait to start
                    start.acquire();

                    result.set(ringBuffer.read(30, TimeUnit.SECONDS));

                } catch (InterruptedException e) {
                    result.set(e);
                } finally {
                    end.release();
                }
            }
        }.start();

        start.release();

        Thread.sleep(2000);

        Object value = new Object();
        ringBuffer.write(value);

        end.acquire();

        assertSame(value, result.get());
    }

    @Test
    public void test_read_timeout_empty_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
        assertNull(ringBuffer.read(1, TimeUnit.SECONDS));
        assertTrue(System.nanoTime() > deadline);
    }

    @Test
    public void test_write_empty_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);
        assertTrue(ringBuffer.write(new Object()));
    }

    @Test
    public void test_write_full_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(1);
        ringBuffer.write(new Object());

        assertFalse(ringBuffer.write(new Object()));
    }

    @Test
    public void test_write_timeout_ringbuffer_become_available()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(1);
        ringBuffer.write(new Object());

        Object value = new Object();

        Semaphore start = new Semaphore(1);
        Semaphore end = new Semaphore(1);

        start.acquire();

        AtomicReference<Object> result = new AtomicReference<>();
        new Thread() {
            @Override
            public void run() {
                try {
                    // Prepare end semaphore
                    end.acquire();

                    // Wait to start
                    start.acquire();

                    result.set(ringBuffer.write(value, 30, TimeUnit.SECONDS));

                } catch (InterruptedException e) {
                    result.set(e);
                } finally {
                    end.release();
                }
            }
        }.start();

        start.release();

        Thread.sleep(2000);

        ringBuffer.read();

        end.acquire();

        assertEquals(Boolean.TRUE, result.get());
        assertSame(value, ringBuffer.read());
    }

    @Test
    public void test_write_timeout_empty_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);
        assertTrue(ringBuffer.write(new Object(), 1, TimeUnit.SECONDS));
    }

    @Test
    public void test_write_double_empty_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(10);
        assertTrue(ringBuffer.write(new Object()));
        assertTrue(ringBuffer.write(new Object()));
    }

    @Test
    public void test_write_timeout_full_ringbuffer()
            throws Exception {

        RingBuffer<Object> ringBuffer = RingBuffer.create(1);
        ringBuffer.write(new Object(), 1, TimeUnit.SECONDS);

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
        assertFalse(ringBuffer.write(new Object(), 1, TimeUnit.SECONDS));
        assertTrue(System.nanoTime() > deadline);
    }

    @Test
    public void test_concurrent_write()
            throws Throwable {

        int concurrencyLevel = 100;

        RingBuffer<Object> ringBuffer = RingBuffer.create(concurrencyLevel);

        Object[] values = new Object[concurrencyLevel];
        for (int i = 0; i < concurrencyLevel; i++) {
            values[i] = new Object();
        }

        AtomicReferenceArray<Object> results = new AtomicReferenceArray<Object>(concurrencyLevel);

        Semaphore start = new Semaphore(concurrencyLevel);
        CountDownLatch end = new CountDownLatch(concurrencyLevel);

        start.acquire(concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    start.acquire();
                    results.set(index, ringBuffer.write(values[index]));
                } catch (Throwable e) {
                    results.set(index, e);
                } finally {
                    end.countDown();
                }
            }).start();
        }

        start.release(concurrencyLevel);
        end.await(20, TimeUnit.SECONDS);

        for (int i = 0; i < concurrencyLevel; i++) {
            Object result = results.get(i);
            if (result instanceof Throwable) {
                throw (Throwable) result;
            }
            assertTrue((Boolean) result);
        }

        Set<Object> readings = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int i = 0; i < concurrencyLevel; i++) {
            readings.add(ringBuffer.read());
        }

        assertEquals(concurrencyLevel, readings.size());
    }

}
