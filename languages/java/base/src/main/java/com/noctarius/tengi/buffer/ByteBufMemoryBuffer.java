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

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class ByteBufMemoryBuffer
    extends AbstractMemoryBuffer
{

    private ByteBuf byteBuffer;

    protected ByteBufMemoryBuffer()
    {
    }

    public ByteBuf getByteBuffer()
    {
        return byteBuffer;
    }

    protected ByteBufMemoryBuffer setByteBuffer( ByteBuf byteBuffer )
    {
        this.byteBuffer = byteBuffer;
        return this;
    }

    @Override
    public boolean readable()
    {
        return byteBuffer.isReadable();
    }

    @Override
    public int readableBytes()
    {
        return byteBuffer.readableBytes();
    }

    @Override
    public int readBytes( byte[] bytes )
    {
        byteBuffer.readBytes( bytes );
        readerIndex += bytes.length;
        return bytes.length;
    }

    @Override
    public int readBytes( byte[] bytes, int offset, int length )
    {
        byteBuffer.readBytes( bytes, offset, length );
        readerIndex += length;
        return length;
    }

    @Override
    public int readBuffer( ByteBuffer byteBuffer )
    {
        int pos = byteBuffer.position();
        this.byteBuffer.readBytes( byteBuffer );
        readerIndex += ( byteBuffer.position() - pos );
        return byteBuffer.position() - pos;
    }

    @Override
    public int readBuffer( ByteBuffer byteBuffer, int offset, int length )
    {
        if ( byteBuffer.hasArray() )
        {
            this.byteBuffer.readBytes( byteBuffer.array(), offset, length );
            readerIndex += length;
            return length;
        }

        byte[] data = new byte[length];
        this.byteBuffer.readBytes( data );
        byteBuffer.put( data, offset, length );
        readerIndex += length;
        return length;
    }

    @Override
    public int readBuffer( WritableMemoryBuffer memoryBuffer )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ByteBuf buffer = ( (ByteBufMemoryBuffer) memoryBuffer ).byteBuffer;
            int pos = buffer.readerIndex();
            byteBuffer.readBytes( buffer );
            readerIndex += ( buffer.readerIndex() - pos );
            return buffer.readerIndex() - pos;
        }

        int writableBytes = memoryBuffer.writableBytes();
        byte[] data = new byte[writableBytes];
        byteBuffer.readBytes( data );
        memoryBuffer.writeBytes( data );
        readerIndex += writableBytes;
        return writableBytes;
    }

    @Override
    public int readBuffer( WritableMemoryBuffer memoryBuffer, int offset, int length )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ByteBuf buffer = ( (ByteBufMemoryBuffer) memoryBuffer ).byteBuffer;
            int pos = buffer.readerIndex();
            byteBuffer.readBytes( buffer );
            readerIndex += ( buffer.readerIndex() - pos );
            return buffer.readerIndex() - pos;
        }

        int writableBytes = memoryBuffer.writableBytes();
        byte[] data = new byte[writableBytes];
        byteBuffer.readBytes( data );
        memoryBuffer.writeBytes( data );
        readerIndex += writableBytes;
        return writableBytes;
    }

    @Override
    public void writeBytes( byte[] bytes )
    {
        byteBuffer.writeBytes( bytes );
        writerIndex += bytes.length;
    }

    @Override
    public void writeBytes( byte[] bytes, int offset, int length )
    {
        byteBuffer.writeBytes( bytes, offset, length );
        writerIndex += length;
    }

    @Override
    public void writeBuffer( ByteBuffer byteBuffer )
    {
        int length = byteBuffer.remaining();
        this.byteBuffer.writeBytes( byteBuffer );
        writerIndex += length;
    }

    @Override
    public void writeBuffer( ByteBuffer byteBuffer, int offset, int length )
    {
        if ( byteBuffer.hasArray() )
        {
            this.byteBuffer.writeBytes( byteBuffer.array(), offset, length );
            writerIndex += length;
        }
        else
        {
            byte[] data = new byte[length];
            byteBuffer.get( data, offset, length );
            this.byteBuffer.writeBytes( data );
            writerIndex += length;
        }
    }

    @Override
    public void writeBuffer( ReadableMemoryBuffer memoryBuffer )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ByteBufMemoryBuffer buffer = (ByteBufMemoryBuffer) memoryBuffer;
            long readableBytes = buffer.byteBuffer.readableBytes();
            this.byteBuffer.readBytes( buffer.byteBuffer );
            writerIndex += readableBytes;
        }
        else
        {
            super.writeBuffer( memoryBuffer );
        }
    }

    @Override
    public void writeBuffer( ReadableMemoryBuffer memoryBuffer, int offset, int length )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ByteBufMemoryBuffer buffer = (ByteBufMemoryBuffer) memoryBuffer;
            int pos = buffer.byteBuffer.readerIndex();
            buffer.byteBuffer.readerIndex( offset );
            this.byteBuffer.readBytes( buffer.byteBuffer, length );
            buffer.byteBuffer.readerIndex( pos );
            writerIndex += length;
        }
        else
        {
            super.writeBuffer( memoryBuffer, offset, length );
        }
    }

    @Override
    public int capacity()
    {
        return byteBuffer.capacity();
    }

    @Override
    public int maxCapacity()
    {
        return byteBuffer.maxCapacity();
    }

    @Override
    public boolean growing()
    {
        return true;
    }

    @Override
    public void free()
    {
        if ( byteBuffer instanceof AbstractReferenceCountedByteBuf )
        {
            // Force a releasing of the underlying ByteBuf
            AbstractReferenceCountedByteBuf byteBuf = (AbstractReferenceCountedByteBuf) byteBuffer;
            byteBuf.release( byteBuf.refCnt() );
        }
    }

    @Override
    public MemoryBuffer duplicate()
    {
        return new ByteBufMemoryBuffer().setByteBuffer( byteBuffer.duplicate() );
    }

    @Override
    protected void writeByte( long offset, byte value )
    {
        int pos = byteBuffer.writerIndex();
        byteBuffer.writerIndex( (int) offset );
        byteBuffer.writeByte( value );
        byteBuffer.writerIndex( pos );
    }

    @Override
    protected byte readByte( long offset )
    {
        int pos = byteBuffer.readerIndex();
        byteBuffer.readerIndex( (int) offset );
        byte value = byteBuffer.readByte();
        byteBuffer.readerIndex( pos );
        return value;
    }

}
