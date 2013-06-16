package com.github.tengi.transport.protocol.handler;

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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import com.github.tengi.Connection;
import com.github.tengi.ConnectionManager;
import com.github.tengi.UniqueId;
import com.github.tengi.utils.ChannelMessageHandler;

public class TengiUdpRequestHandler
    extends ChannelMessageHandler<DatagramPacket>
{

    private final ConnectionManager connectionManager;

    public TengiUdpRequestHandler( ConnectionManager connectionManager )
    {
        this.connectionManager = connectionManager;
    }

    @Override
    public void messageReceived( ChannelHandlerContext ctx, DatagramPacket msg )
        throws Exception
    {
        ByteBuf buffer = msg.content();
        byte[] idData = new byte[16];
        buffer.readBytes( idData );

        UniqueId connectionId = UniqueId.fromByteArray( idData );
        Connection connection = connectionManager.getConnectionByConnectionId( connectionId );
    }

}
