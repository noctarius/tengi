package com.github.tengi.transport;
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

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.buffer.ReadableMemoryBuffer;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.WritableMemoryBuffer;

class ByteBufMemoryBuffer
    implements MemoryBuffer
{

    private ByteBuf byteBuffer;

    ByteBuf getByteBuffer()
    {
        return byteBuffer;
    }

    void setByteBuffer( ByteBuf byteBuffer )
    {
        this.byteBuffer = byteBuffer;
    }

    @Override
    public boolean readable()
    {
        return byteBuffer.readable();
    }

    @Override
    public long readableBytes()
    {
        return byteBuffer.readableBytes();
    }

    @Override
    public int readBytes( byte[] bytes )
    {
        byteBuffer.readBytes( bytes );
        return bytes.length;
    }

    @Override
    public int readBytes( byte[] bytes, int offset, int length )
    {
        byteBuffer.readBytes( bytes, offset, length );
        return length;
    }

    @Override
    public int readBuffer( ByteBuffer byteBuffer )
    {
        this.byteBuffer.readBytes( byteBuffer );
        return byteBuffer.position();
    }

    @Override
    public int readBuffer( ByteBuffer byteBuffer, int offset, int length )
    {
        if ( byteBuffer.hasArray() )
        {
            this.byteBuffer.readBytes( byteBuffer.array(), offset, length );
            return length;
        }

        byte[] data = new byte[length];
        this.byteBuffer.readBytes( data );
        byteBuffer.put( data, offset, length );
        return length;
    }

    @Override
    public long readBuffer( WritableMemoryBuffer memoryBuffer )
    {
        if ( memoryBuffer instanceof ByteBufMemoryBuffer )
        {
            ByteBuf buffer = ( (ByteBufMemoryBuffer) memoryBuffer ).byteBuffer;
            int pos = buffer.readerIndex();
            byteBuffer.readBytes( buffer );
            return buffer.readerIndex() - pos;
        }

        long writableBytes = memoryBuffer.writableBytes();
        byte[] data = new byte[(int) writableBytes];
        byteBuffer.readBytes( data );
        memoryBuffer.writeBytes( data );
        return writableBytes;
    }

    @Override
    public long readBuffer( WritableMemoryBuffer memoryBuffer, long offset, long length )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean readBoolean()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public byte readByte()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public short readUnsignedByte()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public short readShort()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public char readChar()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readInt()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int readCompressedInt()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long readLong()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long readCompressedLong()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float readFloat()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double readDouble()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String readString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UniqueId readUniqueId()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long readerIndex()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void readerIndex( long readerIndex )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean writable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public long writableBytes()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeBytes( byte[] bytes )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBytes( byte[] bytes, int offset, int length )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBuffer( ByteBuffer byteBuffer )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBuffer( ByteBuffer byteBuffer, int offset, int length )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBuffer( ReadableMemoryBuffer memoryBuffer )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBuffer( ReadableMemoryBuffer memoryBuffer, long offset, long length )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeBoolean( boolean value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeByte( byte value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeUnsignedByte( short value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeShort( short value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeChar( char value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeInt( int value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeCompressedInt( int value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeLong( long value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeCompressedLong( long value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeFloat( float value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeDouble( double value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeString( String value )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void writeUniqueId( UniqueId uniqueId )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public long writerIndex()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writerIndex( long writerIndex )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public long capacity()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long maxCapacity()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean growing()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ByteOrder byteOrder()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void byteOrder( ByteOrder byteOrder )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void free()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void clear()
    {
        // TODO Auto-generated method stub

    }

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        // TODO Auto-generated method stub

    }

}
