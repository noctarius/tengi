/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.server.impl.transport;

import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.server.spi.transport.ServerChannel;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

public class NettyServerChannel
        implements ServerChannel<Channel> {

    private final Channel channel;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final int port;
    private final TransportLayer transportLayer;

    public NettyServerChannel(Channel channel, EventLoopGroup bossGroup, EventLoopGroup workerGroup, int port,
                       TransportLayer transportLayer) {

        this.channel = channel;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.port = port;
        this.transportLayer = transportLayer;
    }

    @Override
    public Channel socket() {
        return channel;
    }

    @Override
    public void start() {
    }

    @Override
    public void shutdown()
            throws Exception {

        channel.close().sync().get();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public int port() {
        return port;
    }

    @Override
    public TransportLayer transportLayer() {
        return transportLayer;
    }

    public EventLoopGroup bossGroup() {
        return bossGroup;
    }

    public EventLoopGroup workerGroup() {
        return workerGroup;
    }
}
