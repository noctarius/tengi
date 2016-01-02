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

import com.noctarius.tengi.server.impl.ServerConstants;
import com.noctarius.tengi.server.spi.NegotiationContext;
import com.noctarius.tengi.server.spi.NegotiationResult;
import com.noctarius.tengi.server.spi.Negotiator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

public class GZipNegotiator
        implements Negotiator {

    @Override
    public NegotiationResult handleProtocol(NegotiationContext context, ChannelHandlerContext ctx, ByteBuf buffer) {
        Boolean detectGzip = context.attribute(ServerConstants.NEGOTIATOR_ATTRIBUTE_DETECT_GZIP);
        if (detectGzip == null || !detectGzip) {
            return NegotiationResult.Continue;
        }

        if (buffer.readableBytes() < 2) {
            // Not enough data to negotiate the protocol's magic header
            return NegotiationResult.InsufficientBuffer;
        }

        int magic0 = buffer.getUnsignedByte(buffer.readerIndex());
        int magic1 = buffer.getUnsignedByte(buffer.readerIndex() + 1);

        if (magic0 == 0x1F && magic1 == 0x8B) {
            context.injectChannelHandler(ctx, "gzipinflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
            context.injectChannelHandler(ctx, "gzipdeflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

            context.attribute(ServerConstants.NEGOTIATOR_ATTRIBUTE_DETECT_GZIP, false);
            return NegotiationResult.Restart;
        }
        return NegotiationResult.Continue;
    }
}
