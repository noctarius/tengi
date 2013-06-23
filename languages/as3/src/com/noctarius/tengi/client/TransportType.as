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
package com.noctarius.tengi.client
{

    import com.noctarius.tengi.client.lang.Enum;
    import com.noctarius.tengi.client.lang.IllegalArgumentError;

    public final class TransportType extends Enum
    {

        private static const ENFORCER:Object = new Object();

        public static const TCP:TransportType = new TransportType( "TCP", "tengi-tcp" );

        public static const UDP:TransportType = new TransportType( "UDP", "tengi-udp" );

        public static const HTTP_POLLING:TransportType = new TransportType( "HTTP_POLLING", "tengi-http-polling" );

        public static const HTTP_LONG_POLLING:TransportType = new TransportType( "HTTP_LONG_POLLING", "tengi-http-longpolling" );

        public static const WEBSOCKET:TransportType = new TransportType( "WEBSOCKET", "tengi-websocket" );

        public static const SPDY:TransportType = new TransportType( "SPDY", "tengi-spdy" );

        public static const SSL_PROXY:TransportType = new TransportType( "SSL_PROXY", "tengi-ssl-proxy" );

        {
            finalizeEnumType( TransportType, ENFORCER );
        }

        private var _transport:String;

        function TransportType( name:String, transport:String ):void
        {
            super( name, ENFORCER );
            this._transport = transport;
        }

        public function get transport():String
        {
            return _transport;
        }

        public static function valueOf( name:String ):TransportType
        {
            try
            {
                return TransportType( TCP.byName( name ) );
            }
            catch ( e:Error )
            {
                throw new IllegalArgumentError( e.message );
            }

            return null;
        }

        public static function values():Array
        {
            return TCP.getConstants();
        }

    }
}
