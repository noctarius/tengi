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

    import flash.events.IEventDispatcher;

    /**
     * Dispatched whenever a message is received that was read from the underlying stream.
     *
     * @eventType com.github.tengi.client.transport.events.MessageReceivedEvent
     */
    [Event(name="MESSAGE_RECEIVED", type="com.github.tengi.client.transport.events.MessageReceivedEvent")]

    /**
     * Dispatched whenever a rawdata frame is received that was read from the underlying stream.
     * Possible an additional Streamable object is available as directly usable metadata.
     *
     * @eventType com.github.tengi.client.transport.events.RawDataReceivedEvent
     */
    [Event(name="RAWDATA_REVEIVED", type="com.github.tengi.client.transport.events.RawDataReceivedEvent")]

    /**
     * A ClientConnection represents a connection to the configured gameserver(s).
     * It is capable of sending rawdata as well as message frames.
     * The underlying, typically automatically selected {@link com.github.tengi.client.TransportType} is exposed by the {@link #getTransportType()} method.
     */
    public interface ClientConnection extends IEventDispatcher
    {

        /**
         * Returns the underlying transportation type, such as HTTP-LONGPOLLING, WEBSOCKET, TCP, ...
         *
         * @return Underlying transportation type
         */
        function getTransportType():TransportType;

        /**
         * Sends a message frame to the gameserver. success and failure are optional callback functions that could be used to getting informed of corresponding events.
         * For success the following parameters are required:
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         * For failure the following parameters are required:
         * - error : {@link Error} The thrown error
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         * @param body The streamable object to be send
         * @param success An optional success callback function
         * @param failure An optional failure callback function
         */
        function sendObject( body:Streamable, success:Function = null, failure:Function = null ):void;

        /**
         * Sends a message frame to the gameserver and registers a specialized callback function for handling the gameservers response.
         * linkedCallback can be of two different types. Either of type {@link com.github.tengi.client.LinkedMessageCallback} or a Function type with following parameter signature:
         * - request : {@link com.github.tengi.client.Message} The message that was send
         * - response : {@link com.github.tengi.client.Message} The gameservers response
         * - connection : ClientConnection This connection
         *
         * success and failure are optional callback functions that could be used to getting informed of corresponding events.
         *
         * For success the following parameters are required:
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         * For failure the following parameters are required:
         * - error : {@link Error} The thrown error
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         *
         * @param body The streamable object to be send
         * @param linkedCallback The callback object to be invoked on receiving gameservers response
         * @param bubbles Defines if the event should be bubbled as normal arrival event to registered {@link com.github.tengi.client.MessageListener}
         * @param success An optional success callback function
         * @param failure An optional failure callback function
         */
        function sendLinkedObject( body:Streamable, linkedCallback:*, bubbles:Boolean = false, success:Function = null,
                                   failure:Function = null ):void

        /**
         * Sends a message frame to the gameserver. success and failure are optional callback functions that could be used to getting informed of corresponding events.
         * For success the following parameters are required:
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         * For failure the following parameters are required:
         * - error : {@link Error} The thrown error
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         * @param message The message object to be send
         * @param success An optional success callback function
         * @param failure An optional failure callback function
         */
        function sendMessage( message:Message, success:Function = null, failure:Function = null ):void;

        /**
         * Sends a message frame to the gameserver and registers a specialized callback function for handling the gameservers response.
         * linkedCallback can be of two different types. Either of type {@link com.github.tengi.client.LinkedMessageCallback} or a Function type with following parameter signature:
         * - request : {@link com.github.tengi.client.Message} The message that was send
         * - response : {@link com.github.tengi.client.Message} The gameservers response
         * - connection : ClientConnection This connection
         *
         * success and failure are optional callback functions that could be used to getting informed of corresponding events.
         *
         * For success the following parameters are required:
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         * For failure the following parameters are required:
         * - error : {@link Error} The thrown error
         * - message : {@link com.github.tengi.client.Message} The message that was send
         * - connection : ClientConnection This connection
         *
         *
         * @param message The message object to be send
         * @param linkedCallback The callback object to be invoked on receiving gameservers response
         * @param bubbles Defines if the event should be bubbled as normal arrival event to registered {@link com.github.tengi.client.MessageListener}
         * @param success An optional success callback function
         * @param failure An optional failure callback function
         */
        function sendLinkedMessage( message:Message, linkedCallback:*, bubbles:Boolean = false, success:Function = null,
                                    failure:Function = null ):void

        /**
         * Sends a rawdata frame to the gameserver with an optional metadata {@link com.github.tengi.client.Streamable} object. success and failure are optional callback functions that could be used to getting informed of corresponding events.
         * For success the following parameters are required:
         * - message : {@link com.github.tengi.client.Message} Is always null for rawdata
         * - connection : ClientConnection This connection
         *
         * For failure the following parameters are required:
         * - error : {@link Error} The thrown error
         * - message : {@link com.github.tengi.client.Message} Is always null for rawdata
         * - connection : ClientConnection This connection
         *
         * @param memoryBuffer The raw buffer tp be send
         * @param metadata Optional metadata object to be send with the buffer
         * @param success An optional success callback function
         * @param failure An optional failure callback function
         */
        function sendRawData( memoryBuffer:MemoryBuffer, metadata:Streamable = null, success:Function = null,
                              failure:Function = null ):void;

        /**
         * Configures the {@link com.github.tengi.client.MessageListener} to be used for notifying of arrival of new messages
         *
         * @param messageListener The notified MessageListener
         */
        function setMessageListener( messageListener:MessageListener ):void;

        /**
         * Removes the configured {@link com.github.tengi.client.MessageListener}
         */
        function clearMessageListener():void;

        /**
         * For longpolling transports a {@link com.github.tengi.client.LongPollingRequestFactory} is needed to prepare a LongPolling object specific to the gameservers implementation.
         *
         * @param longPollingRequestFactory The LongPollingRequestFactory to be used for creating LongPolling request objects
         */
        function registerLongPollingRequestFactory( longPollingRequestFactory:LongPollingRequestFactory ):void;

        /**
         * For longpolling transports this method registers the internal longpolling requester which automatically retrieves updates on near realtime. On non-longpolling transports calls to this method are just discarded.
         */
        function startLongPollingCycle():void;

        /**
         * Prepares a new {@link com.github.tengi.client.Message} object containing the given body and optionally defines if it will be used as a longpolling request object.
         *
         * @param body The Streamable object to be used as body object inside of the message
         * @param longPolling Will the returned Message used for a longpolling request
         * @return A newly created Message object containing the given body object
         */
        function prepareMessage( body:Streamable, longPolling:Boolean = false ):Message;

        /**
         * Closes the connection and frees all assigned resources.
         */
        function close():void;

    }
}
