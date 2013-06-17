package com.noctarius.tengi.transport.protocol;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public abstract class TengiChannelInitializer
    extends ChannelInitializer<Channel>
{

    @Override
    protected final void initChannel( Channel channel )
        throws Exception
    {
        initializeTransportPipeline( channel.pipeline() );
        initializeTengiPipeline( channel.pipeline() );
    }

    protected void initializeTengiPipeline( ChannelPipeline channelPipeline )
    {
        channelPipeline.addLast( new LengthFieldBasedFrameDecoder( Integer.MAX_VALUE, 0, 4, -4, 4 ) );
        channelPipeline.addLast( new LengthFieldPrepender( 4, true ) );
    }

    protected abstract void initializeTransportPipeline( ChannelPipeline channelPipeline );

}
