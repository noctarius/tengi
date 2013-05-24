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

    public class Entity implements Streamable
    {
        private var entityId:int = -1;
        private var parentEntityId:int = -1;

        private var entityType:int;

        private var version:int;

        public function toString():String
        {
            return "[entityId=" + entityId + ", " + "parentEntityId=" + parentEntityId + "]";
        }

        public function writeStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
        {
            memoryBuffer.writeInt( entityId );
            memoryBuffer.writeInt( parentEntityId );
            memoryBuffer.writeInt( entityType );
            memoryBuffer.writeInt( version );
        }

        public function readStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
        {
            this.entityId = memoryBuffer.readInt();
            this.parentEntityId = memoryBuffer.readInt();
            this.entityType = memoryBuffer.readInt();
            this.version = memoryBuffer.readInt();
        }

        public function writeEntityHeader( memoryBuffer:MemoryBuffer ):void
        {
            memoryBuffer.writeByte( 1 );
        }

    }
}
