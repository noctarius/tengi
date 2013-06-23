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
    import flash.errors.IllegalOperationError;
    import flash.utils.Dictionary;
    import flash.utils.getQualifiedClassName;

    public class Enum
    {

        private static const ENUM_VALUE_COLLECTION:Dictionary = new Dictionary();

        private var _name:String;
        private var _ordinal:int;

        public function Enum( name:String, enforcer:Object )
        {
            this._name = name;
            addToEnumType( this, enforcer );
        }

        protected function byName( name:String ):Enum
        {
            var className:String = getQualifiedClassName( this );
            var enumTypeConstants:Array = ENUM_VALUE_COLLECTION[className] as Array;
            if ( enumTypeConstants != null )
            {
                for each ( var value:Enum in enumTypeConstants )
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
            return ENUM_VALUE_COLLECTION[className] as Array;
        }

        private static function addToEnumType( enum:Enum, enforcer:Object ):void
        {
            var className:String = getQualifiedClassName( enum );
            var value:Object = ENUM_VALUE_COLLECTION[className];
            if ( value == null )
            {
                value = new EnumTypeDefinition( enforcer )
                ENUM_VALUE_COLLECTION[className] = value;
            }
            else if ( value is Array )
            {
                throw new IllegalOperationError( "Enum type " + className + " is already completed." );
            }
            var enumTypeDefinition:EnumTypeDefinition = EnumTypeDefinition( value );
            if ( enumTypeDefinition._enforcer != enforcer )
            {
                throw new IllegalOperationError( "Illegal enforcer used for enum type " + className + "." );
            }
            enum._ordinal = enumTypeDefinition._currentOrdinal++;
            enumTypeDefinition.constants.push( enum );
        }

        protected static function finalizeEnumType( enumType:Class, enforcer:Object ):void
        {
            var className:String = getQualifiedClassName( enumType );
            var value:Object = ENUM_VALUE_COLLECTION[className];
            if ( value is Array )
            {
                throw new IllegalOperationError( "Enum type " + className + " is already completed." );
            }
            else if ( value == null )
            {
                throw new IllegalOperationError( "Cannot finalize empty enum type " + className + "." );
            }
            var enumTypeDefinition:EnumTypeDefinition = EnumTypeDefinition( value );
            if ( enumTypeDefinition._enforcer != enforcer )
            {
                throw new IllegalOperationError( "Illegal enforcer used for enum type " + className + "." );
            }
            ENUM_VALUE_COLLECTION[className] = [].concat( enumTypeDefinition.constants );
        }

    }
}

import com.noctarius.tengi.client.lang.Enum;

internal class EnumTypeDefinition
{
    internal var _enforcer:Object;
    internal var _currentOrdinal:int = 0;
    internal const constants:Vector.<Enum> = new Vector.<Enum>();

    public function EnumTypeDefinition( enforcer:Object )
    {
        this._enforcer = enforcer;
    }
}