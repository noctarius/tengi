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
package com.github.tengi.client.transport.polling
{
    import com.github.tengi.client.ClientConnection;
    import com.github.tengi.client.Message;
    import com.github.tengi.client.Protocol;
    import com.github.tengi.client.UniqueId;
    import com.github.tengi.client.buffer.MemoryBuffer;

    public class PollingMessage extends Message
    {

        private var _lastUpdateId:int;

        public function PollingMessage( connection:ClientConnection, messageId:UniqueId = null, lastUpdateId:int = -1 )
        {
            super( connection, null, messageId, Message.MESSAGE_TYPE_LONG_POLLING );
            this._lastUpdateId = lastUpdateId;
        }

        override public function readStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
        {
            super.readStream( memoryBuffer, protocol );
            _lastUpdateId = memoryBuffer.readInt();
        }

        override public function writeStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
        {
            super.writeStream( memoryBuffer, protocol );
            memoryBuffer.writeInt( _lastUpdateId );
        }

        public function get lastUpdateId():int
        {
            return _lastUpdateId;
        }

    }
}
