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

import com.fasterxml.uuid.impl.UUIDUtil;
import com.github.tengi.buffer.MemoryBuffer;
import com.github.tengi.utils.UUIDBuilder;

import java.util.UUID;

public class UniqueId
    implements Streamable
{

    private static final char[] CHARS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };

    private final byte[] data = new byte[16];

    @Override
    public void readStream( MemoryBuffer memoryBuffer )
        throws Exception
    {
        memoryBuffer.readBytes( data, 0, 16 );
    }

    @Override
    public void writeStream( MemoryBuffer memoryBuffer )
        throws Exception
    {
        memoryBuffer.writeBytes( data, 0, 16 );
    }

    @Override
    public String toString()
    {
        char[] chars = new char[36];
        int index = 0;
        for ( int i = 0; i < 16; i++ )
        {
            if ( i == 4 || i == 6 || i == 8 || i == 10 )
            {
                chars[index++] = '-';
            }
            chars[index++] = CHARS[( data[i] & 0xF0 ) >>> 4];
            chars[index++] = CHARS[( data[i] & 0x0F )];
        }
        return new String( chars );
    }

    public static UniqueId randomUniqueId()
    {
        UUID uuid = UUIDBuilder.generateRandomUUID();
        UniqueId uniqueId = new UniqueId();
        UUIDUtil.toByteArray( uuid, uniqueId.data );
        return uniqueId;
    }

    public static UniqueId readFromStream( MemoryBuffer memoryBuffer )
        throws Exception
    {
        UniqueId uniqueId = new UniqueId();
        uniqueId.readStream( memoryBuffer );
        return uniqueId;
    }

}
