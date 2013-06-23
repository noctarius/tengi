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
package com.noctarius.tengi.client.lang
{
    import flash.utils.Dictionary;
    import flash.utils.getQualifiedClassName;

    public class Enum
    {

        private static const ENUM_VALUE_COLLECTION:Dictionary = new Dictionary();

        private var _name:String;
        private var _ordinal:int;

        public function Enum( name:String )
        {
            this._name = name;
            addToEnumCollection( this );
        }

        protected function byName( name:String ):Enum
        {
            var className:String = getQualifiedClassName( this );
            var enumTypeDefinition:EnumTypeDefinition = ENUM_VALUE_COLLECTION[className] as EnumTypeDefinition;
            if ( enumTypeDefinition != null )
            {
                for each ( var value:Enum in enumTypeDefinition.constants )
                {
                    if ( value._name == name )
                    {
                        return value;
                    }
                }
            }
            return null;
        }

        public function get name():String
        {
            return _name;
        }

        public function get ordinal():int
        {
            return _ordinal;
        }

        protected function getConstants():Array
        {
            var className:String = getQualifiedClassName( this );
            var enumTypeDefinition:EnumTypeDefinition = ENUM_VALUE_COLLECTION[className] as EnumTypeDefinition;
            if ( enumTypeDefinition != null )
            {
                return [].concat( enumTypeDefinition.constants );
            }
            return null;
        }

        private static function addToEnumCollection( enum:Enum ):void
        {
            var className:String = getQualifiedClassName( enum );
            var enumTypeDefinition:EnumTypeDefinition = ENUM_VALUE_COLLECTION[className] as EnumTypeDefinition;
            if ( enumTypeDefinition == null )
            {
                enumTypeDefinition = new EnumTypeDefinition();
                ENUM_VALUE_COLLECTION[className] = enumTypeDefinition;
            }
            enum._ordinal = enumTypeDefinition._currentOrdinal++;
            enumTypeDefinition.constants.push( enum );
        }

    }
}

import com.noctarius.tengi.client.lang.Enum;

class EnumTypeDefinition
{
    var _currentOrdinal = 0;
    const constants:Vector.<Enum> = new Vector.<Enum>();
}