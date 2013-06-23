package com.noctarius.tengi.service.messagecache;

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

import com.noctarius.tengi.Connection;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Protocol;
import com.noctarius.tengi.UniqueId;
import com.noctarius.tengi.buffer.MemoryBuffer;

import java.util.ArrayList;
import java.util.List;

class PreserializedCompositeMessage
    extends Message
{

    private List<MemoryBuffer> messages;

    public PreserializedCompositeMessage( Connection connection, List<MemoryBuffer> messages, UniqueId messageId )
    {
        super( connection, null, messageId, Message.MESSAGE_TYPE_COMPOSITE );
        this.messages = new ArrayList<>( messages );
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer, Protocol serializationFactory )
    {
        throw new UnsupportedOperationException(
                                                 "This type of CompositeMessage is only used for sending preserialized data" );
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer, Protocol serializationFactory )
    {
        super.writeStream( memoryBuffer, serializationFactory );
        memoryBuffer.writeShort( (short) messages.size() );
        for ( int i = 0; i < messages.size(); i++ )
        {
            MemoryBuffer buffer = messages.get( i );
            memoryBuffer.writeBuffer( buffer.duplicate() );
            memoryBuffer.release();
        }
    }

}
