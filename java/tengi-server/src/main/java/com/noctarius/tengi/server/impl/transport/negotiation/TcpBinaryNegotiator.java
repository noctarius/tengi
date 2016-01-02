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
import com.noctarius.tengi.server.ServerTransport;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.http2.Http2ConnectionProcessor;
import com.noctarius.tengi.server.impl.transport.tcp.TcpConnectionProcessor;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameEncoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslHandler;

public class TcpBinaryNegotiator
        extends ChannelInboundHandlerAdapter {

    private final int port;
    private final boolean detectSsl;
    private final boolean detectCompression;
    private final ConnectionManager connectionManager;
    private final Serializer serializer;

    public TcpBinaryNegotiator(int port, boolean detectSsl, boolean detectCompression, //
                               ConnectionManager connectionManager, Serializer serializer) {

        this.port = port;
        this.detectSsl = detectSsl;
        this.detectCompression = detectCompression;
        this.connectionManager = connectionManager;
        this.serializer = serializer;
    }

    public void channelRead(ChannelHandlerContext ctx, Object object)
            throws Exception {

        if (!(object instanceof ByteBuf)) {
            return;
        }

        ByteBuf in = (ByteBuf) object;

        if (in.readableBytes() < 5) {
            // Not enough data to negotiate the protocol's magic header
            return;
        }

        if (isSsl(in)) {
            // Seems like an SSL connection, so activate it
            enableSsl(ctx);

        } else {
            // Read the magic header
            int magic0 = in.getUnsignedByte(in.readerIndex());
            int magic1 = in.getUnsignedByte(in.readerIndex() + 1);
            int magic2 = in.getUnsignedByte(in.readerIndex() + 2);
            int magic3 = in.getUnsignedByte(in.readerIndex() + 3);

            boolean acceptedProtocol = switchProtocol(in, magic0, magic1, magic2, magic3, ctx);
            if (!acceptedProtocol) {
                // Illegal protocol header or unknown protocol request
                in.clear();
                ctx.close();
                return;
            }
        }
        ctx.fireChannelRead(object);
    }

    private boolean switchProtocol(ByteBuf in, int magic0, int magic1, int magic2, int magic3, ChannelHandlerContext ctx) {
        switch (magic0) {
            case 0x1F:
                if (magic1 == 0x8B && detectCompression) {
                    enableGZIP(ctx);
                }
                break;

            case 0xFF:
                if (magic1 == 's' && magic2 == 'N' && magic3 == 'a' && detectCompression) {
                    enableSnappy(ctx);
                }
                break;

            case 'G':
                if (magic1 == 'E' && magic2 == 'T') {
                    switchToHttpNegotiation(ctx);
                }
                break;

            case 'P':
                if (magic1 == 'O' && magic2 == 'S' && magic3 == 'T') {
                    switchToHttpNegotiation(ctx);
                }
                if (magic1 == 'R' && magic2 == 'I' && magic3 == ' ') {
                    switchToNativeHttp2(ctx);
                }
                break;

            case 'C':
                if (magic1 == 'O' && magic2 == 'N' && magic3 == 'N') {
                    switchToHttpNegotiation(ctx);
                }
                break;

            case 'T':
                if (magic1 == 'e' && magic2 == 'N' && magic3 == 'g') {
                    if (!connectionManager.acceptTransport(ServerTransport.TCP_TRANSPORT, port)) {
                        ctx.close();
                        throw new ConnectionFailedException("Transport not enabled");
                    }
                    in.skipBytes(DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER.length);
                    switchToNativeTcp(ctx);
                }
                break;

            default:
                return false;
        }

        return true;
    }

    private void switchToHttpNegotiation(ChannelHandlerContext ctx) {
        if (!connectionManager.acceptTransport(ServerTransport.HTTP_TRANSPORT, port) //
                && !connectionManager.acceptTransport(ServerTransport.HTTP2_TRANSPORT, port) //
                && !connectionManager.acceptTransport(ServerTransport.WEBSOCKET_TRANSPORT, port)) {

            ctx.close();
            throw new ConnectionFailedException("Transport not enabled");
        }

        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("httpNegotiator", new Http2Negotiator(port, 1024 * 1024, connectionManager, serializer));
        pipeline.remove(this);
    }

    private void switchToNativeHttp2(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("http2-connection-processor", new Http2ConnectionProcessor(connectionManager, serializer));
        //pipeline.addLast("connection-processor", new ConnectionProcessor(connectionManager, serializer));
        pipeline.remove(this);
    }

    private void switchToNativeTcp(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("tcp-connection-processor", new TcpConnectionProcessor(connectionManager, serializer));
        pipeline.remove(this);
    }

    private boolean isSsl(ByteBuf in) {
        if (detectSsl) {
            return SslHandler.isEncrypted(in);
        }
        return false;
    }

    private void enableSsl(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("ssl", connectionManager.getSslContext().newHandler(ctx.alloc()));
        pipeline.addLast("negotiationSSL", new TcpBinaryNegotiator(port, false, true, connectionManager, serializer));
        pipeline.remove(this);
    }

    private void enableGZIP(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("gzipinflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast("gzipdeflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        pipeline.addLast("negotiationGZIP", new TcpBinaryNegotiator(port, true, false, connectionManager, serializer));
        pipeline.remove(this);
    }

    private void enableSnappy(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("snappyinflater", new SnappyFrameEncoder());
        pipeline.addLast("snappydeflater", new SnappyFrameDecoder());
        pipeline.addLast("negotiationSnappy", new TcpBinaryNegotiator(port, true, false, connectionManager, serializer));
        pipeline.remove(this);
    }

}
