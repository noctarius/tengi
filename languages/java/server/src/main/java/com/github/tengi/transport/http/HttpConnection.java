package com.github.tengi.transport.http;
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
import com.github.tengi.ConnectionConstants;
import com.github.tengi.Message;
import com.github.tengi.MessageListener;
import com.github.tengi.transport.polling.PollingConnection;
import com.github.tengi.SerializationFactory;
import com.github.tengi.Streamable;
import com.github.tengi.TransportType;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.ByteBufMemoryBuffer;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.service.messagecache.MessageQueue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class HttpConnection
    implements PollingConnection
{

    private final SerializationFactory serializationFactory;

    private final MessageQueue messageQueue;

    private MessageListener messageListener = null;

    public HttpConnection( SerializationFactory serializationFactory )
    {
        this.serializationFactory = serializationFactory;
        this.messageQueue = new MessageQueue( this, serializationFactory );
    }

    @Override
    public TransportType getTransportType()
    {
        return TransportType.HttpLongPolling;
    }

    @Override
    public <T extends Message> void sendMessage( T message, CompletionFuture<T> completionFuture )
    {
        try
        {
            ByteBuf buffer = Unpooled.directBuffer( 100 );
            MemoryBuffer memoryBuffer = new ByteBufMemoryBuffer( buffer );
            memoryBuffer.writeByte( ConnectionConstants.DATA_TYPE_MESSAGE );
            Message.write( memoryBuffer, message );

            messageQueue.push( memoryBuffer );

            if ( completionFuture != null )
            {
                completionFuture.onSuccess( message, this );
            }
        }
        catch ( Exception e )
        {
            if ( completionFuture != null )
            {
                completionFuture.onFailure( e, message, this );
            }
        }
    }

    @Override
    public <T extends Streamable> void sendRawData( MemoryBuffer memoryBuffer, T metadata,
                                                    CompletionFuture<T> completionFuture )
    {
        try
        {
            ByteBuf buffer = Unpooled.directBuffer( (int) ( memoryBuffer.writerIndex() + 20 ) );
            MemoryBuffer rawBuffer = new ByteBufMemoryBuffer( buffer );
            rawBuffer.writeByte( ConnectionConstants.DATA_TYPE_RAW );
            if ( metadata == null )
            {
                rawBuffer.writeByte( (byte) 0 );
            }
            else
            {
                rawBuffer.writeByte( (byte) 1 );
                rawBuffer.writeShort( serializationFactory.getClassIdentifier( metadata ) );
                metadata.writeStream( rawBuffer );
            }

            rawBuffer.writeBuffer( memoryBuffer, 0, memoryBuffer.writerIndex() );

            messageQueue.push( memoryBuffer );

            if ( completionFuture != null )
            {
                completionFuture.onSuccess( null, this );
            }
        }
        catch ( Exception e )
        {
            if ( completionFuture != null )
            {
                completionFuture.onFailure( e, null, this );
            }
        }
    }

    @Override
    public Message pollResponses( int lastUpdateId )
    {
        return messageQueue.snapshot( lastUpdateId );
    }

    @Override
    public void setMessageListener( MessageListener messageListener )
    {
        this.messageListener = messageListener;
    }

    @Override
    public void clearMessageListener()
    {
        messageListener = null;
    }

    @Override
    public Message prepareMessage( Streamable body )
    {
        return new Message( serializationFactory, this, body, UniqueId.randomUniqueId(), Message.MESSAGE_TYPE_DEFAULT );
    }

    @Override
    public void close()
    {
        messageQueue.clear();
    }
}
