package com.noctarius.tengi.transport.websocket;

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

import com.noctarius.tengi.CompletionFuture;
import com.noctarius.tengi.Connection;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

class CompletionFutureAdapter<T>
    implements ChannelFutureListener
{

    private final CompletionFuture<T> completionFuture;

    private final Connection connection;

    private final T message;

    CompletionFutureAdapter( CompletionFuture<T> completionFuture, T message, Connection connection )
    {
        this.completionFuture = completionFuture;
        this.connection = connection;
        this.message = message;
    }

    @Override
    public void operationComplete( ChannelFuture future )
        throws Exception
    {
        completionFuture.onCompletion( message, connection, future.cause() );
    }

}
