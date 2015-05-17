/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.tengi.client.impl.transport;

import com.noctarius.tengi.client.impl.Connector;
import com.noctarius.tengi.spi.connection.Connection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractClientConnector<M>
        implements Connector<M> {

    protected ChannelFutureListener connectionListener(CompletableFuture<Connection> future, CatchingConsumer success) {
        return (channelFuture) -> {
            if (channelFuture.isSuccess()) {
                success.consume(channelFuture.channel());
            } else {
                future.complete(null);
            }
        };
    }

    protected static interface CatchingConsumer {
        void consume(Channel channel)
                throws Exception;
    }

}
