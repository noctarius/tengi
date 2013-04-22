package com.github.tengi.buffer;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;

public class MemoryBufferPool
{

    private final ByteBufAllocator allocator = new PooledByteBufAllocator( true );

    public MemoryBuffer pop()
    {
        return new ByteBufMemoryBuffer( allocator.buffer() );
    }

    public void push( MemoryBuffer memoryBuffer )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ( (ByteBufMemoryBuffer) memoryBuffer ).buffer.release();
        }
    }

}
