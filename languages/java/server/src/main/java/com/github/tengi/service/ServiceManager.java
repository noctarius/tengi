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
import com.github.tengi.Message;
import com.github.tengi.MessageListener;
import com.github.tengi.Streamable;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.transport.polling.PollingConnection;
import com.github.tengi.transport.polling.PollingMessage;

public class ServiceManager
    implements MessageListener
{

    private final Service<Message> service;

    public ServiceManager( Service<Message> service )
    {
        this.service = service;
    }

    @Override
    public void messageReceived( Message message, Connection connection )
    {
        if ( message instanceof PollingMessage )
        {
            if ( connection.getTransportType().isPolling() )
            {
                throw new IllegalArgumentException( "Given connection is not a PollingConnection "
                    + "but LongPolling request arrived" );
            }

            longPolling( (PollingMessage) message, connection );
        }
        else
        {
            service.call( message, connection );
        }
    }

    @Override
    public void rawDataReceived( MemoryBuffer request, Streamable metadata, Connection connection )
    {
        service.call( request, metadata, connection );
    }

    private void longPolling( PollingMessage request, Connection connection )
    {
        PollingConnection pollingConnection = (PollingConnection) connection;
        pollingConnection.sendPollResponses( pollingConnection.getPollingChannel(), request.getLastUpdateId() );
    }

}
