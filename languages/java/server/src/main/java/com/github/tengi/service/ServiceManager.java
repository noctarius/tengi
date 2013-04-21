package com.github.tengi.service;
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

import com.github.tengi.Connection;
import com.github.tengi.ConnectionConstants;
import com.github.tengi.Message;
import com.github.tengi.SerializationFactory;
import com.github.tengi.Streamable;
import com.github.tengi.buffer.ByteBufMemoryBuffer;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.transport.polling.PollingConnection;
import com.github.tengi.transport.polling.PollingMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ServiceManager
{

    private final SerializationFactory serializationFactory;

    private final Service service;

    public ServiceManager( Service service, SerializationFactory serializationFactory )
    {
        this.service = service;
        this.serializationFactory = serializationFactory;
    }

    private void call( MemoryBuffer memoryBuffer, Connection connection )
    {
        byte frameType = memoryBuffer.readByte();
        if ( frameType == ConnectionConstants.DATA_TYPE_MESSAGE )
        {
            Message message = Message.read( memoryBuffer, serializationFactory, connection );
            if ( message instanceof PollingMessage )
            {
                longPolling( (PollingMessage) message, connection );
            }
            else
            {
                call( message, connection );
            }
        }
        else if ( frameType == ConnectionConstants.DATA_TYPE_RAW )
        {
            Streamable metadata = null;
            if ( memoryBuffer.readByte() == 1 )
            {
                short classId = memoryBuffer.readShort();
                metadata = serializationFactory.instantiate( classId );
            }

            int length = memoryBuffer.readInt();
            ByteBuf buffer = Unpooled.buffer( length );
            MemoryBuffer rawBuffer = new ByteBufMemoryBuffer( buffer );
            memoryBuffer.readBuffer( rawBuffer, 0, length );

            call( rawBuffer, metadata, connection );
        }
    }

    private void call( Message request, Connection connection )
    {
        service.call( request, connection );
    }

    private void call( MemoryBuffer request, Streamable metadata, Connection connection )
    {
        service.call( request, metadata, connection );
    }

    private void longPolling( PollingMessage request, Connection connection )
    {
        if ( connection.getTransportType().isPolling() )
        {
            PollingConnection pollingConnection = (PollingConnection) connection;
            Message response = pollingConnection.pollResponses( request.getLastUpdateId() );
            connection.sendMessage( response, null );
        }
    }

}
