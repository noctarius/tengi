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
     * This class is taken from protobuf-as3 implemenation on
     * https://code.google.com/p/protoc-gen-as3
     *
     * Thanks guys for the great work
     */
    public class Binary64
    {
        /**
         * @private
         */
        internal static const CHAR_CODE_0:uint = '0'.charCodeAt();
        /**
         * @private
         */
        internal static const CHAR_CODE_9:uint = '9'.charCodeAt();
        /**
         * @private
         */
        internal static const CHAR_CODE_A:uint = 'a'.charCodeAt();
        /**
         * @private
         */
        internal static const CHAR_CODE_Z:uint = 'z'.charCodeAt();
        public var low:uint;
        /**
         * @private
         */
        internal var internalHigh:uint;

        public function Binary64( low:uint = 0, high:uint = 0 )
        {
            this.low = low
            this.internalHigh = high
        }

        /**
         * Division by n.
         * @return The remainder after division.
         * @private
         */
        internal final function div( n:uint ):uint
        {
            const modHigh:uint = internalHigh % n
            const mod:uint = (low % n + modHigh * 6) % n
            internalHigh /= n
            const newLow:Number = (modHigh * 4294967296.0 + low) / n
            internalHigh += uint( newLow / 4294967296.0 )
            low = newLow
            return mod
        }

        /**
         * @private
         */
        internal final function mul( n:uint ):void
        {
            const newLow:Number = Number( low ) * n
            internalHigh *= n
            internalHigh += uint( newLow / 4294967296.0 )
            low *= n
        }

        /**
         * @private
         */
        internal final function add( n:uint ):void
        {
            const newLow:Number = Number( low ) + n
            internalHigh += uint( newLow / 4294967296.0 )
            low = newLow
        }

        /**
         * @private
         */
        internal final function bitwiseNot():void
        {
            low = ~low
            internalHigh = ~internalHigh
        }
    }
}
