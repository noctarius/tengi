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
    import com.github.tengi.client.transport.http.HttpConnection;

    public class ConnectionManager
    {
        private const connections:Vector.<Connection> = new Vector.<Connection>();

        private var serializationFactory:SerializationFactory;

        public function ConnectionManager( serializationFactory:SerializationFactory )
        {
            this.serializationFactory = serializationFactory;
        }

        public function createHttpConnection( host:String, port:int, contextPath:String, ssl:Boolean ):Connection
        {
            var connection:Connection = new HttpConnection( host, port, contextPath, ssl, serializationFactory );
            connections.push( connection );
            return connection;
        }

        public function createTcpConnection( host:String, port:int ):Connection
        {
            return null;
        }

        public function createConnection( host:String, port:int, contextPath:String, ssl:Boolean ):Connection
        {
            return createHttpConnection( host, port, contextPath, ssl );
        }

    }
}
