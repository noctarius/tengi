package com.github.tengi.buffer;

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

    public MemoryBufferPool( int poolSize )
    {
        poolSettings.min( poolSize ).max( 0 );
        memoryBufferPool = poolSettings.pool();
    }

    public MemoryBuffer pop( ByteBuf byteBuffer )
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

    public void push( MemoryBuffer memoryBuffer )
    {
        if ( memoryBuffer instanceof MemoryBufferAdapter )
        {
            memoryBufferPool.returnObj( (MemoryBufferAdapter) memoryBuffer );
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
