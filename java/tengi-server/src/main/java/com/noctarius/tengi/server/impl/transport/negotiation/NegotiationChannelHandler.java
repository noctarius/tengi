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
package com.noctarius.tengi.server.impl.transport.negotiation;

import com.noctarius.tengi.core.connection.TransportLayer;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.spi.NegotiationContext;
import com.noctarius.tengi.server.spi.NegotiationResult;
import com.noctarius.tengi.server.spi.Negotiator;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NegotiationChannelHandler
        extends ChannelInboundHandlerAdapter {

    private final int port;
    private final Serializer serializer;
    private final TransportLayer transportLayer;
    private final ConnectionManager connectionManager;

    private final NegotiationContext context = new Context();

    public NegotiationChannelHandler(int port, TransportLayer transportLayer, ConnectionManager connectionManager,
                                     Serializer serializer) {

        this.port = port;
        this.serializer = serializer;
        this.transportLayer = transportLayer;
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object)
            throws Exception {

        if (!(object instanceof ByteBuf)) {
            return;
        }

        ByteBuf buffer = (ByteBuf) object;
        NegotiationResult result = tryNegotiation(ctx, buffer);

        switch (result) {
            case Failed:
                ctx.channel().close().sync();
                return;

            case Successful:
                ctx.pipeline().remove(this);
                ctx.fireChannelRead(object);

            case Restart:
            case InsufficientBuffer:
                return;
        }
    }

    private NegotiationResult tryNegotiation(ChannelHandlerContext ctx, ByteBuf buffer) {
        Negotiator[] negotiators = connectionManager.findNegotiators(transportLayer, port);
        if (negotiators.length > 0) {
            for (Negotiator negotiator : negotiators) {
                NegotiationResult result = negotiator.handleProtocol(context, ctx, buffer);
                switch (result) {
                    case InsufficientBuffer:
                    case Successful:
                    case Restart:
                        return result;

                    case Continue:
                        continue;

                    default:
                        throw new IllegalStateException("Illegal state reached");
                }
            }

        }
        return NegotiationResult.Failed;
    }

    private class Context
            implements NegotiationContext {

        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        @Override
        public ConnectionManager getConnectionManager() {
            return connectionManager;
        }

        @Override
        public Serializer getSerializer() {
            return serializer;
        }

        @Override
        public int getPort() {
            return port;
        }

        @Override
        public <T> T attribute(String name) {
            return (T) attributes.get(name);
        }

        @Override
        public <T> void attribute(String name, T value) {
            attributes.put(name, value);
        }

        @Override
        public void injectChannelHandler(ChannelHandlerContext ctx, String name, ChannelHandler handler) {
            ChannelPipeline pipeline = ctx.pipeline();

            ChannelHandlerContext lastContext = pipeline.lastContext();
            ChannelHandler lastHandler = lastContext.handler();
            String lastName = lastContext.name();

            //pipeline.addLast(name, handler);
            //pipeline.remove(lastHandler);
            //pipeline.addLast(lastName, lastHandler);
            pipeline.addBefore(lastName, name, handler);
        }
    }

}
