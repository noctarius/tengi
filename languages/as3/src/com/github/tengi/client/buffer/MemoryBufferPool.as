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
    import flash.utils.ByteArray;

    public class MemoryBufferPool
    {

        private var memoryBufferPool:Vector.<MemoryBuffer> = new Vector.<MemoryBuffer>();

        public function MemoryBufferPool( poolSize:int )
        {
            for ( var i:int = 0; i < poolSize; i++ )
            {
                memoryBufferPool.push( new MemoryBufferAdapter() );
            }
        }

        public function pop( byteArray:ByteArray ):MemoryBuffer
        {
            if ( memoryBufferPool.length > 0 )
            {
                var memoryBuffer:MemoryBuffer = memoryBufferPool.pop();
                memoryBuffer.clear();
                memoryBuffer.byteArray = byteArray;
                return memoryBuffer;
            }
            return new MemoryBuffer( byteArray );
        }

        public function push( memoryBuffer:MemoryBuffer ):void
        {
            if ( memoryBuffer is MemoryBufferAdapter )
            {
                memoryBuffer.byteArray = null;
                memoryBuffer.clear();
                memoryBufferPool.push( memoryBuffer );
            }
        }

    }
}

import com.github.tengi.client.buffer.MemoryBuffer;

internal class MemoryBufferAdapter extends MemoryBuffer
{
}