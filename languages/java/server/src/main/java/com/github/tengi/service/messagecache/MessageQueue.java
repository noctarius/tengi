package com.github.tengi.service.messagecache;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.github.tengi.Connection;
import com.github.tengi.Message;
import com.github.tengi.SerializationFactory;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.buffer.MemoryBufferPool;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageQueue
{

    private final Deque<CachedMessage> cache = new LinkedBlockingDeque<>();

    private final SerializationFactory serializationFactory;

    private final MemoryBufferPool memoryBufferPool;

    private final AtomicInteger updateId = new AtomicInteger( 0 );

    private final Connection connection;

    public MessageQueue( Connection connection, SerializationFactory serializationFactory,
                         MemoryBufferPool memoryBufferPool )
    {
        this.connection = connection;
        this.serializationFactory = serializationFactory;
        this.memoryBufferPool = memoryBufferPool;
    }

    public int size()
    {
        return cache.size();
    }

    public void clear()
    {
        cache.clear();
        updateId.set( 0 );
    }

    public void push( MemoryBuffer memoryBuffer )
    {
        int updateId = this.updateId.incrementAndGet();
        cache.push( new CachedMessage( updateId, memoryBuffer ) );
    }

    public Message snapshot( int lastUpdateId )
    {
        if ( cache.size() == 0 )
        {
            return null;
        }

        List<MemoryBuffer> cachedMessages = new LinkedList<>();

        Iterator<CachedMessage> iterator = cache.descendingIterator();
        while ( iterator.hasNext() )
        {
            CachedMessage cachedMessage = iterator.next();
            if ( cachedMessage.updateId > lastUpdateId )
            {
                MemoryBuffer memoryBuffer = cachedMessage.memoryBuffer;

                memoryBuffer.lock();
                cachedMessages.add( memoryBuffer );
            }
            else if ( cachedMessage.updateId < lastUpdateId - 50 && cachedMessage.memoryBuffer.isReleasable() )
            {
                iterator.remove();
                cachedMessage.memoryBuffer.release();
                memoryBufferPool.push( cachedMessage.memoryBuffer );
            }
        }

        if ( cachedMessages.size() > 0 )
        {
            return new PreserializedCompositeMessage( serializationFactory, connection, cachedMessages,
                                                      UniqueId.randomUniqueId() );
        }
        return null;
    }

    private class CachedMessage
    {

        private final int updateId;

        private final MemoryBuffer memoryBuffer;

        public CachedMessage( int updateId, MemoryBuffer memoryBuffer )
        {
            this.updateId = updateId;
            this.memoryBuffer = memoryBuffer;
        }
    }

}
