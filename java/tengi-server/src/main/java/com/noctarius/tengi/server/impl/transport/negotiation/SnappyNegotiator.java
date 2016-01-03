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
import com.noctarius.tengi.server.spi.negotiation.NegotiationContext;
import com.noctarius.tengi.server.spi.negotiation.NegotiationResult;
import com.noctarius.tengi.server.spi.negotiation.Negotiator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameEncoder;

public class SnappyNegotiator
        implements Negotiator {

    @Override
    public NegotiationResult handleProtocol(NegotiationContext context, ChannelHandlerContext ctx, ByteBuf buffer) {
        Boolean detectSnappy = context.attribute(ServerConstants.NEGOTIATOR_ATTRIBUTE_DETECT_SNAPPY);
        if (detectSnappy == null || !detectSnappy) {
            return NegotiationResult.Continue;
        }

        if (buffer.readableBytes() < 5) {
            // Not enough data to negotiate the protocol's magic header
            return NegotiationResult.InsufficientBuffer;
        }

        // Read the magic header
        int magic0 = buffer.getUnsignedByte(buffer.readerIndex());
        int magic1 = buffer.getUnsignedByte(buffer.readerIndex() + 1);
        int magic2 = buffer.getUnsignedByte(buffer.readerIndex() + 2);
        int magic3 = buffer.getUnsignedByte(buffer.readerIndex() + 3);

        if (magic0 == 0xFF && magic1 == 's' && magic2 == 'N' && magic3 == 'a') {
            context.injectChannelHandler(ctx, "snappyinflater", new SnappyFrameEncoder());
            context.injectChannelHandler(ctx, "snappydeflater", new SnappyFrameDecoder());

            context.attribute(ServerConstants.NEGOTIATOR_ATTRIBUTE_DETECT_SNAPPY, false);
            return NegotiationResult.Restart;
        }

        return NegotiationResult.Continue;
    }
}
