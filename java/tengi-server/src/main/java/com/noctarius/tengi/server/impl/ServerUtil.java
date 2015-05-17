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
package com.noctarius.tengi.server.impl;

import com.noctarius.tengi.Identifier;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class ServerUtil {

    public static final AttributeKey<Identifier> CONNECTION_ID = AttributeKey.newInstance("CONNECTION_ID");

    public static <T> T connectionAttribute(ChannelHandlerContext ctx, AttributeKey<T> key) {
        return connectionAttribute(ctx, key, false);
    }

    public static <T> T connectionAttribute(ChannelHandlerContext ctx, AttributeKey<T> key, boolean remove) {
        Attribute<T> attribute = ctx.attr(key);
        if (attribute != null) {
            return remove ? attribute.getAndRemove() : attribute.get();
        }
        return null;
    }

    public static <T> void connectionAttribute(ChannelHandlerContext ctx, AttributeKey<T> key, T value) {
        Attribute<T> attribute = ctx.attr(key);
        attribute.set(value);
    }

    private ServerUtil() {
    }

}
