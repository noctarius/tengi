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
package com.noctarius.tengi.client.lang.math
{
    /**
     * Copyright (c) 2011 , Yang Bo All rights reserved.
     *
     * Author: Yang Bo (pop.atry@gmail.com)
     *
     * Use, modification and distribution are subject to the "New BSD License"
     * as listed at <url: http://www.opensource.org/licenses/bsd-license.php >.
     *
     * Original classname: Int64
     *
     * This class is taken from protobuf-as3 implemenation on
     * https://code.google.com/p/protoc-gen-as3
     *
     * Thanks guys for the great work
     */
    public class Long extends Binary64
    {
        public final function set high( value:int ):void
        {
            internalHigh = value
        }

        public final function get high():int
        {
            return internalHigh
        }

        public function Long( low:uint = 0, high:int = 0 )
        {
            super( low, high )
        }

        /**
         * Convert from <code>Number</code>.
         */
        public static function fromNumber( n:Number ):Long
        {
            return new Long( n, Math.floor( n / 4294967296.0 ) )
        }

        /**
         * Convert to <code>Number</code>.
         */
        public final function toNumber():Number
        {
            return high * 4294967296.0 + low
        }

        public final function toString( radix:uint = 10 ):String
        {
            if ( radix < 2 || radix > 36 )
            {
                throw new ArgumentError
            }
            switch ( high )
            {
                case 0:
                {
                    return low.toString( radix )
                }

                case -1:
                {
                    if ( (low & 0x80000000) == 0 )
                    {
                        return (int( low | 0x80000000 ) - 2147483648.0).toString( radix )
                    }
                    else
                    {
                        return int( low ).toString( radix )
                    }
                }

                default:
                {
                    break;
                }
            }
            if ( low == 0 && high == 0 )
            {
                return "0"
            }
            const digitChars:Array = [];
            const copyOfThis:UInt64 = new UInt64( low, high );
            if ( high < 0 )
            {
                copyOfThis.bitwiseNot()
                copyOfThis.add( 1 )
            }
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
            if ( high < 0 )
            {
                return '-' + copyOfThis.low.toString( radix ) + String.fromCharCode.apply( String,
                                                                                           digitChars.reverse() )
            }
            else
            {
                return copyOfThis.low.toString( radix ) + String.fromCharCode.apply( String, digitChars.reverse() )
            }
        }

        public static function parseInt64( str:String, radix:uint = 0 ):Long
        {
            const negative:Boolean = str.search( /^\-/ ) == 0
            var i:uint = negative ? 1 : 0
            if ( radix == 0 )
            {
                if ( str.search( /^\-?0x/ ) == 0 )
                {
                    radix = 16
                    i += 2
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
            const result:Long = new Long
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
            if ( negative )
            {
                result.bitwiseNot()
                result.add( 1 )
            }
            return result
        }
    }
}
