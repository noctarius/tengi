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
package com.github.tengi.client.buffer
{

    import com.github.tengi.client.UniqueId;
    import com.github.tengi.client.lang.IllegalArgumentError;
    import com.github.tengi.client.lang.math.Long;

    import flash.utils.ByteArray;
    import flash.utils.Endian;

    public class MemoryBuffer
    {

        var _byteArray:ByteArray;

        private var _readerIndex:uint = 0;
        private var _writerIndex:uint = 0;

        public function MemoryBuffer( byteArray:ByteArray = null )
        {
            this._byteArray = byteArray;

            if ( byteArray != null )
            {
                this._writerIndex = byteArray.length;
                this._byteArray.endian = "BIG_ENDIAN";
            }
        }

        public function get capacity():int
        {
            return int.MAX_VALUE;
        }

        public function free():Boolean
        {
            clear();
            return true;
        }

        public function clear():void
        {
            if ( _byteArray != null )
            {
                _byteArray.clear();
            }
            _writerIndex = 0;
            _readerIndex = 0;
        }

        public function get readable():Boolean
        {
            return _byteArray.length > 0 && _readerIndex < _byteArray.length;
        }

        public function get readableByte():int
        {
            return _byteArray.length - _readerIndex;
        }

        public function readBytes( buffer:*, offset:int = -1, length:int = -1 ):int
        {
            _byteArray.position = _readerIndex;
            var target:ByteArray;
            if ( buffer is ByteArray )
            {
                target = buffer as ByteArray;
            }
            else if ( buffer is MemoryBuffer )
            {
                target = (buffer as MemoryBuffer)._byteArray;
            }
            else
            {
                throw new IllegalArgumentError( "Given buffer type is unsupported (legal types ByteArray, MemoryBuffer)" );
            }

            var writeableBytes:int = length != -1 ? length : target.length - target.position;
            var writeOffset:int = offset != -1 ? offset : target.position;
            _byteArray.readBytes( target, writeOffset, writeableBytes );
            _readerIndex += writeableBytes;
            return writeableBytes;
        }

        public function readBoolean():Boolean
        {
            _byteArray.position = _readerIndex;
            var value:Boolean = _byteArray.readBoolean();
            _readerIndex += 1;
            return value;
        }

        public function readByte():int
        {
            _byteArray.position = _readerIndex;
            var value:int = _byteArray.readByte();
            _readerIndex += 1;
            return value;
        }

        public function readUnsignedByte():int
        {
            return readByte() & 0xFF;
        }

        public function readShort():int
        {
            _byteArray.position = _readerIndex;
            var value:int = _byteArray.readShort();
            _readerIndex += 2;
            return value;
        }

        public function readChar():int
        {
            return readInt();
        }

        public function readInt():int
        {
            _byteArray.position = _readerIndex;
            var value:int = _byteArray.readInt();
            _readerIndex += 4;
            return value;
        }

        public function readCompressedInt():int
        {
            // Missing CompressionUtils
            return readInt();
        }

        public function readLong():Long
        {
            _byteArray.position = _readerIndex;
            var u1:uint = _byteArray.readUnsignedInt();
            var u0:uint = _byteArray.readUnsignedInt();
            var value:Long = Long.newLong( u1, u0 );
            _readerIndex += 8;
            return value;
        }

        public function readCompressedLong():Long
        {
            // Missing CompressionUtils
            return readLong();
        }

        public function readFloat():Number
        {
            _byteArray.position = _readerIndex;
            var value:Number = _byteArray.readFloat();
            _readerIndex += 4;
            return value;
        }

        public function readDouble():Number
        {
            _byteArray.position = _readerIndex;
            var value:Number = _byteArray.readDouble();
            _readerIndex += 8;
            return value;
        }

        public function readString():String
        {
            _byteArray.position = _readerIndex;
            var value:String = _byteArray.readUTF();
            _readerIndex += (_byteArray.position - _readerIndex);
            return value;
        }

        public function readUniqueId():UniqueId
        {
            _byteArray.position = _readerIndex;
            var value:UniqueId = UniqueId.readFromStream( this );
            _readerIndex += 16;
            return value;
        }

        public function get readerIndex():int
        {
            return _readerIndex;
        }

        public function set readerIndex( readerIndex:int ):void
        {
            _readerIndex = readerIndex;
        }

        public function get writable():Boolean
        {
            return true;
        }

        public function get writableBytes():int
        {
            return int.MAX_VALUE;
        }

        public function writeBytes( buffer:*, offset:int = -1, length:int = -1 ):int
        {
            _byteArray.position = _writerIndex;
            var source:ByteArray;
            if ( buffer is ByteArray )
            {
                source = buffer as ByteArray;
            }
            else if ( buffer is MemoryBuffer )
            {
                source = (buffer as MemoryBuffer)._byteArray;
            }
            else
            {
                throw new IllegalArgumentError( "Given buffer type is unsupported (legal types ByteArray, MemoryBuffer)" );
            }

            var readableBytes:int = length != -1 ? length : source.length - source.position;
            var readOffset:int = offset != -1 ? offset : source.position;
            source.readBytes( _byteArray, readOffset, readableBytes );
            _writerIndex += readableBytes;
            return readableBytes;

        }

        public function writeBoolean( value:Boolean ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeBoolean( value );
            _writerIndex += 1;
        }

        public function writeByte( value:int ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeByte( value );
            _writerIndex += 1;
        }

        public function writeUnsignedByte( value:int ):void
        {
            writeByte( value );
        }

        public function writeShort( value:int ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeShort( value );
            _writerIndex += 2;
        }

        public function writeChar( value:int ):void
        {
            writeInt( value );
        }

        public function writeInt( value:int ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeInt( value );
            _writerIndex += 4;
        }

        public function writeCompressedInt( value:int ):void
        {
            // TODO Missing CompressionUtils
            writeInt( value );
        }

        public function writeLong( value:Long ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeUnsignedInt( value.composite0 );
            _byteArray.writeUnsignedInt( value.composite1 );
            _writerIndex += 8;
        }

        public function writeCompressedLong( value:Long ):void
        {
            // TODO Missing CompressionUtils
            writeLong( value );
        }

        public function writeFloat( value:Number ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeFloat( value );
            _writerIndex += 4;
        }

        public function writeDouble( value:Number ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeDouble( value );
            _writerIndex += 8;
        }

        public function writeString( value:String ):void
        {
            _byteArray.position = _writerIndex;
            _byteArray.writeUTF( value );
            _writerIndex += (_byteArray.position - _writerIndex);
        }

        public function writeUniqueId( value:UniqueId ):void
        {
            _byteArray.position = _writerIndex;
            value.writeStream( this, null );
            _writerIndex += 16;
        }

        public function get writerIndex():int
        {
            return _writerIndex;
        }

        public function set writerIndex( writerIndex:int ):void
        {
            _writerIndex = writerIndex;
        }

        function get byteArray():ByteArray
        {
            return _byteArray;
        }

        function set byteArray( byteArray:ByteArray ):void
        {
            this._byteArray = byteArray;

            if ( this._byteArray != null )
            {
                this._writerIndex = byteArray.length;
                this._byteArray.endian = Endian.BIG_ENDIAN;
            }
        }

    }
}
