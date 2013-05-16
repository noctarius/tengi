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

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import com.github.tengi.CompletionFuture;
import com.github.tengi.Message;
import com.github.tengi.SerializationFactory;
import com.github.tengi.Streamable;
import com.github.tengi.TransportType;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.buffer.MemoryBufferPool;

public class WebsocketConnection
    extends AbstractChannelConnection
{

    WebsocketConnection( UniqueId connectionId, Channel channel, MemoryBufferPool memoryBufferPool,
                         SerializationFactory serializationFactory )
    {
        super( connectionId, channel, memoryBufferPool, serializationFactory );
    }

    @Override
    public TransportType getTransportType()
    {
        return TransportType.WebSocket;
    }

    @Override
    public <T extends Message> void sendMessage( T message, CompletionFuture<T> completionFuture )
    {
        ByteBuf buffer = getByteBuf( 100 );
        MemoryBuffer memoryBuffer = memoryBufferPool.pop( buffer );
        try
        {
            prepareMessageBuffer( message, memoryBuffer );
            getUnderlyingChannel().write( buffer );

            if ( completionFuture != null )
            {
                completionFuture.onSuccess( message, this );
            }
        }
        catch ( Exception e )
        {
            memoryBufferPool.push( memoryBuffer );
            if ( completionFuture != null )
            {
                completionFuture.onFailure( e, message, this );
            }
        }

        ChannelFuture channelFuture = getUnderlyingChannel().write( message );
        channelFuture.addListener( new CompletionFutureAdapter<T>( completionFuture, message, this ) );
    }

    @Override
    public <T extends Streamable> void sendRawData( MemoryBuffer rawBuffer, final T metadata,
                                                    final CompletionFuture<T> completionFuture )
    {
        ByteBuf buffer = getByteBuf( rawBuffer.writerIndex() + 20 );
        MemoryBuffer memoryBuffer = memoryBufferPool.pop( buffer );
        try
        {
            prepareMessageBuffer( rawBuffer, metadata, memoryBuffer );
            getUnderlyingChannel().write( buffer );

            if ( completionFuture != null )
            {
                completionFuture.onSuccess( null, this );
            }
        }
        catch ( Exception e )
        {
            memoryBufferPool.push( rawBuffer );
            if ( completionFuture != null )
            {
                completionFuture.onFailure( e, null, this );
            }
        }
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub

    }

}
