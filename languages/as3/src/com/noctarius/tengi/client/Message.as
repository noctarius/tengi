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

package com.noctarius.tengi.client
{

    import com.noctarius.tengi.client.buffer.MemoryBuffer;

    public class Message implements Streamable
    {

        public static const MESSAGE_TYPE_DEFAULT:int = 0;

        public static const MESSAGE_TYPE_LONG_POLLING:int = 1;

        public static const MESSAGE_TYPE_COMPOSITE:int = 2;

        protected var _connection:ClientConnection;

        private var _body:Streamable;

        private var _messageId:UniqueId;

        private var _type:int = MESSAGE_TYPE_DEFAULT;

        public function Message( connection:ClientConnection, body:Streamable = null, messageId:UniqueId = null,
                                 type:int = 0 )
        {
            this._connection = connection;
            this._messageId = messageId != null ? messageId : UniqueId.randomUniqueId();
            this._body = body;
            this._type = type;
        }

        public function toString():String
        {
            return "Message [messageId=" + _messageId + ", body=" + (_body != null ? _body.toString() : "null") + "]";
        }

        public function readStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
        {
            this._messageId = new UniqueId();
            this._messageId.readStream( memoryBuffer, protocol );
            this._type = memoryBuffer.readByte();
            if ( memoryBuffer.readByte() == 1 )
            {
                var classId:int = memoryBuffer.readShort();
                _body = protocol.instantiate( classId );
                _body.readStream( memoryBuffer, protocol );
            }
        }

        public function writeStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
        {
            _messageId.writeStream( memoryBuffer, protocol )
            memoryBuffer.writeByte( _type );
            if ( body == null )
            {
                memoryBuffer.writeByte( 0 );
            }
            else
            {
                memoryBuffer.writeByte( 1 );
                var classId:int = protocol.getClassIdentifier( _body );
                memoryBuffer.writeShort( classId );
                _body.writeStream( memoryBuffer, protocol );
            }
        }

        public function get connection():ClientConnection
        {
            return _connection;
        }

        public function get body():Streamable
        {
            return _body;
        }

        public function get messageId():UniqueId
        {
            return _messageId;
        }

        public function get type():int
        {
            return _type;
        }

        public static function read( memoryBuffer:MemoryBuffer, protocol:Protocol, connection:ClientConnection ):Message
        {
            var type:int = memoryBuffer.readByte();

            var message:Message;
            if ( type == MESSAGE_TYPE_COMPOSITE )
            {
                message = new CompositeMessage( connection );
            }
            else
            {
                message = new Message( connection );
            }

            message.readStream( memoryBuffer, protocol );
            return message;
        }

        public static function write( memoryBuffer:MemoryBuffer, message:Message, protocol:Protocol ):void
        {
            memoryBuffer.writeByte( message._type );
            message.writeStream( memoryBuffer, protocol );
        }

    }
}