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

import com.noctarius.tengi.buffer.MemoryBuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CompositeMessage
    extends Message
    implements Iterable<Message>
{

    private List<Message> messages;

    public CompositeMessage( Connection connection )
    {
        super( connection, Message.MESSAGE_TYPE_COMPOSITE );
    }

    public CompositeMessage( Connection connection, List<Message> messages, UniqueId messageId )
    {
        super( connection, null, messageId, Message.MESSAGE_TYPE_COMPOSITE );
        this.messages = new ArrayList<>( messages );
    }

    public short getMessageCount()
    {
        return (short) ( messages == null ? 0 : messages.size() );
    }

    @SuppressWarnings( "unchecked" )
    public <T extends Message> T retrieveMessage( short index )
    {
        if ( index >= messages.size() )
        {
            throw new IndexOutOfBoundsException( "max allowed index is " + ( messages.size() - 1 ) );
        }
        return (T) ( messages == null ? null : messages.get( index ) );
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer, Protocol protocol )
    {
        super.readStream( memoryBuffer, protocol );

        short length = memoryBuffer.readShort();
        messages = new ArrayList<>( length );
        for ( int i = 0; i < length; i++ )
        {
            messages.add( Message.read( memoryBuffer, protocol, connection ) );
        }
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer, Protocol protocol )
    {
        super.writeStream( memoryBuffer, protocol );
        memoryBuffer.writeShort( (short) messages.size() );
        for ( int i = 0; i < messages.size(); i++ )
        {
            messages.get( i ).writeStream( memoryBuffer, protocol );
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
