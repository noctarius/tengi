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

    public class MemoryBuffer
    {

        var byteArray:ByteArray;

        private var _readerIndex:uint = 0;
        private var _writerIndex:uint;

        public function MemoryBuffer( byteArray:ByteArray = null )
        {
            this.byteArray = byteArray;
            this._writerIndex = byteArray.length;

            this.byteArray.endian = "BIG_ENDIAN";
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
            if ( byteArray != null )
            {
                byteArray.clear();
            }
            _writerIndex = 0;
            _readerIndex = 0;
        }

        public function get readable():Boolean
        {
            return byteArray.length > 0 && _readerIndex < byteArray.length;
        }

        public function get readableByte():int
        {
            return byteArray.length - _readerIndex;
        }

        public function readBytes( buffer:*, offset:int = -1, length:int = -1 ):int
        {
            byteArray.position = _readerIndex;
            var target:ByteArray;
            if ( buffer is ByteArray )
            {
                target = buffer as ByteArray;
            }
            else if ( buffer is MemoryBuffer )
            {
                target = (buffer as MemoryBuffer).byteArray;
            }
            else
            {
                throw new IllegalArgumentError( "Given buffer type is unsupported (legal types ByteArray, MemoryBuffer)" );
            }

            var writeableBytes:int = length != -1 ? length : target.length - target.position;
            var writeOffset:int = offset != -1 ? offset : target.position;
            byteArray.readBytes( target, writeOffset, writeableBytes );
            _readerIndex += writeableBytes;
            return writeableBytes;
        }

        public function readBoolean():Boolean
        {
            byteArray.position = _readerIndex;
            var value:Boolean = byteArray.readBoolean();
            _readerIndex += 1;
            return value;
        }

        public function readByte():int
        {
            byteArray.position = _readerIndex;
            var value:int = byteArray.readByte();
            _readerIndex += 1;
            return value;
        }

        public function readUnsignedByte():int
        {
            return readByte() & 0xFF;
        }

        public function readShort():int
        {
            byteArray.position = _readerIndex;
            var value:int = byteArray.readShort();
            _readerIndex += 2;
            return value;
        }

        public function readChar():int
        {
            return readInt();
        }

        public function readInt():int
        {
            byteArray.position = _readerIndex;
            var value:int = byteArray.readInt();
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
            byteArray.position = _readerIndex;
            var u1:uint = byteArray.readUnsignedInt();
            var u0:uint = byteArray.readUnsignedInt();
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
            byteArray.position = _readerIndex;
            var value:Number = byteArray.readFloat();
            _readerIndex += 4;
            return value;
        }

        public function readDouble():Number
        {
            byteArray.position = _readerIndex;
            var value:Number = byteArray.readDouble();
            _readerIndex += 8;
            return value;
        }

        public function readString():String
        {
            byteArray.position = _readerIndex;
            var value:String = byteArray.readUTF();
            _readerIndex += (byteArray.position - _readerIndex);
            return value;
        }

        public function readUniqueId():UniqueId
        {
            byteArray.position = _readerIndex;
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
            byteArray.position = _writerIndex;
            var source:ByteArray;
            if ( buffer is ByteArray )
            {
                source = buffer as ByteArray;
            }
            else if ( buffer is MemoryBuffer )
            {
                source = (buffer as MemoryBuffer).byteArray;
            }
            else
            {
                throw new IllegalArgumentError( "Given buffer type is unsupported (legal types ByteArray, MemoryBuffer)" );
            }

            var readableBytes:int = length != -1 ? length : source.length - source.position;
            var readOffset:int = offset != -1 ? offset : source.position;
            source.readBytes( byteArray, readOffset, readableBytes );
            _writerIndex += readableBytes;
            return readableBytes;

        }

        public function writeBoolean( value:Boolean ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeBoolean( value );
            _writerIndex += 1;
        }

        public function writeByte( value:int ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeByte( value );
            _writerIndex += 1;
        }

        public function writeUnsignedByte( value:int ):void
        {
            writeByte( value );
        }

        public function writeShort( value:int ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeShort( value );
            _writerIndex += 2;
        }

        public function writeChar( value:int ):void
        {
            writeInt( value );
        }

        public function writeInt( value:int ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeInt( value );
            _writerIndex += 4;
        }

        public function writeCompressedInt( value:int ):void
        {
            // TODO Missing CompressionUtils
            writeInt( value );
        }

        public function writeLong( value:Long ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeUnsignedInt( value.composite0 );
            byteArray.writeUnsignedInt( value.composite1 );
            _writerIndex += 8;
        }

        public function writeCompressedLong( value:Long ):void
        {
            // TODO Missing CompressionUtils
            writeLong( value );
        }

        public function writeFloat( value:Number ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeFloat( value );
            _writerIndex += 4;
        }

        public function writeDouble( value:Number ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeDouble( value );
            _writerIndex += 8;
        }

        public function writeString( value:String ):void
        {
            byteArray.position = _writerIndex;
            byteArray.writeUTF( value );
            _writerIndex += (byteArray.position - _writerIndex);
        }

        public function writeUniqueId( value:UniqueId ):void
        {
            byteArray.position = _writerIndex;
            value.writeStream( this );
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

    }
}
