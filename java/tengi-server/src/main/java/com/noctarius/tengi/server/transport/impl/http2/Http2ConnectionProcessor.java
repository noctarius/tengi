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
package com.noctarius.tengi.server.transport.impl.http2;

import com.noctarius.tengi.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2Headers;

public class Http2ConnectionProcessor
        extends Http2ConnectionHandler {

    private final Serializer serializer;

    public Http2ConnectionProcessor(Serializer serializer) {
        super(new DefaultHttp2Connection(true), new InternalFrameAdapter());
        ((InternalFrameAdapter) decoder().listener()).encoder(encoder());
        this.serializer = serializer;
    }

    private static class InternalFrameAdapter
            extends Http2FrameAdapter {

        private Http2ConnectionEncoder encoder;

        private void encoder(Http2ConnectionEncoder encoder) {
            this.encoder = encoder;
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endStream)
                throws Http2Exception {

            super.onHeadersRead(ctx, streamId, headers, padding, endStream);
        }

        @Override
        public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream)
                throws Http2Exception {

            return super.onDataRead(ctx, streamId, data, padding, endOfStream);
        }
    }

}
