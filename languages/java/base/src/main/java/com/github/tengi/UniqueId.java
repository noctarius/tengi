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

import java.util.UUID;

public class UniqueId
{

    private final byte[] uniqueIdData = new byte[16];

    private final long mostSigBits;

    private final long leastSigBits;

    private UniqueId( UUID uuid )
    {
        leastSigBits = uuid.getLeastSignificantBits();
        mostSigBits = uuid.getMostSignificantBits();
        copyLongToBytes( leastSigBits, true );
        copyLongToBytes( mostSigBits, false );
    }

    private UniqueId( long leastSigBits, long mostSigBits )
    {
        this.leastSigBits = leastSigBits;
        this.mostSigBits = mostSigBits;
        copyLongToBytes( leastSigBits, true );
        copyLongToBytes( mostSigBits, false );
    }

    private UniqueId( byte[] data )
    {
        if ( data.length != 16 )
        {
            throw new IllegalArgumentException( "data length must be 16 bytes" );
        }
        System.arraycopy( data, 0, uniqueIdData, 0, 16 );
        leastSigBits = copyBytesToLong( data, true );
        mostSigBits = copyBytesToLong( data, false );
    }

    public long getMostSigBits()
    {
        return mostSigBits;
    }

    public long getLeastSigBits()
    {
        return leastSigBits;
    }

    public byte[] getUniqueIdData()
    {
        return uniqueIdData;
    }

    private long copyBytesToLong( byte[] data, boolean leastSig )
    {
        int index = leastSig ? 0 : 8;
        return ( ( ( ( data[index++] & 0xFFL ) << 56 ) | ( ( data[index++] & 0xFFL ) << 48 )
            | ( ( data[index++] & 0xFFL ) << 40 ) | ( ( data[index++] & 0xFFL ) << 32 )
            | ( ( data[index++] & 0xFFL ) << 24 ) | ( ( data[index++] & 0xFFL ) << 16 )
            | ( ( data[index++] & 0xFFL ) << 8 ) | ( ( data[index++] & 0xFFL ) << 0 ) ) );
    }

    private void copyLongToBytes( long value, boolean leastSig )
    {
        int index = leastSig ? 0 : 8;
        uniqueIdData[index++] = (byte) ( value >> 56 );
        uniqueIdData[index++] = (byte) ( value >> 48 );
        uniqueIdData[index++] = (byte) ( value >> 40 );
        uniqueIdData[index++] = (byte) ( value >> 32 );
        uniqueIdData[index++] = (byte) ( value >> 24 );
        uniqueIdData[index++] = (byte) ( value >> 16 );
        uniqueIdData[index++] = (byte) ( value >> 8 );
        uniqueIdData[index++] = (byte) ( value >> 0 );
    }

    public static UniqueId uniqueId( UUID uuid )
    {
        return new UniqueId( uuid );
    }

    public static UniqueId uniqueId( long leastSigBits, long mostSigBits )
    {
        return new UniqueId( leastSigBits, mostSigBits );
    }

    public static UniqueId uniqueId( byte[] data )
    {
        return new UniqueId( data );
    }
}
