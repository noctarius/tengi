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

package com.github.tengi.client
{
    import com.github.tengi.client.buffer.MemoryBuffer;

    import flash.system.System;
    import flash.utils.ByteArray;
    import flash.utils.getTimer;

    public class UniqueId implements Streamable
    {

        private static const CHARS:Array = [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70];

        private const uniqueIdData:ByteArray = new ByteArray();

        function UniqueId( uniqueIdData:ByteArray = null ):void
        {
            if ( uniqueIdData != null )
            {
                this.uniqueIdData.clear();
                this.uniqueIdData.writeBytes( uniqueIdData, 0, 16 );
            }
        }

        public function equals( obj:UniqueId ):Boolean
        {
            var temp:ByteArray = new ByteArray();
            temp.writeBytes( obj.uniqueIdData, 0, 16 );

            var index:int = 0;
            for each ( var data:int in uniqueIdData )
            {
                temp.position = index;
                if ( data != obj.uniqueIdData.readByte() )
                {
                    return false;
                }
                index++;
            }
            return true;
        }

        public function writeStream( memoryBuffer:MemoryBuffer ):void
        {
            uniqueIdData.position = 0;
            memoryBuffer.writeBytes( uniqueIdData, 0, 16 );
        }

        public function readStream( memoryBuffer:MemoryBuffer ):void
        {
            uniqueIdData.clear();
            memoryBuffer.readBytes( uniqueIdData, 0, 16 );
        }

        public function toString():String
        {
            uniqueIdData.position = 0;
            var chars:Array = new Array( 36 );
            var index:uint = 0;
            for ( var i:uint = 0; i < 16; i++ )
            {
                if ( i == 4 || i == 6 || i == 8 || i == 10 )
                {
                    chars[index++] = 45; // Hyphen char code
                }
                var b:int = uniqueIdData.readByte();
                chars[index++] = CHARS[(b & 0xF0) >>> 4];
                chars[index++] = CHARS[(b & 0x0F)];
            }
            return String.fromCharCode.apply( null, chars );
        }

        public static function readFromStream( memoryBuffer:MemoryBuffer ):UniqueId
        {
            var uniqueId:UniqueId = new UniqueId();
            uniqueId.readStream( memoryBuffer );
            return uniqueId;
        }

        public static function randomUniqueId():UniqueId
        {
            var data:ByteArray = new ByteArray();
            var r:uint = uint( new Date().time );
            data.writeUnsignedInt( System.totalMemory ^ r );
            data.writeInt( getTimer() ^ r );
            data.writeDouble( Math.random() * r );
            return new UniqueId( data );
        }

    }
}
