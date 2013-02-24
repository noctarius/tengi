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

    import flash.utils.ByteArray;

    public interface MemoryBuffer
    {

        function get capacity() : int;

        function get byteOrder() : ByteOrder;

        function set byteOrder( byteOrder : ByteOrder );

        function free() : Boolean;

        function clear() : void;

        function get readable() : Boolean;

        function get readableByte() : int;

        function readBytes( buffer : *, offset : int = -1, length : int = -1 ) : int;

        function readBoolean() : Boolean;

        function readByte() : int;

        function readUnsignedByte() : int;

        function readShort() : int;

        function readChar() : int;

        function readInt() : int;

        function readCompressedInt() : int;

        function readLong() : Number;

        function readCompressedLong() : Number;

        function readFloat() : Number;

        function readDouble() : Number;

        function readString() : String;

        function readUniqueId() : UniqueId;

        function get readerIndex() : int;

        function set readerIndex( readerIndex : int ) : void;

        function get writable() : Boolean;

        function get writableBytes() : int;

        function writeBytes( buffer : *, offset : int = -1, length : int = -1 ) : int;

        function writeBoolean( value : Boolean ) : void;

        function writeByte( value : int ) : void;

        function writeUnsignedByte( value : int ) : void;

        function writeShort( value : int ) : void;

        function writeChar( value : int ) : void;

        function writeCompressedInt( value : int ) : void;

        function writeLong( value : Number ) : void;

        function writeCompressedLong( value : Number ) : void;

        function writeFloat( value : Number ) : void;

        function writeDouble( value : Number ) : void;

        function writeString( value : String ) : void;

        function writeUniqueId( value : UniqueId ) : void;

        function get writerIndex() : int;

        function set writerIndex( writerIndex : int ) : void;

    }
}
