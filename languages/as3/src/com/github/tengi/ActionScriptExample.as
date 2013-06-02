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
package com.github.tengi
{
    import com.github.tengi.client.ClientConnection;
    import com.github.tengi.client.ConnectionConfiguration;
    import com.github.tengi.client.ConnectionManager;
    import com.github.tengi.client.Message;
    import com.github.tengi.client.MessageListener;
    import com.github.tengi.client.ConnectionListenerAdapter;
    import com.github.tengi.client.Streamable;
    import com.github.tengi.client.buffer.MemoryBuffer;

    import flash.errors.IOError;

    public class ActionScriptExample extends ConnectionListenerAdapter implements MessageListener
    {
        public function ActionScriptExample()
        {
            var connectionManager:ConnectionManager = new ConnectionManager( 20 );

            var configuration:ConnectionConfiguration = new ConnectionConfiguration( new ExampleProtocol() );
            configuration.host = "localhost";
            configuration.port = 80;

            connectionManager.createConnection( configuration, this );
        }

        override public function onConnect( connection:ClientConnection ):void
        {
            var example:Example = new Example();
            example.value = "From AS3";
            // Response is received through messageReceived(...)
            connection.sendObject( example );

            var linkedExample:Example = new Example();
            linkedExample.value = "This one has it's own callback";
            // Response is received in given callback
            connection.sendLinkedObject( linkedExample, function ( request:Message, response:Message,
                                                                   connection:ClientConnection ):void
            {
                var sendExample:Example = request.body as Example;
                var receivedExample:Example = response.body as Example;
                trace( "send=" + sendExample.value + ", received=" + receivedExample.value );
            } );
        }

        public function messageReceived( message:Message, connection:ClientConnection ):void
        {
            var body:Streamable = message.body;
            if ( body is Example )
            {
                trace( "Received Example::value = " + (body as Example).value );
            }
        }

        public function rawDataReceived( memoryBuffer:MemoryBuffer, metadata:Streamable,
                                         connection:ClientConnection ):void
        {
            throw new IOError( "rawdata are not supported by this protocol :-(" );
        }
    }
}

import com.github.tengi.client.Entity;
import com.github.tengi.client.Protocol;
import com.github.tengi.client.Streamable;
import com.github.tengi.client.buffer.MemoryBuffer;

import flash.errors.IOError;

internal class ExampleProtocol implements Protocol
{

    public function instantiate( classId:int ):Streamable
    {
        switch ( classId )
        {
            case 1:
                return new Example();
        }
        throw new IOError( "Unknown classId" );
    }

    public function getClassIdentifier( streamable:Streamable ):int
    {
        if ( streamable is Example )
        {
            return 1;
        }
        throw new IOError( "Unknown streamable type" );
    }

    public function isEntity( classId:int ):Boolean
    {
        return false;
    }

    public function readEntity( memoryBuffer:MemoryBuffer, classId:int ):Entity
    {
        return null;
    }

    public function get mimeType():String
    {
        return "binary/tengi";
    }
}

internal class Example implements Streamable
{

    private var _value:String;

    public function get value():String
    {
        return _value;
    }

    public function set value( value:String ):void
    {
        _value = value;
    }

    public function readStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
    {
        memoryBuffer.writeString( _value );
    }

    public function writeStream( memoryBuffer:MemoryBuffer, protocol:Protocol ):void
    {
        _value = memoryBuffer.readString();
    }

    public function toString():String
    {
        return _value;
    }
}
