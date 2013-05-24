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
    public class ConnectionConfiguration
    {

        private var _protocol:Protocol;

        private var _port:int = 80;
        private var _host:String = "localhost";
        private var _wssContext:String = "/wss";
        private var _httpContext:String = "/http";
        private var _ssl:Boolean = true;

        public function ConnectionConfiguration( protocol:Protocol )
        {
            this._protocol = protocol;
        }

        public function get mimeType():String
        {
            return _protocol.mimeType;
        }

        public function get protocol():Protocol
        {
            return _protocol;
        }

        public function get port():int
        {
            return _port;
        }

        public function set port( port:int ):void
        {
            _port = port;
        }

        public function get host():String
        {
            return _host;
        }

        public function set host( host:String ):void
        {
            _host = host;
        }

        public function get wssContext():String
        {
            return _wssContext;
        }

        public function set wssContext( wssContext:String ):void
        {
            _wssContext = wssContext;
        }

        public function get httpContext():String
        {
            return _httpContext;
        }

        public function set httpContext( httpContext:String ):void
        {
            _httpContext = httpContext;
        }

        public function get ssl():Boolean
        {
            return _ssl;
        }

        public function set ssl( ssl:Boolean ):void
        {
            _ssl = ssl;
        }
    }
}
