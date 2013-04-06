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
package com.github.tengi.client.buffer
{
    import com.github.tengi.client.lang.IllegalArgumentError;
    import com.github.tengi.client.lang.util.Enum;

    public final class ByteOrder extends Enum
    {

        public static const BIG_ENDIAN : ByteOrder = new ByteOrder( "BIG_ENDIAN", 0, _ );

        public static const LITTLE_ENDIAN : ByteOrder = new ByteOrder( "LITTLE_ENDIAN", 1, _ );

        function ByteOrder( name : String, ordinal : int, restrictor : * )
        {
            super( name, ordinal, restrictor );
        }


        internal static function get constants():Array
        {
            return [ BIG_ENDIAN, LITTLE_ENDIAN ];
        }

        public static function valueOf(name : String):ByteOrder
        {
            try
            {
                return ByteOrder( BIG_ENDIAN.constantOf( name ) );
            }
            catch (e : Error)
            {
                throw new IllegalArgumentError( e.message );
            }

            return null;
        }

        override protected function getConstants():Array
        {
            return constants;
        }

    }
}
