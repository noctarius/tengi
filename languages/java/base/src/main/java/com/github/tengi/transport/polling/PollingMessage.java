package com.github.tengi.transport.polling;
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
import com.github.tengi.SerializationFactory;
import com.github.tengi.UniqueId;
import com.github.tengi.buffer.MemoryBuffer;

public class PollingMessage
    extends Message
{

    private int lastUpdateId;

    public PollingMessage( SerializationFactory serializationFactory, Connection connection )
    {
        super( serializationFactory, connection, Message.MESSAGE_TYPE_LONG_POLLING );
    }

    public PollingMessage( SerializationFactory serializationFactory, Connection connection, int lastUpdateId,
                           UniqueId messageId )
    {
        super( serializationFactory, connection, null, messageId, Message.MESSAGE_TYPE_LONG_POLLING );
        this.lastUpdateId = lastUpdateId;
    }

    @Override
    public void readStream( MemoryBuffer memoryBuffer )
    {
        super.readStream( memoryBuffer );
        memoryBuffer.writeInt( lastUpdateId );
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer )
    {
        super.writeStream( memoryBuffer );
        lastUpdateId = memoryBuffer.readInt();
    }

    public int getLastUpdateId()
    {
        return lastUpdateId;
    }

}
