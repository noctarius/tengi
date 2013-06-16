package com.github.tengi.rdp;

import java.util.Map;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOption;
import io.netty.channel.RecvByteBufAllocator;

public class RdpChannelConfiguration
    implements ChannelConfig
{

    private int sequenceMax;

    private int receiveBufferMax;

    public int getSequenceMax()
    {
        return sequenceMax;
    }

    public void setSequenceMax( int sequenceMax )
    {
        this.sequenceMax = sequenceMax;
    }

    public int getReceiveBufferMax()
    {
        return receiveBufferMax;
    }

    public void setReceiveBufferMax( int receiveBufferMax )
    {
        this.receiveBufferMax = receiveBufferMax;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean setOptions( Map<ChannelOption<?>, ?> options )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T getOption( ChannelOption<T> option )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> boolean setOption( ChannelOption<T> option, T value )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getConnectTimeoutMillis()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis( int connectTimeoutMillis )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getWriteSpinCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ChannelConfig setWriteSpinCount( int writeSpinCount )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ByteBufAllocator getAllocator()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChannelConfig setAllocator( ByteBufAllocator allocator )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RecvByteBufAllocator getRecvByteBufAllocator()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChannelConfig setRecvByteBufAllocator( RecvByteBufAllocator allocator )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAutoRead()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ChannelConfig setAutoRead( boolean autoRead )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getWriteBufferHighWaterMark()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark( int writeBufferHighWaterMark )
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getWriteBufferLowWaterMark()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark( int writeBufferLowWaterMark )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
