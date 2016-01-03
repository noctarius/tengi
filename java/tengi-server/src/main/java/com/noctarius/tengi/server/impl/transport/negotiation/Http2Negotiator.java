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

import com.noctarius.tengi.core.exception.ConnectionFailedException;
import com.noctarius.tengi.server.ServerTransports;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.http2.Http2ConnectionProcessor;
import com.noctarius.tengi.spi.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.util.List;

import static io.netty.handler.codec.http2.Http2CodecUtil.TLS_UPGRADE_PROTOCOL_NAME;

public class Http2Negotiator
        extends ByteToMessageDecoder {

    private final int port;
    private final int maxHttpContentLength;
    private final ConnectionManager connectionManager;
    private final Serializer serializer;

    public Http2Negotiator(int port, int maxHttpContentLength, ConnectionManager connectionManager, Serializer serializer) {
        this.port = port;
        this.maxHttpContentLength = maxHttpContentLength;
        this.connectionManager = connectionManager;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {

        if (initPipeline(ctx)) {
            // When we reached here we can remove this handler as its now clear
            // what protocol we want to use
            // from this point on. This will also take care of forward all
            // messages.
            ctx.pipeline().remove(this);
        }
    }

    private boolean initPipeline(ChannelHandlerContext ctx) {
        // Get the SslHandler from the ChannelPipeline so we can obtain the
        // SslEngine from it.
        SslHandler handler = ctx.pipeline().get(SslHandler.class);
        if (handler == null) {
            // SSL is necessary to negotiate HTTP2, therefore it can only be HTTP
            switchToHttp(ctx);
            return true;
        }

        SelectedProtocol protocol = getProtocol(handler.engine());
        switch (protocol) {
            case UNKNOWN:
                // Not done with choosing the protocol, so just return here for now,
                //return false;
                switchToHttp(ctx);
                break;
            case HTTP_2:
                if (!connectionManager.acceptTransport(ServerTransports.HTTP2_TRANSPORT, port)) {
                    ctx.close();
                    throw new ConnectionFailedException("Transport not enabled");
                }
                switchToHttp2(ctx);
                break;
            case HTTP_1_0:
            case HTTP_1_1:
                switchToHttp(ctx);
                break;
            default:
                throw new IllegalStateException("Unknown SelectedProtocol");
        }
        return true;
    }

    private void switchToHttp2(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("http2-connection-processor", new Http2ConnectionProcessor(connectionManager, serializer));
        pipeline.remove(this);
    }

    private void switchToHttp(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("httpCodec", new HttpServerCodec());
        pipeline.addLast("httpChunkAggregator", new HttpObjectAggregator(maxHttpContentLength));
        pipeline.addLast("websocketNegotiator", new WebsocketNegotiator(port, connectionManager, serializer));
    }

    private SelectedProtocol getProtocol(SSLEngine engine) {
        String[] protocol = engine.getSession().getProtocol().split(":");
        if (protocol != null && protocol.length > 1) {
            SelectedProtocol selectedProtocol = SelectedProtocol.protocol(protocol[1]);
            System.err.println("Selected Protocol is " + selectedProtocol);
            return selectedProtocol;
        }
        return SelectedProtocol.UNKNOWN;
    }

    private enum SelectedProtocol {
        /**
         * Must be updated to match the HTTP/2 draft number.
         */
        HTTP_2(TLS_UPGRADE_PROTOCOL_NAME),
        HTTP_1_1("http/1.1"),
        HTTP_1_0("http/1.0"),
        UNKNOWN("Unknown");

        private final String name;

        SelectedProtocol(String defaultName) {
            name = defaultName;
        }

        public String protocolName() {
            return name;
        }

        /**
         * Get an instance of this enum based on the protocol name returned by the NPN server provider
         *
         * @param name the protocol name
         * @return the SelectedProtocol instance
         */
        public static SelectedProtocol protocol(String name) {
            for (SelectedProtocol protocol : SelectedProtocol.values()) {
                if (protocol.protocolName().equals(name)) {
                    return protocol;
                }
            }
            return UNKNOWN;
        }
    }

}
