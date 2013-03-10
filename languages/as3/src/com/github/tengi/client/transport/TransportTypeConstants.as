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
package com.github.tengi.client.transport
{
    import com.github.tengi.client.TransportType;

    public final class TransportTypeConstants
    {

        public static const TCP : TransportType = new TransportTypeEnum( "TCP", 0 );

        public static const UDP : TransportType = new TransportTypeEnum( "UDP", 1 );

        public static const HTTP_POLLING : TransportType = new TransportTypeEnum( "HTTP_POLLING", 2 );

        public static const HTTP_LONG_POLLING : TransportType = new TransportTypeEnum( "HTTP_LONG_POLLING", 3 );

        public static const WEBSOCKET : TransportType = new TransportTypeEnum( "WEBSOCKET", 4 );

        public static const SPDY : TransportType = new TransportTypeEnum( "SPDY", 5 );

    }
}
