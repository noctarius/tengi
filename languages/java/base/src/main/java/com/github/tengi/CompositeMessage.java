package com.github.tengi;
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

import com.github.tengi.buffer.MemoryBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompositeMessage
    extends Message
    implements Iterable<Message>
{

    private List<Message> messages;

    public CompositeMessage( SerializationFactory serializationFactory, Connection connection )
    {
        super( serializationFactory, connection, Message.MESSAGE_TYPE_COMPOSITE );
    }

    public CompositeMessage( SerializationFactory serializationFactory, Connection connection, List<Message> messages,
                             UniqueId messageId )
    {
        super( serializationFactory, connection, null, messageId, Message.MESSAGE_TYPE_COMPOSITE );
        this.messages = new ArrayList<>( messages );
    }

    public short getMessageCount()
    {
        return (short) ( messages == null ? 0 : messages.size() );
    }

    public <T extends Message> T retrieveMessage( short index )
    {
        if ( index >= messages.size() )
        {
            throw new IndexOutOfBoundsException( "max allowed index is " + ( messages.size() - 1 ) );
        }
        return (T) ( messages == null ? null : messages.get( index ) );
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer )
    {
        super.readStream( memoryBuffer );

        short length = memoryBuffer.readShort();
        messages = new ArrayList<>( length );
        for ( int i = 0; i < length; i++ )
        {
            messages.add( Message.read( memoryBuffer, serializationFactory, connection ) );
        }
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer )
    {
        super.writeStream( memoryBuffer );
        memoryBuffer.writeShort( (short) messages.size() );
        for ( int i = 0; i < messages.size(); i++ )
        {
            messages.get( i ).writeStream( memoryBuffer );
        }
    }

    public Iterator<Message> iterator()
    {
        return new Iterator<Message>()
        {
            private int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < messages.size();
            }

            @Override
            public Message next()
            {
                return messages.get( index++ );
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException( "remove not supported on CompositeMessage" );
            }
        };
    }

}
