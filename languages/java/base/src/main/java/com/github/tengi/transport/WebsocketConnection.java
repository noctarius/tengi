package com.github.tengi.transport;
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

import com.github.tengi.CompletionFuture;
import com.github.tengi.Connection;
import com.github.tengi.Message;
import com.github.tengi.MessageListener;
import com.github.tengi.TransportType;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.ReadableMemoryBuffer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.util.UUID;

public class WebsocketConnection implements Connection
{

    private final UniqueId uniqueId = UniqueId.uniqueId( UUID.randomUUID() );

    private final Channel channel;

    private ChannelInboundMessageHandlerAdapter messageListener = null;

    WebsocketConnection( Channel channel )
    {
        this.channel = channel;
    }

    @Override
    public UniqueId getUniqueId()
    {
        return uniqueId;
    }

    @Override
    public TransportType getConnectionType()
    {
        return TransportType.WebSocket;
    }

    @Override
    public <T extends Message> void sendMessage( T message, CompletionFuture<T> completionFuture )
    {
        ChannelFuture channelFuture = channel.write( message );
        channelFuture.addListener( new CompletionFutureAdapter<T>( completionFuture, message, this ) );
    }

    @Override
    public <T> void sendRawData( ReadableMemoryBuffer memoryBuffer, final T metadata, final CompletionFuture<T> completionFuture )
    {
        ChannelFuture channelFuture = channel.write( memoryBuffer );
        channelFuture.addListener( new CompletionFutureAdapter<T>( completionFuture, metadata, this ) );
    }

    @Override
    public void setMessageListener( MessageListener messageListener )
    {
        this.messageListener = new MessageListenerAdapter( messageListener, this );
    }

    @Override
    public void clearMessageListener()
    {
        messageListener = null;
    }

}
