package com.github.tengi.transport.protocol;

import com.github.tengi.ConnectionManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class TengiServerChannelInitializer
    extends ChannelInitializer<Channel>
{

    public ConnectionManager connectionManager;

    public TengiServerChannelInitializer( ConnectionManager connectionManager )
    {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void initChannel( Channel channel )
        throws Exception
    {
        channel.pipeline().addLast( new TengiUnificatedProtocolNegotiator( true, true, connectionManager ) );
    }

}
