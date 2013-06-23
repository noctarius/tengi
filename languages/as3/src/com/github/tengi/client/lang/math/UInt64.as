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
package com.github.tengi.client.lang.math
{
    /**
     * Copyright (c) 2011 , Yang Bo All rights reserved.
     *
     * Author: Yang Bo (pop.atry@gmail.com)
     *
     * Use, modification and distribution are subject to the "New BSD License"
     * as listed at <url: http://www.opensource.org/licenses/bsd-license.php >.
     *
     * This class is taken from protobuf-as3 implemenation on
     * https://code.google.com/p/protoc-gen-as3
     *
     * Thanks guys for the great work
     */
    public final class UInt64 extends Binary64
    {
        public final function set high( value:uint ):void
        {
            internalHigh = value
        }

        public final function get high():uint
        {
            return internalHigh
        }

        public function UInt64( low:uint = 0, high:uint = 0 )
        {
            super( low, high )
        }

        /**
         * Convert from <code>Number</code>.
         */
        public static function fromNumber( n:Number ):UInt64
        {
            return new UInt64( n, Math.floor( n / 4294967296.0 ) )
        }

        /**
         * Convert to <code>Number</code>.
         */
        public final function toNumber():Number
        {
            return (high * 4294967296) + low
        }

        public final function toString( radix:uint = 10 ):String
        {
            if ( radix < 2 || radix > 36 )
            {
                throw new ArgumentError
            }
            if ( high == 0 )
            {
                return low.toString( radix )
            }
            const digitChars:Array = [];
            const copyOfThis:UInt64 = new UInt64( low, high );
            do {
                const digit:uint = copyOfThis.div( radix );
                if ( digit < 10 )
                {
                    digitChars.push( digit + CHAR_CODE_0 );
                }
                else
                {
                    digitChars.push( digit - 10 + CHAR_CODE_A );
                }
            }
            while ( copyOfThis.high != 0 )
            return copyOfThis.low.toString( radix ) + String.fromCharCode.apply( String, digitChars.reverse() )
        }

        public static function parseUInt64( str:String, radix:uint = 0 ):UInt64
        {
            var i:uint = 0
            if ( radix == 0 )
            {
                if ( str.search( /^0x/ ) == 0 )
                {
                    radix = 16
                    i = 2
                }
                else
                {
                    radix = 10
                }
            }
            if ( radix < 2 || radix > 36 )
            {
                throw new ArgumentError
            }
            str = str.toLowerCase()
            const result:UInt64 = new UInt64
            for ( ; i < str.length; i++ )
            {
                var digit:uint = str.charCodeAt( i )
                if ( digit >= CHAR_CODE_0 && digit <= CHAR_CODE_9 )
                {
                    digit -= CHAR_CODE_0
                }
                else if ( digit >= CHAR_CODE_A && digit <= CHAR_CODE_Z )
                {
                    digit -= CHAR_CODE_A
                    digit += 10
                }
                else
                {
                    throw new ArgumentError
                }
                if ( digit >= radix )
                {
                    throw new ArgumentError
                }
                result.mul( radix )
                result.add( digit )
            }
            return result
        }
    }
}
