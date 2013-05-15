package com.github.tengi.transport.protocol;

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

    private void initializeTengiPipeline( ChannelPipeline channelPipeline )
    {
        channelPipeline.addLast( new LengthFieldBasedFrameDecoder( Integer.MAX_VALUE, 0, 4, -4, 4 ) );
        channelPipeline.addLast( new LengthFieldPrepender( 4, true ) );
    }

    protected abstract void initializeTransportPipeline( ChannelPipeline channelPipeline );

}
