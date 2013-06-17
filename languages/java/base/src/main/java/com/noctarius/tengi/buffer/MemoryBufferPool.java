package com.noctarius.tengi.buffer;

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

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.ByteBuf;
import nf.fr.eraasoft.pool.ObjectPool;
import nf.fr.eraasoft.pool.PoolException;
import nf.fr.eraasoft.pool.PoolSettings;
import nf.fr.eraasoft.pool.PoolableObjectBase;

public class MemoryBufferPool
{

    private final ObjectPool<MemoryBufferAdapter> memoryBufferPool;

    private final PoolSettings<MemoryBufferAdapter> poolSettings =
        new PoolSettings<>( new PoolableMemoryBufferObject() );

    private final ByteBufAllocator allocator = new PooledByteBufAllocator( true );

    public MemoryBufferPool( int poolSize )
    {
        poolSettings.min( poolSize ).max( 0 );
        memoryBufferPool = poolSettings.pool();
    }

    public MemoryBuffer pop( int initialSize )
    {
        return wrap( allocator.ioBuffer( initialSize ) );
    }

    public MemoryBuffer pop()
    {
        return wrap( allocator.ioBuffer() );
    }

    public void push( MemoryBuffer memoryBuffer )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ( (ByteBufMemoryBuffer) memoryBuffer ).getByteBuffer().release();
        }
        if ( memoryBuffer instanceof MemoryBufferAdapter )
        {
            memoryBufferPool.returnObj( (MemoryBufferAdapter) memoryBuffer );
        }
    }

    public MemoryBuffer wrap( ByteBuf byteBuffer )
    {
        try
        {
            ByteBufMemoryBuffer memoryBuffer = memoryBufferPool.getObj();
            memoryBuffer.setByteBuffer( byteBuffer );
            return memoryBuffer;
        }
        catch ( PoolException e )
        {
            return new ByteBufMemoryBuffer().setByteBuffer( byteBuffer );
        }
    }

    private class PoolableMemoryBufferObject
        extends PoolableObjectBase<MemoryBufferAdapter>
    {

        @Override
        public MemoryBufferAdapter make()
            throws PoolException
        {
            return new MemoryBufferAdapter();
        }

        @Override
        public void activate( MemoryBufferAdapter memoryBuffer )
            throws PoolException
        {
            memoryBuffer.clear();
            memoryBuffer.setByteBuffer( null );
        }

        @Override
        public void passivate( MemoryBufferAdapter memoryBuffer )
        {
            memoryBuffer.clear();
            memoryBuffer.setByteBuffer( null );
        }
    }

    private class MemoryBufferAdapter
        extends ByteBufMemoryBuffer
    {
    }

}
