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
    import com.github.tengi.client.lang.IllegalArgumentError;

    import org.as3commons.lang.IIterator;

    public class CompositeMessage extends Message
    {

        private var _messages:Vector.<Message>;

        public function CompositeMessage( serializationFactory:SerializationFactory, connection:Connection,
                                          messages:Vector.<Message>, messageId:UniqueId = null, type:int = 0 )
        {
            super( serializationFactory, connection, null, messageId, Message.MESSAGE_TYPE_COMPOSITE );
            this._messages = messages;
        }

        override public function readStream( memoryBuffer:MemoryBuffer ):void
        {
            super.readStream( memoryBuffer );
            memoryBuffer.writeInt( _messages.length );
            for ( var i:int = _messages.length - 1; i >= 0; i-- )
            {
                _messages[i].writeStream( memoryBuffer );
            }
        }

        override public function writeStream( memoryBuffer:MemoryBuffer ):void
        {
            super.writeStream( memoryBuffer );

            _messages = new Vector.<Message>();
            var length:int = memoryBuffer.readInt();
            for ( var i:int = 0; i < length; i++ )
            {
                var message:Message = new Message( _serializationFactory, connection );
                message.readStream( memoryBuffer );
                _messages.push( message );
            }
        }

        public function get messageCount():int
        {
            return _messages.length;
        }

        public function retrieveMessage( index:int ):Message
        {
            var count = messageCount;
            if ( index >= count )
            {
                throw new IllegalArgumentError( "index is out of range, max message index is " + (count - 1) );
            }
            return _messages[index];
        }

        public function get iterator():IIterator
        {
            return new CompositeMessageIterator( _messages );
        }

    }

}

import com.github.tengi.client.Message;

import org.as3commons.lang.IIterator;

internal class CompositeMessageIterator implements IIterator
{

    private var messages:Vector.<Message>;
    private var index:int = 0;

    function CompositeMessageIterator( messages:Vector.<Message> )
    {
        this.messages = messages;
    }

    public function first():void
    {
        index = 0;
    }

    public function last():void
    {
        index = messages.length - 1;
    }

    public function next():Object
    {
        return messages[index++];
    }
}