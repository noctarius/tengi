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
    import com.github.tengi.client.buffer.MemoryBufferPool;
    import com.github.tengi.client.transport.events.ConnectionEstablishedEvent;
    import com.github.tengi.client.transport.events.ConnectionEvents;
    import com.github.tengi.client.transport.http.HttpConnection;

    import flash.events.EventDispatcher;

    /**
     * Dispatched when a valid transportation type was selected and connection using this transport was established.
     *
     * @eventType com.github.tengi.client.transport.events.ConnectionEstablishedEvent
     */
    [Event(name="CONNECTION_ESTABLISHED", type="com.github.tengi.client.transport.events.ConnectionEstablishedEvent")]

    public class ConnectionManager extends EventDispatcher
    {
        private const connections:Vector.<Connection> = new Vector.<Connection>();

        private var serializationFactory:SerializationFactory;
        private var memoryBufferPool:MemoryBufferPool;
        private var contentType:String;

        public function ConnectionManager( contentType:String, memoryBufferPoolSize:int,
                                           serializationFactory:SerializationFactory )
        {
            this.contentType = contentType;
            this.memoryBufferPool = new MemoryBufferPool( memoryBufferPoolSize );
            this.serializationFactory = serializationFactory;
        }

        public function createHttpConnection( configuration:ConnectionConfiguration ):Connection
        {
            var connection:Connection = new HttpConnection( configuration, contentType, memoryBufferPool,
                                                            serializationFactory );
            connections.push( connection );
            return connection;
        }

        public function createTcpConnection( configuration:ConnectionConfiguration ):Connection
        {
            return null;
        }

        public function createConnection( configuration:ConnectionConfiguration,
                                          connectionListener:ConnectionListener = null ):void
        {
            var connection:Connection = createHttpConnection( configuration );

            if ( connectionListener != null )
            {
                connectionListener.onConnect( connection );
            }

            dispatchEvent( new ConnectionEstablishedEvent( connection, ConnectionEvents.CONNECTION_ESTABLISHED ) );
        }

    }
}
