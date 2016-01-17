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

import com.noctarius.tengi.server.impl.ConnectionManager;
import com.noctarius.tengi.server.impl.transport.NettyNegotiator;
import com.noctarius.tengi.server.spi.negotiation.NegotiationContext;
import com.noctarius.tengi.server.spi.negotiation.NegotiationResult;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

public class SSLNegotiator
        implements NettyNegotiator {

    @Override
    public NegotiationResult handleProtocol(NegotiationContext context, ChannelHandlerContext ctx, ByteBuf buffer) {
        if (buffer.readableBytes() < 5) {
            // Not enough data to negotiate the protocol's magic header
            return NegotiationResult.InsufficientBuffer;
        }

        if (SslHandler.isEncrypted(buffer)) {
            ConnectionManager connectionManager = context.getConnectionManager();

            context.injectChannelHandler(ctx, connectionManager.getSslContext().newHandler(ctx.alloc()));

            return NegotiationResult.Restart;
        }
        return NegotiationResult.Continue;
    }
}
