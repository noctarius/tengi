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
package com.github.tengi.client.transport.events
{
    import com.github.tengi.client.Streamable;
    import com.github.tengi.client.buffer.MemoryBuffer;

    import flash.events.Event;

    public class RawDataReceivedEvent extends Event
    {

        private var _rawBuffer:MemoryBuffer;
        private var _metadata:Streamable;

        public function RawDataReceivedEvent( type:String, rawBuffer:MemoryBuffer, metadata:Streamable,
                                              bubbles:Boolean = false, cancelable:Boolean = false )
        {
            super( type, bubbles, cancelable );
            this._rawBuffer = rawBuffer;
            this._metadata = metadata;
        }

        public function get rawBuffer():MemoryBuffer
        {
            return _rawBuffer;
        }

        public function get metadata():Streamable
        {
            return _metadata;
        }

        public override function clone():Event
        {
            return new RawDataReceivedEvent( type, _rawBuffer, _metadata, bubbles, cancelable );
        }

    }
}
