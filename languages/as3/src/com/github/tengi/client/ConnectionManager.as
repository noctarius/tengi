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
    import com.github.tengi.client.lang.util.Console;
    import com.github.tengi.client.transport.events.ConnectionEstablishedEvent;
    import com.github.tengi.client.transport.events.ConnectionEvents;
    import com.github.tengi.client.transport.http.HttpConnection;

    import flash.events.EventDispatcher;
    import flash.events.HTTPStatusEvent;
    import flash.events.IOErrorEvent;
    import flash.events.SecurityErrorEvent;
    import flash.net.URLLoader;
    import flash.net.URLLoaderDataFormat;
    import flash.net.URLRequest;
    import flash.net.URLRequestHeader;
    import flash.net.URLRequestMethod;

    /**
     * Dispatched when a valid transportation type was selected and connection using this transport was established.
     *
     * @eventType com.github.tengi.client.transport.events.ConnectionEstablishedEvent
     */
    [Event(name="CONNECTION_ESTABLISHED", type="com.github.tengi.client.transport.events.ConnectionEstablishedEvent")]

    /**
     * The ConnectionManager is used to create gameserver connections using a {@link com.github.tengi.client.ConnectionConfiguration} object.
     * Using {@link #createConnection()} the ConnectionManager queries the server supported {@link com.github.tengi.client.TransportType}s and a suggested order to try them one by one to find best matching transport and creates a {@link com.github.tengi.client.ClientConnection} using this transport.
     */ public class ConnectionManager extends EventDispatcher
    {
        private const connections:Vector.<ClientConnection> = new Vector.<ClientConnection>();

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

        public function createHttpConnection( configuration:ConnectionConfiguration ):ClientConnection
        {
            var connection:ClientConnection = new HttpConnection( configuration, contentType, memoryBufferPool,
                                                                  serializationFactory );
            connections.push( connection );
            return connection;
        }

        public function createTcpConnection( configuration:ConnectionConfiguration ):ClientConnection
        {
            return null;
        }

        public function createConnection( configuration:ConnectionConfiguration,
                                          connectionListener:ConnectionListener = null ):void
        {
            var connectionId:UniqueId = UniqueId.randomUniqueId();
            var header:URLRequestHeader = new URLRequestHeader( ConnectionConstants.HTTP_HEADER_NAME_CONNECTIONID,
                                                                connectionId.toString() );

            var request:URLRequest = new URLRequest();
            request.method = URLRequestMethod.HEAD;
            request.requestHeaders.push( header );

            var urlLoader:URLLoader = new URLLoader();
            urlLoader.dataFormat = URLLoaderDataFormat.TEXT;
            urlLoader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler );
            urlLoader.addEventListener( HTTPStatusEvent.HTTP_RESPONSE_STATUS, httpStatusHandler );
            urlLoader.addEventListener( IOErrorEvent.IO_ERROR, ioErrorHandler );

            var connection:ClientConnection = createHttpConnection( configuration );

            if ( connectionListener != null )
            {
                connectionListener.onConnect( connection );
            }

            dispatchEvent( new ConnectionEstablishedEvent( connection, ConnectionEvents.CONNECTION_ESTABLISHED ) );
        }

        private function httpStatusHandler( event:HTTPStatusEvent ):void
        {
            for each ( var header:URLRequestHeader  in event.responseHeaders )
            {
                if ( header.name == ConnectionConstants.HTTP_HEADER_NAME_SUPPORTED_TRANSPORT_TYPES )
                {
                    var transports:Array = header.value.split( "," );

                }
            }
        }

        private function securityErrorHandler( event:SecurityErrorEvent ):void
        {
            Console.log( "securityErrorHandler: " + event );
        }

        private function ioErrorHandler( event:IOErrorEvent ):void
        {
            Console.log( "ioErrorHandler: " + event );
        }

    }
}
