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
package com.noctarius.tengi.client.transport
{
    import com.noctarius.tengi.client.ClientConnection;
    import com.noctarius.tengi.client.ConnectionConfiguration;
    import com.noctarius.tengi.client.ConnectionConstants;
    import com.noctarius.tengi.client.LinkedMessageCallback;
    import com.noctarius.tengi.client.Message;
    import com.noctarius.tengi.client.MessageListener;
    import com.noctarius.tengi.client.Protocol;
    import com.noctarius.tengi.client.Streamable;
    import com.noctarius.tengi.client.buffer.MemoryBuffer;
    import com.noctarius.tengi.client.buffer.MemoryBufferPool;

    import flash.events.EventDispatcher;
    import flash.utils.ByteArray;
    import flash.utils.Dictionary;

    public class AbstractConnection extends EventDispatcher
    {

        private var _protocol:Protocol;

        private var _memoryBufferPool:MemoryBufferPool;

        private var _connection:ClientConnection;

        private var _messageListener:MessageListener = null;

        private const _linkedMessages:Dictionary = new Dictionary();

        public function AbstractConnection( connection:ClientConnection, memoryBufferPool:MemoryBufferPool,
                                            configuration:ConnectionConfiguration )
        {
            this._connection = connection;
            this._protocol = configuration.protocol;
            this._memoryBufferPool = memoryBufferPool;
        }

        protected final function get protocol():Protocol
        {
            return _protocol;
        }

        protected final function get memoryBufferPool():MemoryBufferPool
        {
            return _memoryBufferPool;
        }

        public function setMessageListener( messageListener:* ):void
        {
            this._messageListener = messageListener;
        }

        public function clearMessageListener():void
        {
            this._messageListener = null;
        }

        protected function registerLinkedMessage( message:Message, linkedCallback:*, bubbles:Boolean ):void
        {
            if ( !(linkedCallback is Function) && !(linkedCallback is LinkedMessageCallback) )
            {
                throw new ArgumentError( "linkedCallback is not of type Function or LinkedMessageCallback" );
            }

            _linkedMessages[message.messageId] = new LinkedMessageHolder( message, linkedCallback, bubbles );
        }

        protected function notifyLinkedMessage( response:Message ):Boolean
        {
            var holder:LinkedMessageHolder = _linkedMessages[response.messageId];
            if ( holder == null )
            {
                return true;
            }

            delete _linkedMessages[response.messageId];
            var linkedCallback:* = holder.linkedCallback;
            if ( linkedCallback is Function )
            {
                (linkedCallback as Function)( holder.request, response, _connection );
            }
            else
            {
                (linkedCallback as LinkedMessageCallback).onLinkedMessageResponse( holder.request, response,
                                                                                   _connection );
            }

            return holder.bubbles;
        }

        protected function get messageListener():MessageListener
        {
            return _messageListener;
        }

        protected function createMessageFrame( message:Message, byteArray:ByteArray ):void
        {
            var memoryBuffer:MemoryBuffer = _memoryBufferPool.pop( byteArray );
            try
            {
                // Reserve 4 bytes for frame length
                memoryBuffer.writeInt( 0 );

                memoryBuffer.writeByte( ConnectionConstants.DATA_TYPE_MESSAGE );
                Message.write( memoryBuffer, message, _protocol );

                var writerIndex:int = memoryBuffer.writerIndex;
                memoryBuffer.writerIndex = 0;
                memoryBuffer.writeInt( byteArray.length );
                memoryBuffer.writerIndex = writerIndex;
            }
            finally
            {
                _memoryBufferPool.push( memoryBuffer );
            }
        }

        protected function createRawDataFrame( rawBuffer:MemoryBuffer, metadata:Streamable, byteArray:ByteArray ):void
        {
            var memoryBuffer:MemoryBuffer = _memoryBufferPool.pop( byteArray );
            try
            {
                // Reserve 4 bytes for frame length
                memoryBuffer.writeInt( 0 );

                memoryBuffer.writeByte( ConnectionConstants.DATA_TYPE_RAW );
                writeNullableObject( metadata, memoryBuffer );
                memoryBuffer.writeInt( memoryBuffer.writerIndex );
                memoryBuffer.writeBytes( memoryBuffer, 0, memoryBuffer.writerIndex );

                var writerIndex:int = memoryBuffer.writerIndex;
                memoryBuffer.writerIndex = 0;
                memoryBuffer.writeInt( byteArray.length );
                memoryBuffer.writerIndex = writerIndex;
            }
            finally
            {
                _memoryBufferPool.push( memoryBuffer );
            }
        }

        protected function writeNullableObject( streamable:Streamable, memoryBuffer:MemoryBuffer ):void
        {
            if ( streamable == null )
            {
                memoryBuffer.writeByte( 0 );
            }
            else
            {
                memoryBuffer.writeByte( 1 );
                memoryBuffer.writeShort( _protocol.getClassIdentifier( streamable ) );
                streamable.writeStream( memoryBuffer, _protocol );
            }
        }

        protected function readNullableObject( memoryBuffer:MemoryBuffer ):Streamable
        {
            if ( memoryBuffer.readByte() == 1 )
            {
                var classId:int = memoryBuffer.readShort();
                var streamable:Streamable = _protocol.instantiate( classId );
                streamable.readStream( memoryBuffer, _protocol );
                return streamable;
            }
            return null;
        }

    }
}

import com.noctarius.tengi.client.ClientConnection;
import com.noctarius.tengi.client.Message;
import com.noctarius.tengi.client.MessageFrameListener;
import com.noctarius.tengi.client.MessageListener;
import com.noctarius.tengi.client.RawFrameListener;
import com.noctarius.tengi.client.Streamable;
import com.noctarius.tengi.client.buffer.MemoryBuffer;

internal class LinkedMessageHolder
{
    private var _request:Message;

    private var _linkedCallback:*;

    private var _bubbles:Boolean;

    function LinkedMessageHolder( request:Message, linkedCallback:*, bubbles:Boolean )
    {
        this._request = request;
        this._linkedCallback = linkedCallback;
        this._bubbles = bubbles;
    }

    public function get request():Message
    {
        return _request;
    }

    public function get linkedCallback():*
    {
        return _linkedCallback;
    }

    public function get bubbles():Boolean
    {
        return _bubbles;
    }
}

internal class ClosureMessageListener implements MessageListener
{

    private var messageFrameListener:MessageFrameListener;
    private var rawFrameListener:RawFrameListener;

    public function ClosureMessageListener( messageFrameListener:MessageFrameListener,
                                            rawFrameListener:RawFrameListener )
    {
        this.messageFrameListener = messageFrameListener;
        this.rawFrameListener = rawFrameListener;
    }

    public function messageReceived( message:Message, connection:ClientConnection ):void
    {
        if ( !messageFrameListener )
        {
            messageFrameListener.messageReceived( message, connection );
        }
    }

    public function rawDataReceived( memoryBuffer:MemoryBuffer, metadata:Streamable, connection:ClientConnection ):void
    {
        if ( !rawFrameListener )
        {
            rawFrameListener.rawDataReceived( memoryBuffer, metadata, connection );
        }
    }
}
