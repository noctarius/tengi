package com.github.tengi.buffer;

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

import com.github.tengi.UniqueId;
import com.github.tengi.utils.UnicodeUtil;
import com.github.tengi.utils.UnsafeUtil;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

@SuppressWarnings( "restriction" )
public abstract class AbstractMemoryBuffer
    implements MemoryBuffer
{

    private static final sun.misc.Unsafe unsafe = UnsafeUtil.getUnsafe();

    private static final long lockCounterOffset;

    static
    {
        try
        {

            Field lockCounterField = AbstractMemoryBuffer.class.getDeclaredField( "lockCounter" );
            lockCounterField.setAccessible( true );
            lockCounterOffset = unsafe.objectFieldOffset( lockCounterField );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Incompatible JVM - sun.misc.Unsafe support is missing" );
        }
    }

    private volatile int lockCounter = 0;

    protected int writerIndex = 0;

    protected int readerIndex = 0;

    @Override
    public void lock()
    {
        if ( lockCounter == -1 )
        {
            throw new IllegalStateException( "MemoryBuffer already released" );
        }

        while ( true )
        {
            int lockCount = lockCounter + 1;
            if ( unsafe.compareAndSwapInt( this, lockCounterOffset, lockCounter, lockCount ) )
            {
                return;
            }
        }
    }

    @Override
    public void release()
    {
        if ( lockCounter == -1 )
        {
            throw new IllegalStateException( "MemoryBuffer already released" );
        }

        while ( true )
        {
            int lockCount = lockCounter - 1;
            if ( unsafe.compareAndSwapInt( this, lockCounterOffset, lockCounter, lockCount ) )
            {
                if ( lockCount == -1 )
                {
                    free();
                }

                return;
            }
        }
    }

    @Override
    public boolean isReleased()
    {
        return lockCounter == -1;
    }

    @Override
    public boolean isReleasable()
    {
        return lockCounter == 0;
    }

    @Override
    public void clear()
    {
        writerIndex = 0;
        readerIndex = 0;
    }

    @Override
    public boolean readable()
    {
        return readerIndex() < writerIndex();
    }

    @Override
    public int readableBytes()
    {
        return writerIndex() - readerIndex();
    }

    @Override
    public boolean readBoolean()
    {
        return readByte() == 1 ? true : false;
    }

    @Override
    public byte readByte()
    {
        return readByte( readerIndex++ );
    }

    @Override
    public int readBytes( byte[] bytes )
    {
        return readBytes( bytes, 0, bytes.length );
    }

    @Override
    public int readBytes( byte[] bytes, int offset, int length )
    {
        for ( int pos = offset; pos < offset + length; pos++ )
        {
            bytes[pos] = readByte();
        }
        return length;
    }

    @Override
    public int readBuffer( ByteBuffer byteBuffer )
    {
        int remaining = Math.min( byteBuffer.remaining(), readableBytes() );
        return readBuffer( byteBuffer, byteBuffer.position(), remaining );
    }

    @Override
    public int readBuffer( ByteBuffer byteBuffer, int offset, int length )
    {
        if ( byteBuffer.hasArray() )
        {
            readBytes( byteBuffer.array(), offset, length );
        }
        else
        {
            for ( int pos = offset; pos < offset + length; pos++ )
            {
                byteBuffer.put( offset, readByte() );
            }
        }
        return length;
    }

    @Override
    public int readBuffer( WritableMemoryBuffer memoryBuffer )
    {
        int remaining = Math.min( memoryBuffer.writableBytes(), readableBytes() );
        return readBuffer( memoryBuffer, memoryBuffer.writerIndex(), remaining );
    }

    @Override
    public int readBuffer( WritableMemoryBuffer memoryBuffer, int offset, int length )
    {
        if ( memoryBuffer instanceof AbstractMemoryBuffer )
        {
            AbstractMemoryBuffer mb = (AbstractMemoryBuffer) memoryBuffer;
            for ( long pos = offset; pos < offset + length; pos++ )
            {
                mb.writeByte( offset, readByte() );
            }
        }
        else
        {
            int mark = memoryBuffer.writerIndex();
            memoryBuffer.writerIndex( offset );
            for ( int pos = offset; pos < offset + length; pos++ )
            {
                memoryBuffer.writeByte( readByte() );
            }
            memoryBuffer.writerIndex( Math.max( mark, memoryBuffer.writerIndex() ) );
        }
        return length;
    }

    @Override
    public short readUnsignedByte()
    {
        return (short) ( readByte() & 0xFF );
    }

    @Override
    public short readShort()
    {
        return ByteOrderUtils.getShort( this, true );
    }

    @Override
    public char readChar()
    {
        return (char) readShort();
    }

    @Override
    public int readInt()
    {
        return ByteOrderUtils.getInt( this, true );
    }

    @Override
    public int readCompressedInt()
    {
        return Int32Compressor.readInt32( this );
    }

    @Override
    public long readLong()
    {
        return ByteOrderUtils.getLong( this, true );
    }

    @Override
    public long readCompressedLong()
    {
        return Int64Compressor.readInt64( this );
    }

    @Override
    public float readFloat()
    {
        return Float.intBitsToFloat( readInt() );
    }

    @Override
    public double readDouble()
    {
        return Double.longBitsToDouble( readLong() );
    }

    @Override
    public String readString()
    {
        return UnicodeUtil.UTF8toUTF16( this );
    }

    @Override
    public UniqueId readUniqueId()
    {
        try
        {
            return UniqueId.readFromStream( this );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Error while deserializing UniqueId", e );
        }
    }

    @Override
    public int readerIndex()
    {
        return readerIndex;
    }

    @Override
    public void readerIndex( int readerIndex )
    {
        this.readerIndex = readerIndex;
    }

    @Override
    public boolean writable()
    {
        return growing() || writerIndex() < maxCapacity();
    }

    @Override
    public void writeBoolean( boolean value )
    {
        writeByte( (byte) ( value ? 1 : 0 ) );
    }

    @Override
    public void writeByte( int value )
    {
        writeByte( writerIndex++, (byte) value );
    }

    @Override
    public int writableBytes()
    {
        return maxCapacity() - writerIndex();
    }

    @Override
    public void writeBytes( byte[] bytes )
    {
        writeBytes( bytes, 0, bytes.length );
    }

    @Override
    public void writeBytes( byte[] bytes, int offset, int length )
    {
        for ( int pos = offset; pos < length; pos++ )
        {
            writeByte( bytes[pos] );
        }
    }

    @Override
    public void writeBuffer( ByteBuffer byteBuffer )
    {
        int remaining = Math.min( byteBuffer.remaining(), writableBytes() );
        writeBuffer( byteBuffer, byteBuffer.position(), remaining );
    }

    @Override
    public void writeBuffer( ByteBuffer byteBuffer, int offset, int length )
    {
        if ( byteBuffer.hasArray() )
        {
            writeBytes( byteBuffer.array(), offset, length );
        }
        else
        {
            for ( int pos = offset; pos < offset + length; pos++ )
            {
                writeByte( byteBuffer.get( offset ) );
            }
        }
    }

    @Override
    public void writeBuffer( ReadableMemoryBuffer memoryBuffer )
    {
        int remaining = Math.min( memoryBuffer.readableBytes(), writableBytes() );
        writeBuffer( memoryBuffer, memoryBuffer.readerIndex(), remaining );
    }

    @Override
    public void writeBuffer( ReadableMemoryBuffer memoryBuffer, int offset, int length )
    {
        if ( memoryBuffer instanceof AbstractMemoryBuffer )
        {
            AbstractMemoryBuffer mb = (AbstractMemoryBuffer) memoryBuffer;
            for ( long pos = offset; pos < offset + length; pos++ )
            {
                writeByte( mb.readByte( offset ) );
            }
        }
        else
        {
            int mark = memoryBuffer.readerIndex();
            memoryBuffer.readerIndex( offset );
            for ( int pos = offset; pos < offset + length; pos++ )
            {
                writeByte( memoryBuffer.readByte() );
            }
            memoryBuffer.readerIndex( Math.max( mark, memoryBuffer.readerIndex() ) );
        }
    }

    @Override
    public void writeUnsignedByte( short value )
    {
        writeByte( (byte) value );
    }

    @Override
    public void writeShort( short value )
    {
        ByteOrderUtils.putShort( value, this, true );
    }

    @Override
    public void writeChar( char value )
    {
        writeShort( (short) value );
    }

    @Override
    public void writeInt( int value )
    {
        ByteOrderUtils.putInt( value, this, true );
    }

    @Override
    public void writeCompressedInt( int value )
    {
        Int32Compressor.writeInt32( value, this );
    }

    @Override
    public void writeLong( long value )
    {
        ByteOrderUtils.putLong( value, this, true );
    }

    @Override
    public void writeCompressedLong( long value )
    {
        Int64Compressor.writeInt64( value, this );
    }

    @Override
    public void writeFloat( float value )
    {
        writeInt( Float.floatToIntBits( value ) );
    }

    @Override
    public void writeDouble( double value )
    {
        writeLong( Double.doubleToLongBits( value ) );
    }

    @Override
    public void writeString( String value )
    {
        UnicodeUtil.UTF16toUTF8( value, this );
    }

    @Override
    public void writeUniqueId( UniqueId uniqueId )
    {
        try
        {
            uniqueId.writeStream( this, null );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Error while serializing UniqueId", e );
        }
    }

    @Override
    public int writerIndex()
    {
        return writerIndex;
    }

    @Override
    public void writerIndex( int writerIndex )
    {
        this.writerIndex = writerIndex;
    }

    protected void rangeCheck( long offset )
    {
        if ( offset < 0 )
        {
            throw new IndexOutOfBoundsException( String.format( "Offset %s is below 0", offset ) );
        }
        if ( offset >= maxCapacity() )
        {
            throw new IndexOutOfBoundsException( String.format( "Offset %s is higher than maximum legal index ",
                                                                offset, ( maxCapacity() - 1 ) ) );
        }
    }

    protected abstract void writeByte( long offset, byte value );

    protected abstract byte readByte( long offset );

}
