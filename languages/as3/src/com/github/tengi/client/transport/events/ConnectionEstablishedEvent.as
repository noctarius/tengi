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
package com.github.tengi.client.transport.events
{
    import com.github.tengi.client.ClientConnection;

    import flash.events.Event;

    public class ConnectionEstablishedEvent extends Event
    {

        private var _connection:ClientConnection;

        public function ConnectionEstablishedEvent( connection:ClientConnection, type:String, bubbles:Boolean = false,
                                                    cancelable:Boolean = false )
        {
            super( type, bubbles, cancelable );
            this._connection = connection;
        }

        public function get connection():ClientConnection
        {
            return _connection;
        }

        public override function clone():Event
        {
            return new ConnectionEstablishedEvent( connection, type, bubbles, cancelable );
        }
    }
}