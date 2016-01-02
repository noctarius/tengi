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
package com.noctarius.tengi.server.impl.transport.http2;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.connection.HandshakeHandler;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.server.ServerTransport;
import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableDecoder;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2Headers;

import static com.noctarius.tengi.server.impl.ServerUtil.CONNECTION_ID;
import static com.noctarius.tengi.server.impl.ServerUtil.connectionAttribute;

public class Http2ConnectionProcessor
        extends Http2ConnectionHandler {

    private final ConnectionManager connectionManager;

    public Http2ConnectionProcessor(ConnectionManager connectionManager, Serializer serializer) {
        super(new DefaultHttp2Connection(true), new InternalFrameAdapter());
        InternalFrameAdapter adapter = ((InternalFrameAdapter) decoder().listener());
        adapter.encoder(encoder());
        adapter.serializer(serializer);
        adapter.connectionManager(connectionManager);
        adapter.transport(ServerTransport.HTTP2_TRANSPORT);
        this.connectionManager = connectionManager;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        ctx.channel().close();
        Identifier connectionId = connectionAttribute(ctx, CONNECTION_ID);
        connectionManager.exceptionally(connectionId, cause);

        super.exceptionCaught(ctx, cause);
    }

    private static class InternalFrameAdapter
            extends Http2FrameAdapter {

        private Http2ConnectionEncoder encoder;
        private ConnectionManager connectionManager;
        private Serializer serializer;
        private Transport transport;

        private void encoder(Http2ConnectionEncoder encoder) {
            this.encoder = encoder;
        }

        private void connectionManager(ConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        private void serializer(Serializer serializer) {
            this.serializer = serializer;
        }

        private void transport(Transport transport) {
            this.transport = transport;
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream)
                throws Http2Exception {

            super.onHeadersRead(ctx, streamId, headers, padding, endStream);
        }

        @Override
        public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
                throws Http2Exception {

            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(data);
            try (AutoClosableDecoder decoder = serializer.retrieveDecoder(memoryBuffer)) {
                if (decoder == null) {
                    ctx.close();
                    return 0;
                }

                boolean loggedIn = decoder.readBoolean();

                if (!loggedIn) {
                    handleHandshakeRequest(ctx, decoder, streamId, encoder);
                    return 0;
                }

                Identifier connectionId = decoder.readObject();
                connectionAttribute(ctx, CONNECTION_ID, connectionId);
                Message message = decoder.readObject();
                connectionManager.publishMessage(ctx.channel(), connectionId, message);
            } catch (Exception e) {
                throw new Http2Exception(Http2Error.INTERNAL_ERROR, "Internal Server Error", e);
            }
            return data.writerIndex();
        }

        private void handleHandshakeRequest(ChannelHandlerContext ctx, AutoClosableDecoder decoder, int streamId,
                                            Http2ConnectionEncoder encoder)
                throws Exception {

            Object request = decoder.readObject();
            if (!(request instanceof Handshake)) {
                ctx.close();
                return;
            }

            Identifier connectionId = Identifier.randomIdentifier();
            HandshakeHandler handshakeHandler = connectionManager.getHandshakeHandler();
            Handshake handshakeResponse = handshakeHandler.handleHandshake(connectionId, (Handshake) request);
            if (handshakeResponse == null) {
                ctx.close();
                return;
            }
            if (handshakeResponse == request) {
                ctx.close();
                throw new IllegalStateException("Handshake could not be accepted, illegal verification");
            }

            connectionAttribute(ctx, CONNECTION_ID, connectionId);
            ConnectionContext connectionContext = createConnectionContext(ctx, connectionId, streamId, encoder);
            Connection connection = connectionManager.assignConnection(connectionId, connectionContext, transport);
            connectionContext.writeSocket(encoder, connection, createHandshakeResponse(ctx, handshakeResponse));
        }

        private MemoryBuffer createHandshakeResponse(ChannelHandlerContext ctx, Handshake handshakeResponse)
                throws Exception {

            ByteBuf buffer = ctx.alloc().buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
                encoder.writeObject("response", handshakeResponse);
            }
            return memoryBuffer;
        }

        private ConnectionContext<Http2ConnectionEncoder> createConnectionContext(ChannelHandlerContext ctx,
                                                                                  Identifier connectionId, int streamId,
                                                                                  Http2ConnectionEncoder encoder) {

            return new Http2ConnectionContext(connectionId, serializer, transport, encoder, streamId, ctx);
        }
    }

}
