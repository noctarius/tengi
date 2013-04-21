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
package com.github.tengi.client.transport.http
{
    import com.github.tengi.client.Connection;
    import com.github.tengi.client.ConnectionConstants;
    import com.github.tengi.client.LongPollingRequestFactory;
    import com.github.tengi.client.Message;
    import com.github.tengi.client.MessageListener;
    import com.github.tengi.client.SerializationFactory;
    import com.github.tengi.client.Streamable;
    import com.github.tengi.client.TransportType;
    import com.github.tengi.client.UniqueId;
    import com.github.tengi.client.buffer.MemoryBuffer;

    import flash.errors.IOError;
    import flash.events.Event;
    import flash.events.HTTPStatusEvent;
    import flash.events.IOErrorEvent;
    import flash.events.SecurityErrorEvent;
    import flash.events.TimerEvent;
    import flash.external.ExternalInterface;
    import flash.net.URLLoader;
    import flash.net.URLLoaderDataFormat;
    import flash.net.URLRequest;
    import flash.net.URLRequestHeader;
    import flash.net.URLRequestMethod;
    import flash.utils.ByteArray;
    import flash.utils.Dictionary;
    import flash.utils.Timer;
    import flash.utils.getTimer;

    public class HttpConnection implements Connection
    {

        private const requests:Dictionary = new Dictionary();

        private const longPollingUrlLoader:URLLoader = new URLLoader();

        private var messageListener:MessageListener = null;

        private var longPollingRequestFactory:LongPollingRequestFactory = null;

        private var serializationFactory:SerializationFactory;
        private var contentType:String;
        private var contextPath:String;
        private var host:String;
        private var port:int;
        private var ssl:Boolean;
        private var url:String;

        private var closed:Boolean = false;

        private var lastLongPollTime = getTimer();

        public function HttpConnection( host:String, port:int, contextPath:String, ssl:Boolean, contentType:String,
                                        serializationFactory:SerializationFactory )
        {
            this.serializationFactory = serializationFactory;
            this.contentType = contentType;
            this.contextPath = contextPath;
            this.host = host;
            this.port = port;
            this.ssl = ssl;

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

        public function sendMessage( message:Message, success:Function = null, failure:Function = null ):void
        {
            try
            {
                if ( message.connection != this )
                {
                    throw Error( "The sending connection is not the one that created the message" );
                }

                var output:ByteArray = new ByteArray();
                var memoryBuffer:MemoryBuffer = new MemoryBuffer( output );
                memoryBuffer.writeByte( ConnectionConstants.DATA_TYPE_MESSAGE );
                Message.write( memoryBuffer, message );

                var request:URLRequest = new URLRequest( url );
                request.method = URLRequestMethod.POST;
                request.contentType = contentType;
                request.data = output;

                var urlLoader:URLLoader = new URLLoader();
                urlLoader.dataFormat = URLLoaderDataFormat.BINARY;
                urlLoader.addEventListener( Event.COMPLETE, callCompleteListener );
                urlLoader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, callSecurityErrorHandler );
                urlLoader.addEventListener( HTTPStatusEvent.HTTP_RESPONSE_STATUS, callHttpStatusHandler );
                urlLoader.addEventListener( IOErrorEvent.IO_ERROR, callIoErrorHandler );

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
                var memoryBuffer:MemoryBuffer = new MemoryBuffer( output );
                memoryBuffer.writeByte( ConnectionConstants.DATA_TYPE_RAW );

                if ( metadata == null )
                {
                    memoryBuffer.writeByte( 0 );
                }
                else
                {
                    memoryBuffer.writeByte( 1 );
                    memoryBuffer.writeShort( serializationFactory.getClassIdentifier( metadata ) );
                    metadata.writeStream( memoryBuffer );
                }

                memoryBuffer.writeBytes( memoryBuffer, 0, memoryBuffer.writerIndex );

                var request:URLRequest = new URLRequest( url );
                request.method = URLRequestMethod.POST;
                request.contentType = contentType;
                request.data = output;

                var urlLoader:URLLoader = new URLLoader();
                urlLoader.dataFormat = URLLoaderDataFormat.BINARY;
                urlLoader.addEventListener( Event.COMPLETE, callCompleteListener );
                urlLoader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, callSecurityErrorHandler );
                urlLoader.addEventListener( HTTPStatusEvent.HTTP_RESPONSE_STATUS, callHttpStatusHandler );
                urlLoader.addEventListener( IOErrorEvent.IO_ERROR, callIoErrorHandler );

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

        public function setMessageListener( messageListener:MessageListener ):void
        {
            this.messageListener = messageListener;
        }

        public function clearMessageListener():void
        {
            messageListener = null;
        }

        public function prepareMessage( body:Streamable, longPolling:Boolean = false ):Message
        {
            var type:int = longPolling ? Message.MESSAGE_TYPE_LONG_POLLING : Message.MESSAGE_TYPE_DEFAULT;
            return new Message( serializationFactory, this, body, UniqueId.randomUniqueId(), type );
        }

        public function startLongPollingCycle():void
        {
            if ( closed )
            {
                return;
            }

            if ( getTimer() - lastLongPollTime < 100 )
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
            var memoryBuffer:MemoryBuffer = new MemoryBuffer( output );
            Message.write( memoryBuffer, message );

            var request:URLRequest = new URLRequest( url );
            request.method = URLRequestMethod.POST;
            request.contentType = contentType;
            request.data = output;

            longPollingUrlLoader.load( request );
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
            log( "longPollingSecurityErrorHandler: " + event );

            lastLongPollTime = getTimer();
            startLongPollingCycle();
        }

        private function longPollingHttpStatusHandler( event:HTTPStatusEvent ):void
        {
            if ( event.status != 200 )
            {
                log( "longPollingHttpStatusHandler: " + event );
                lastLongPollTime = getTimer();
                startLongPollingCycle();
            }
        }

        private function longPollingIoErrorHandler( event:IOErrorEvent ):void
        {
            log( "longPollingIoErrorHandler: " + event );

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
                    log( header.name + " = " + header.value );
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
            log( "callSecurityErrorHandler: " + event );
            delete requests[event.target];
        }

        private function callHttpStatusHandler( event:HTTPStatusEvent ):void
        {
            if ( event.status != 200 )
            {
                log( "callHttpStatusHandler: " + event );
            }

            var requestMapper:RequestMapper = requests[event.target as URLLoader];
            requestMapper.httpStatus = event.status;
            requestMapper.responseHeaders = event.responseHeaders;
        }

        private function callIoErrorHandler( event:IOErrorEvent ):void
        {
            log( "callIoErrorHandler: " + event );
            delete requests[event.target];
        }

        private function longPollingTimerFinished( event:TimerEvent ):void
        {
            startLongPollingCycle();
        }

        public function log( message:String ):void
        {
            ExternalInterface.call( "console.log", message );
        }

        private function handleMessage( data:ByteArray ):void
        {
            var memoryBuffer:MemoryBuffer = new MemoryBuffer( data );

            var dataType:int = memoryBuffer.readByte();
            if ( dataType == ConnectionConstants.DATA_TYPE_MESSAGE )
            {
                var message:Message = Message.read( memoryBuffer, serializationFactory, this );

                requests[message.messageId] = null;

                if ( messageListener != null )
                {
                    messageListener.messageReceived( message, this );
                }
            }
            else if ( dataType == ConnectionConstants.DATA_TYPE_RAW )
            {
                //TODO read metadata from stream

                var length:int = memoryBuffer.readInt();
                var data:ByteArray = new ByteArray();
                memoryBuffer.readBytes( data, 0, length );

                if ( messageListener != null )
                {
                    messageListener.dataReceived( new MemoryBuffer( data ), this );
                }
            }
        }

    }

}

internal class RequestMapper
{
    var responseHeaders:Array;

    var httpStatus:int;
}
