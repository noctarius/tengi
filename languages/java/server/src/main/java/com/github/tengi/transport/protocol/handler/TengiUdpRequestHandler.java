package com.github.tengi.transport.protocol.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;

import com.github.tengi.Connection;
import com.github.tengi.ConnectionManager;
import com.github.tengi.UniqueId;

public class TengiUdpRequestHandler
    extends ChannelInboundMessageHandlerAdapter<DatagramPacket>
{

    private final ConnectionManager connectionManager;

    public TengiUdpRequestHandler( ConnectionManager connectionManager )
    {
        this.connectionManager = connectionManager;
    }

    @Override
    public void messageReceived( ChannelHandlerContext ctx, DatagramPacket msg )
        throws Exception
    {
        ByteBuf buffer = msg.content();
        byte[] idData = new byte[16];
        buffer.readBytes( idData );

        UniqueId connectionId = UniqueId.fromByteArray( idData );
        Connection connection = connectionManager.getConnectionByConnectionId( connectionId );
    }

}
