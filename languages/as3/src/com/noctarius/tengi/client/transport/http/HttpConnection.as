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
package com.noctarius.tengi.client.transport.http
{
    import com.noctarius.tengi.client.ClientConnection;
    import com.noctarius.tengi.client.ConnectionConfiguration;
    import com.noctarius.tengi.client.ConnectionConstants;
    import com.noctarius.tengi.client.LongPollingRequestFactory;
    import com.noctarius.tengi.client.Message;
    import com.noctarius.tengi.client.Streamable;
    import com.noctarius.tengi.client.TransportType;
    import com.noctarius.tengi.client.UniqueId;
    import com.noctarius.tengi.client.buffer.MemoryBuffer;
    import com.noctarius.tengi.client.buffer.MemoryBufferPool;
    import com.noctarius.tengi.client.lang.util.Console;
    import com.noctarius.tengi.client.transport.AbstractConnection;
    import com.noctarius.tengi.client.transport.events.ConnectionEvents;
    import com.noctarius.tengi.client.transport.events.MessageReceivedEvent;
    import com.noctarius.tengi.client.transport.events.RawDataReceivedEvent;
    import com.noctarius.tengi.client.transport.polling.PollingMessage;

    import flash.errors.IOError;
    import flash.events.Event;
    import flash.events.HTTPStatusEvent;
    import flash.events.IOErrorEvent;
    import flash.events.SecurityErrorEvent;
    import flash.events.TimerEvent;
    import flash.net.URLLoader;
    import flash.net.URLLoaderDataFormat;
    import flash.net.URLRequest;
    import flash.net.URLRequestHeader;
    import flash.net.URLRequestMethod;
    import flash.utils.ByteArray;
    import flash.utils.Dictionary;
    import flash.utils.Timer;
    import flash.utils.getTimer;

    public class HttpConnection extends AbstractConnection implements ClientConnection
    {

        private const requests:Dictionary = new Dictionary();

        private const longPollingUrlLoader:URLLoader = new URLLoader();

        private var longPollingRequestFactory:LongPollingRequestFactory = null;

        private var contentType:String;
        private var contextPath:String;
        private var host:String;
        private var port:int;
        private var ssl:Boolean;
        private var url:String;

        private var lastUpdateId:int;

        private var closed:Boolean = false;

        private var lastLongPollTime:Number = getTimer();

        public function HttpConnection( configuration:ConnectionConfiguration, memoryBufferPool:MemoryBufferPool )
        {
            super( this, memoryBufferPool, configuration );

            this.contentType = contentType;
            this.contextPath = configuration.httpContext;
            this.host = configuration.host;
            this.port = configuration.port;
            this.ssl = configuration.ssl;

            url = (ssl ? "https://" : "http://") + host + ":" + port + "/" + contextPath;

            longPollingUrlLoader.dataFormat = URLLoaderDataFormat.BINARY;
            longPollingUrlLoader.addEventListener( Event.COMPLETE, longPollingCompleteListener );
            longPollingUrlLoader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, longPollingSecurityErrorHandler );
            longPollingUrlLoader.addEventListener( HTTPStatusEvent.HTTP_STATUS, longPollingHttpStatusHandler );
            longPollingUrlLoader.addEventListener( IOErrorEvent.IO_ERROR, longPollingIoErrorHandler );
        }

        public function getTransportType():TransportType
        {
            return TransportType.HTTP_LONG_POLLING;
        }

        public function sendLinkedMessage( message:Message, linkedCallback:*, bubbles:Boolean = false,
                                           success:Function = null, failure:Function = null ):void
        {
            registerLinkedMessage( message, linkedCallback, bubbles );
            sendMessage( message, success, failure );
        }

        public function sendObject( body:Streamable, success:Function = null, failure:Function = null ):void
        {
            var message:Message = prepareMessage( body );
            sendMessage( message, success, failure );
        }

        public function sendLinkedObject( body:Streamable, linkedCallback:*, bubbles:Boolean = false,
                                          success:Function = null, failure:Function = null ):void
        {
            var message:Message = prepareMessage( body );
            sendLinkedMessage( message, linkedCallback, bubbles, success, failure );
        }

        public function sendMessage( message:Message, success:Function = null, failure:Function = null ):void
        {
            try
            {
                if ( message.connection != this )
                {
                    throw Error( "The sending connection is not the one that created the message" );
                }

                var output:ByteArray = new ByteArray();
                createMessageFrame( message, output );

                var request:URLRequest = createURLRequest( output );
                var urlLoader:URLLoader = createURLLoader();

                var requestMapper:RequestMapper = new RequestMapper();
                requests[urlLoader] = requestMapper;

                urlLoader.load( request );

                if ( success != null )
                {
                    success();
                }
            }
            catch ( error:Error )
            {
                if ( failure != null )
                {
                    failure( error, message, this );
                }
            }
        }

        public function sendRawData( memoryBuffer:MemoryBuffer, metadata:Streamable = null, success:Function = null,
                                     failure:Function = null ):void
        {
            try
            {
                var output:ByteArray = new ByteArray();
                createRawDataFrame( memoryBuffer, metadata, output );

                var request:URLRequest = createURLRequest( output );
                var urlLoader:URLLoader = createURLLoader();

                var requestMapper:RequestMapper = new RequestMapper();
                requests[urlLoader] = requestMapper;

                urlLoader.load( request );

                if ( success != null )
                {
                    success();
                }
            }
            catch ( error:Error )
            {
                if ( failure != null )
                {
                    failure( error, null, this );
                }
            }
        }

        public function close():void
        {
            closed = true;
        }

        public function registerLongPollingRequestFactory( longPollingRequestFactory:LongPollingRequestFactory ):void
        {
            if ( this.longPollingRequestFactory != null )
            {
                throw new ArgumentError( "LongPollingRequestFactory is already set" );
            }

            this.longPollingRequestFactory = longPollingRequestFactory;
        }

        public function prepareMessage( body:Streamable, longPolling:Boolean = false ):Message
        {
            var messageId:UniqueId = UniqueId.randomUniqueId();
            return longPolling ? new PollingMessage( this, messageId, lastUpdateId ) : new Message( this, body,
                                                                                                    messageId,
                                                                                                    Message.MESSAGE_TYPE_DEFAULT );
        }

        public function startLongPollingCycle():void
        {
            if ( closed )
            {
                return;
            }

            if ( getTimer() - lastLongPollTime < 500 )
            {
                var timer:Timer = new Timer( 100, 1 );
                timer.addEventListener( TimerEvent.TIMER_COMPLETE, longPollingTimerFinished );
                timer.start();

                return;
            }

            var message:Message;
            if ( longPollingRequestFactory != null )
            {
                var body:Streamable = longPollingRequestFactory.prepareLongPollingRequest( this );
                message = prepareMessage( body, true );
            }
            else
            {
                message = prepareMessage( null, true );
            }

            var output:ByteArray = new ByteArray();
            var memoryBuffer:MemoryBuffer = memoryBufferPool.pop( output );
            try
            {
                Message.write( memoryBuffer, message, protocol );
            }
            finally
            {
                memoryBufferPool.push( memoryBuffer );
            }

            var request:URLRequest = createURLRequest( output );
            longPollingUrlLoader.load( request );
        }

        private function createURLRequest( byteArray:ByteArray ):URLRequest
        {
            var request:URLRequest = new URLRequest( url );
            request.method = URLRequestMethod.POST;
            request.contentType = contentType;
            request.data = byteArray;

            return request;
        }

        private function createURLLoader():URLLoader
        {
            var urlLoader:URLLoader = new URLLoader();
            urlLoader.dataFormat = URLLoaderDataFormat.BINARY;
            urlLoader.addEventListener( Event.COMPLETE, callCompleteListener );
            urlLoader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, callSecurityErrorHandler );
            urlLoader.addEventListener( HTTPStatusEvent.HTTP_RESPONSE_STATUS, callHttpStatusHandler );
            urlLoader.addEventListener( IOErrorEvent.IO_ERROR, callIoErrorHandler );

            return urlLoader;
        }

        private function longPollingCompleteListener( event:Event ):void
        {
            var data:ByteArray = longPollingUrlLoader.data as ByteArray;
            handleMessage( data );

            lastLongPollTime = getTimer();
            startLongPollingCycle();
        }

        private function longPollingSecurityErrorHandler( event:SecurityErrorEvent ):void
        {
            Console.log( "longPollingSecurityErrorHandler: " + event );

            lastLongPollTime = getTimer();
            startLongPollingCycle();
        }

        private function longPollingHttpStatusHandler( event:HTTPStatusEvent ):void
        {
            if ( event.status != 200 )
            {
                Console.log( "longPollingHttpStatusHandler: " + event );
                lastLongPollTime = getTimer();
                startLongPollingCycle();
            }
        }

        private function longPollingIoErrorHandler( event:IOErrorEvent ):void
        {
            Console.log( "longPollingIoErrorHandler: " + event );

            lastLongPollTime = getTimer();
            startLongPollingCycle();
        }

        private function callCompleteListener( event:Event ):void
        {
            var loader:URLLoader = event.target as URLLoader;

            var requestMapper:RequestMapper = requests[loader];
            delete requests[loader];
            if ( requestMapper.responseHeaders != null )
            {
                for each ( var header:URLRequestHeader in requestMapper.responseHeaders )
                {
                    Console.log( header.name + " = " + header.value );
                    if ( "Content-Type" == header.name && contentType != header.value )
                    {
                        throw new IOError( "Illegal content-type received: " + header.value );
                    }
                }
            }

            var data:ByteArray = loader.data as ByteArray;
            handleMessage( data );
        }

        private function callSecurityErrorHandler( event:SecurityErrorEvent ):void
        {
            Console.log( "callSecurityErrorHandler: " + event );
            delete requests[event.target];
        }

        private function callHttpStatusHandler( event:HTTPStatusEvent ):void
        {
            if ( event.status != 200 )
            {
                Console.log( "callHttpStatusHandler: " + event );
            }

            var requestMapper:RequestMapper = requests[event.target as URLLoader];
            requestMapper.httpStatus = event.status;
            requestMapper.responseHeaders = event.responseHeaders;
        }

        private function callIoErrorHandler( event:IOErrorEvent ):void
        {
            Console.log( "callIoErrorHandler: " + event );
            delete requests[event.target];
        }

        private function longPollingTimerFinished( event:TimerEvent ):void
        {
            startLongPollingCycle();
        }

        private function handleMessage( data:ByteArray ):void
        {
            var length:int = data.readInt();
            if ( length > data.length )
            {
                throw new IOError( "Not enough data" );
            }

            var memoryBuffer:MemoryBuffer = memoryBufferPool.pop( data );
            try
            {
                var dataType:int = memoryBuffer.readByte();
                if ( dataType == ConnectionConstants.DATA_TYPE_MESSAGE )
                {
                    var message:Message = Message.read( memoryBuffer, protocol, this );

                    requests[message.messageId] = null;

                    if ( notifyLinkedMessage( message ) )
                    {
                        if ( messageListener != null )
                        {
                            messageListener.messageReceived( message, this );
                        }

                        dispatchEvent( new MessageReceivedEvent( ConnectionEvents.MESSAGE_RECEIVED, message ) );
                    }
                }
                else if ( dataType == ConnectionConstants.DATA_TYPE_RAW )
                {
                    var metadata:Streamable = readNullableObject( memoryBuffer );

                    var bufferLength:int = memoryBuffer.readInt();
                    var data:ByteArray = new ByteArray();
                    memoryBuffer.readBytes( data, 0, bufferLength );

                    var rawBuffer:MemoryBuffer = new MemoryBuffer( data );
                    if ( messageListener != null )
                    {
                        messageListener.rawDataReceived( rawBuffer, metadata, this );
                    }

                    dispatchEvent( new RawDataReceivedEvent( ConnectionEvents.RAWDATA_RECEIVED, rawBuffer, metadata ) );
                }
            }
            finally
            {
                memoryBufferPool.push( memoryBuffer );
            }
        }

    }

}

internal class RequestMapper
{
    internal var responseHeaders:Array;

    internal var httpStatus:int;
}
