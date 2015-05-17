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
package com.noctarius.tengi.core.pooling.impl;

import com.noctarius.tengi.core.pooling.ObjectHandler;
import com.noctarius.tengi.core.pooling.ObjectPool;
import com.noctarius.tengi.core.pooling.PooledObject;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class NonBlockingObjectPoolTestCase {

    @Test
    public void test_acquire_null_activator_handler_action()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(new ObjectHandler<Value>() {
            @Override
            public Value create() {
                return new Value();
            }

            @Override
            public void activateObject(Value object) {
                object.value = "test";
            }
        }, 10);

        PooledObject<Value> valuePooledObject = pool.acquire();
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
    }

    @Test
    public void test_acquire_null_activator_handler_no_action()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 10);

        PooledObject<Value> valuePooledObject = pool.acquire();
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertNull(valuePooledObject.getObject().value);
    }

    @Test
    public void test_acquire_non_null_activator_handler_action()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(new ObjectHandler<Value>() {
            @Override
            public Value create() {
                return new Value();
            }

            @Override
            public void activateObject(Value object) {
                object.value = "test";
            }
        }, 10);

        PooledObject<Value> valuePooledObject = pool.acquire((v) -> v.value = "test2");
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
    }

    @Test
    public void test_acquire_non_null_activator_handler_no_action()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 10);

        PooledObject<Value> valuePooledObject = pool.acquire((v) -> v.value = "test");
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
    }

    @Test
    public void test_release_null_handler_passivator_action()
            throws Exception {

        final Value value = new Value();
        value.value = "test";

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(new ObjectHandler<Value>() {
            @Override
            public Value create() {
                return value;
            }

            @Override
            public void passivateObject(Value object) {
                object.value = null;
            }
        }, 10);

        PooledObject<Value> valuePooledObject = pool.acquire();
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
        pool.release(valuePooledObject);
        assertNull(value.value);
    }

    @Test
    public void test_release_null_passivator_handler_no_action()
            throws Exception {

        final Value value = new Value();
        value.value = "test";

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(() -> value, 10);

        PooledObject<Value> valuePooledObject = pool.acquire();
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
        pool.release(valuePooledObject);
        assertEquals("test", value.value);
    }

    @Test
    public void test_release_non_null_passivator_handler_action()
            throws Exception {

        final Value value = new Value();
        value.value = "test";

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(new ObjectHandler<Value>() {
            @Override
            public Value create() {
                return value;
            }

            @Override
            public void passivateObject(Value object) {
                object.value = null;
            }
        }, 10);

        PooledObject<Value> valuePooledObject = pool.acquire();
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
        pool.release(valuePooledObject, (v) -> v.value = "test2");
        assertNull(value.value);
    }

    @Test
    public void test_release_non_null_passivator_handler_no_action()
            throws Exception {

        final Value value = new Value();
        value.value = "test";

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(() -> value, 10);

        PooledObject<Value> valuePooledObject = pool.acquire();
        assertNotNull(valuePooledObject);
        assertNotNull(valuePooledObject.getObject());
        assertEquals("test", valuePooledObject.getObject().value);
        pool.release(valuePooledObject, (v) -> v.value = "test2");
        assertEquals("test2", value.value);
    }

    @Test
    public void test_concurrent_acquire()
            throws Throwable {

        int concurrencyLevel = 100;

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, concurrencyLevel);

        AtomicReferenceArray<Object> results = new AtomicReferenceArray<>(concurrencyLevel);

        Semaphore start = new Semaphore(concurrencyLevel);
        CountDownLatch end = new CountDownLatch(concurrencyLevel);

        start.acquire(concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            int index = i;
            new Thread(() -> {
                try {
                    start.acquire();
                    PooledObject<Value> valuePooledObject = pool.acquire();
                    assertTrue(!valuePooledObject.toString().contains("INTERMEDIATE"));
                    results.set(index, valuePooledObject.getObject());
                } catch (Throwable e) {
                    results.set(index, e);
                } finally {
                    end.countDown();
                }
            }).start();
        }

        start.release(concurrencyLevel);
        end.await(20, TimeUnit.SECONDS);

        Set<Value> values = Collections.newSetFromMap(new IdentityHashMap<>());
        for (int i = 0; i < concurrencyLevel; i++) {
            Object result = results.get(i);
            if (result instanceof Throwable) {
                throw (Throwable) result;
            }
            values.add((Value) result);
        }

        assertEquals(100, values.size());
    }

    @Test
    public void test_acquire_validator_valid()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, (v) -> true, 2);
        PooledObject<Value> obj1 = pool.acquire();
        assertTrue(!obj1.toString().contains("INTERMEDIATE"));
        PooledObject<Value> obj2 = pool.acquire();
        assertTrue(!obj2.toString().contains("INTERMEDIATE"));

        pool.release(obj1);

        PooledObject<Value> obj3 = pool.acquire();
        assertTrue(!obj3.toString().contains("INTERMEDIATE"));
        assertSame(obj1, obj3);
    }

    @Test
    public void test_acquire_validator_invalid()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, (v) -> false, 2);
        PooledObject<Value> obj1 = pool.acquire();
        assertTrue(!obj1.toString().contains("INTERMEDIATE"));
        PooledObject<Value> obj2 = pool.acquire();
        assertTrue(!obj2.toString().contains("INTERMEDIATE"));

        pool.release(obj1);

        PooledObject<Value> obj3 = pool.acquire();
        assertTrue(!obj3.toString().contains("INTERMEDIATE"));
        assertNotSame(obj1, obj3);
    }

    @Test
    public void test_concurrent_release()
            throws Throwable {

        int concurrencyLevel = 100;

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, concurrencyLevel);

        PooledObject<Value>[] results = new PooledObject[concurrencyLevel];
        AtomicReferenceArray<Throwable> exceptions = new AtomicReferenceArray<>(concurrencyLevel);

        Semaphore start = new Semaphore(concurrencyLevel);
        CountDownLatch end = new CountDownLatch(concurrencyLevel);

        start.acquire(concurrencyLevel);
        for (int i = 0; i < concurrencyLevel; i++) {
            int index = i;
            PooledObject<Value> valuePooledObject = pool.acquire();
            assertTrue(!valuePooledObject.toString().contains("INTERMEDIATE"));
            results[i] = valuePooledObject;

            new Thread(() -> {
                try {
                    start.acquire();
                    pool.release(valuePooledObject);
                } catch (Throwable e) {
                    exceptions.set(index, e);
                } finally {
                    end.countDown();
                }
            }).start();
        }

        start.release(concurrencyLevel);
        end.await(20, TimeUnit.SECONDS);

        for (int i = 0; i < concurrencyLevel; i++) {
            Throwable throwable = exceptions.get(i);
            if (throwable != null) {
                throw throwable;
            }

            assertTrue(results[i].toString().contains("FREE"));
        }
    }

    @Test
    public void test_acquire_but_full_intermediate_entry()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 2);
        PooledObject<Value> obj1 = pool.acquire();
        assertTrue(!obj1.toString().contains("INTERMEDIATE"));
        PooledObject<Value> obj2 = pool.acquire();
        assertTrue(!obj2.toString().contains("INTERMEDIATE"));

        PooledObject<Value> obj3 = pool.acquire();
        assertNotNull(obj3);
        assertTrue(obj3.toString().contains("INTERMEDIATE"));
    }

    @Test
    public void test_release_intermediate_entry()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 2);
        PooledObject<Value> obj1 = pool.acquire();
        assertTrue(!obj1.toString().contains("INTERMEDIATE"));
        PooledObject<Value> obj2 = pool.acquire();
        assertTrue(!obj2.toString().contains("INTERMEDIATE"));

        PooledObject<Value> obj3 = pool.acquire();
        assertNotNull(obj3);
        assertTrue(obj3.toString().contains("INTERMEDIATE"));
        pool.release(obj3);

        PooledObject<Value> obj4 = pool.acquire();
        assertNotNull(obj4);
        assertTrue(obj4.toString().contains("INTERMEDIATE"));
        assertNotSame(obj3, obj4);
    }

    @Test(expected = IllegalStateException.class)
    public void test_close()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 2);
        PooledObject<Value> obj = pool.acquire();
        assertNotNull(obj);
        pool.release(obj);

        pool.close();

        pool.acquire();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_release_null_object()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 2);
        pool.release(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_release_wrong_typed_object()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 2);
        pool.release(new WrongPooledObject<>());
    }

    @Test(expected = ConcurrentModificationException.class)
    public void test_release_already_free_object()
            throws Exception {

        ObjectPool<Value> pool = new NonBlockingObjectPool<>(Value::new, 2);
        PooledObject<Value> pooledObject = pool.acquire();

        Field field = NonBlockingObjectPool.Entry.class.getDeclaredField("state");
        field.setAccessible(true);
        field.set(pooledObject, 0);

        pool.release(pooledObject);
    }

    private static class Value {

        private String value;

    }

    private static class WrongPooledObject<T>
            implements PooledObject<T> {

        @Override
        public T getObject() {
            return null;
        }
    }

}