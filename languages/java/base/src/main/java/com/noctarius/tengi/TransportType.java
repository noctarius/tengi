package com.noctarius.tengi;

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

public enum TransportType
{
    BioTcpSocket( "tengi-tcp" ),
    NioTcpSocket( "tengi-tcp" ),
    BioUdpSocket( "tengi-udp" ),
    NioUdpSocket( "tengi-udp" ),
    HttpPolling( "tengi-http-polling", true ),
    HttpLongPolling( "tengi-http-longpolling", true ),
    WebSocket( "tengi-websocket" ),
    SPDY( "tengi-spdy" ),
    SCTP( "tengi-sctp" );

    private final boolean polling;

    private final String transport;

    private TransportType( String transport )
    {
        this( transport, false );
    }

    private TransportType( String transport, boolean polling )
    {
        this.polling = polling;
        this.transport = transport;
    }

    public String getTransport()
    {
        return transport;
    }

    public boolean isPolling()
    {
        return polling;
    }

    public static TransportType byTransport( String transport )
    {
        for ( TransportType transportType : values() )
        {
            if ( transportType.getTransport().equals( transport ) )
            {
                return transportType;
            }
        }
        return null;
    }

}
