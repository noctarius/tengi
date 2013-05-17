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

    public interface Connection extends IEventDispatcher
    {

        function getTransportType():TransportType;

        function sendMessage( message:Message, success:Function = null, failure:Function = null ):void;

        function sendRawData( memoryBuffer:MemoryBuffer, metadata:Streamable = null, success:Function = null,
                              failure:Function = null ):void;

        function setMessageListener( messageListener:MessageListener ):void;

        function clearMessageListener():void;

        function registerLongPollingRequestFactory( longPollingRequestFactory:LongPollingRequestFactory ):void;

        function startLongPollingCycle():void;

        function prepareMessage( body:Streamable, longPolling:Boolean = false ):Message;

        function close():void;

    }
}
