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
    public class ConnectionConstants
    {
        function ConnectionConstants()
        {
        }

        public static const DATA_TYPE_MESSAGE:int = 1;

        public static const DATA_TYPE_RAW:int = 2;

        public static const HTTP_HEADER_NAME_CONNECTIONID:String = "XX-tengi-connection-id";

        public static const HTTP_HEADER_NAME_SUPPORTED_TRANSPORT_TYPES:String = "XX-tengi-transport-types";

        public static const HTTP_HEADER_NAME_TRANSPORT_TYPE:String = "XX-tengi-transport-type";

    }
}
