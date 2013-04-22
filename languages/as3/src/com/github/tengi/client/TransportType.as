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

        public static const TCP:TransportType = new TransportType( "TCP", 0, _ );

        public static const UDP:TransportType = new TransportType( "UDP", 1, _ );

        public static const HTTP_POLLING:TransportType = new TransportType( "HTTP_POLLING", 2, _ );

        public static const HTTP_LONG_POLLING:TransportType = new TransportType( "HTTP_LONG_POLLING", 3, _ );

        public static const WEBSOCKET:TransportType = new TransportType( "WEBSOCKET", 4, _ );

        public static const SPDY:TransportType = new TransportType( "SPDY", 5, _ );

        public static const SSL_PROXY:TransportType = new TransportType( "SSL_PROXY", 6, _ );

        function TransportType( name:String, ordinal:int, restrictor:* ):void
        {
            super( name, ordinal, restrictor );
        }

        internal static function get constants():Array
        {
            return [ TCP, UDP, HTTP_POLLING, HTTP_LONG_POLLING, WEBSOCKET, SPDY, SSL_PROXY ];
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
