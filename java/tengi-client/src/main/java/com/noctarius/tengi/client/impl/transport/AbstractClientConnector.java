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

import com.noctarius.tengi.client.impl.ConnectCallback;
import com.noctarius.tengi.client.impl.Connector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

public abstract class AbstractClientConnector<M>
        implements Connector<M> {

    protected ChannelFutureListener connectionListener(ConnectCallback connectCallback, CatchingConsumer success,
                                                       ChannelFutureListener closeHandler) {

        return (channelFuture) -> {
            if (channelFuture.isSuccess()) {
                Channel channel = channelFuture.channel();
                channel.closeFuture().addListener(closeHandler);
                success.consume(channel);
            } else {
                Throwable cause = channelFuture.cause();
                if (cause != null) {
                    connectCallback.on(cause);
                } else {
                    connectCallback.on(null, null);
                }
            }
        };
    }

    protected static interface CatchingConsumer {
        void consume(Channel channel)
                throws Exception;
    }

}
