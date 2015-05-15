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
package com.noctarius.tengi.transport.client.impl.http;

import com.noctarius.tengi.TransportLayer;
import com.noctarius.tengi.client.Connector;
import com.noctarius.tengi.client.MessagePublisher;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.TransportConstants;
import com.noctarius.tengi.serialization.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HttpConnector
        implements Connector {

    private final AtomicBoolean destroyed = new AtomicBoolean(false);

    private final AtomicReference<Channel> upstream = new AtomicReference<>(null);
    private final AtomicReference<Channel> downstream = new AtomicReference<>(null);

    private final Bootstrap bootstrap;

    private final Serializer serializer;
    private final MessagePublisher messagePublisher;
    private final EventLoopGroup clientGroup;

    public HttpConnector(Serializer serializer, MessagePublisher messagePublisher, EventLoopGroup clientGroup) {
        this.serializer = serializer;
        this.messagePublisher = messagePublisher;
        this.clientGroup = clientGroup;
        this.bootstrap = createBootstrap();
    }

    @Override
    public CompletableFuture<Connection> connect(InetAddress address, int port) {
        return null;
    }

    @Override
    public Channel getUpstreamChannel() {
        return upstream.get();
    }

    @Override
    public Channel getDownstreamChannel() {
        return downstream.get();
    }

    @Override
    public Collection<Channel> getCommunicationChannels() {
        return Collections.unmodifiableCollection(Arrays.asList(getUpstreamChannel(), getDownstreamChannel()));
    }

    @Override
    public void destroy()
            throws Exception {

        if (destroyed.compareAndSet(false, true)) {
            Channel channel = getUpstreamChannel();
            if (channel != null) {
                channel.close().sync();
            }
            channel = getDownstreamChannel();
            if (channel != null) {
                channel.close().sync();
            }
        }
    }

    @Override
    public String getName() {
        return TransportConstants.TRANSPORT_NAME_HTTP;
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public int getDefaultPort() {
        return TransportConstants.DEFAULT_PORT_TCP;
    }

    @Override
    public TransportLayer getTransportLayer() {
        return TransportLayer.TCP;
    }

    private Bootstrap createBootstrap() {
        return null;
    }

}
