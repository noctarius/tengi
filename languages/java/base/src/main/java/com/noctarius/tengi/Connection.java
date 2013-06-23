package com.noctarius.tengi;

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

import com.noctarius.tengi.buffer.MemoryBuffer;

public interface Connection
{

    UniqueId getConnectionId();

    TransportType getTransportType();

    <T extends Message> void sendObject( Streamable body );

    <T extends Message> void sendObject( Streamable body, CompletionFuture<T> completionFuture );

    <T extends Message> void sendMessage( T message );

    <T extends Message> void sendMessage( T message, CompletionFuture<T> completionFuture );

    <T extends Streamable> void sendRawData( MemoryBuffer rawBuffer, T metadata );

    <T extends Streamable> void sendRawData( MemoryBuffer rawBuffer, T metadata, CompletionFuture<T> completionFuture );

    void setMessageListener( MessageFrameListener messageFrameListener, RawFrameListener rawFrameListener );
    
    void setMessageListener( MessageListener messageListener );

    void clearMessageListener();

    Message prepareMessage( Streamable body );

    void close();

}
