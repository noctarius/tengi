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

public class Message
{

    public static final byte MESSAGE_TYPE_DEFAULT = 0;

    public static final byte MESSAGE_TYPE_LONG_POLLING = 1;

    public static final byte MESSAGE_TYPE_COMPOSITE = 2;

    protected SerializationFactory serializationFactory;

    protected Connection connection;

    private Streamable body;

    private UniqueId messageId;

    private byte type = MESSAGE_TYPE_DEFAULT;

    public Message( SerializationFactory serializationFactory, Connection connection, byte type )
    {
        this.serializationFactory = serializationFactory;
        this.connection = connection;
        this.type = type;
    }

    public Message( SerializationFactory serializationFactory, Connection connection, Streamable body,
                    UniqueId messageId, byte type )
    {
        this.serializationFactory = serializationFactory;
        this.connection = connection;
        this.messageId = messageId != null ? messageId : UniqueId.randomUniqueId();
        this.body = body;
        this.type = type;
    }

    public String toString()
    {
        return "Message [messageId=" + messageId + ", body=" + ( body != null ? body.toString() : "null" ) + "]";
    }

    public void readStream( MemoryBuffer memoryBuffer )
    {
        this.messageId = new UniqueId();
        this.messageId.readStream( memoryBuffer, serializationFactory );
        if ( memoryBuffer.readByte() == 1 )
        {
            int classId = memoryBuffer.readShort();
            body = serializationFactory.instantiate( classId );
            body.readStream( memoryBuffer, serializationFactory );
        }
    }

    public void writeStream( MemoryBuffer memoryBuffer )
    {
        messageId.writeStream( memoryBuffer, serializationFactory );
        if ( body == null )
        {
            memoryBuffer.writeByte( (byte) 0 );
        }
        else
        {
            memoryBuffer.writeByte( (byte) 1 );
            short classId = serializationFactory.getClassIdentifier( body );
            memoryBuffer.writeShort( classId );
            body.writeStream( memoryBuffer, serializationFactory );
        }
    }

    public Connection getConnection()
    {
        return connection;
    }

    public Streamable getBody()
    {
        return body;
    }

    public UniqueId getMessageId()
    {
        return messageId;
    }

    public int getType()
    {
        return type;
    }

    public static Message read( MemoryBuffer memoryBuffer, SerializationFactory serializationFactory,
                                Connection connection )
    {
        byte type = memoryBuffer.readByte();

        Message message;
        if ( type == MESSAGE_TYPE_COMPOSITE )
        {
            message = new CompositeMessage( serializationFactory, connection );
        }
        else
        {
            message = new Message( serializationFactory, connection, type );
        }
        message.readStream( memoryBuffer );
        return message;
    }

    public static void write( MemoryBuffer memoryBuffer, Message message )
    {
        memoryBuffer.writeByte( message.type );
        message.writeStream( memoryBuffer );
    }

}
