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

    import com.github.tengi.client.lang.IllegalArgumentError;
    import com.github.tengi.client.lang.util.Enum;

    public final class TransportType extends Enum
    {

        public static const TCP:TransportType = new TransportType( "TCP", 0, "tengi-tcp", _ );

        public static const UDP:TransportType = new TransportType( "UDP", 1, "tengi-udp", _ );

        public static const HTTP_POLLING:TransportType = new TransportType( "HTTP_POLLING", 2, "tengi-http-polling",
                                                                            _ );

        public static const HTTP_LONG_POLLING:TransportType = new TransportType( "HTTP_LONG_POLLING", 3,
                                                                                 "tengi-http-longpolling", _ );

        public static const WEBSOCKET:TransportType = new TransportType( "WEBSOCKET", 4, "tengi-websocket", _ );

        public static const SPDY:TransportType = new TransportType( "SPDY", 5, "tengi-spdy", _ );

        private var _transport:String;

        function TransportType( name:String, ordinal:int, transport:String, restrictor:* ):void
        {
            super( name, ordinal, restrictor );
            this._transport = transport;
        }

        public function get transport():String
        {
            return _transport;
        }

        internal static function get constants():Array
        {
            return [ TCP, UDP, HTTP_POLLING, HTTP_LONG_POLLING, WEBSOCKET, SPDY ];
        }

        public static function valueOf( name:String ):TransportType
        {
            try
            {
                return TransportType( TCP.constantOf( name ) );
            }
            catch ( e:Error )
            {
                throw new IllegalArgumentError( e.message );
            }

            return null;
        }

        override protected function getConstants():Array
        {
            return constants;
        }

    }
}
