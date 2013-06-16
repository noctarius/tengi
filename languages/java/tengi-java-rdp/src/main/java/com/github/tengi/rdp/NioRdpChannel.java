package com.github.tengi.rdp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.EventLoop;
import io.netty.channel.MessageList;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

import com.github.tengi.rdp.frames.SynFrame;

public class NioRdpChannel
    extends AbstractChannel
    implements RdpChannel
{

    private static final ChannelMetadata METADATA = new ChannelMetadata( false );

    private final int sequenceMax;

    private final int receiveBufferMax;

    private final DatagramChannel channel;

    private final ByteBufAllocator allocator;

    private final RdpChannelConfiguration configuration;

    private volatile InetSocketAddress recipient;

    private int sequenceInitial;

    private int sequenceNext;

    private int sequenceUna;

    private RdpChannelState state;

    public NioRdpChannel( DatagramChannel channel, RdpChannelConfiguration configuration )
    {
        super( channel, null );
        this.channel = channel;
        this.allocator = channel.alloc();
        this.sequenceMax = configuration.getSequenceMax();
        this.receiveBufferMax = configuration.getReceiveBufferMax();
        this.configuration = configuration;
    }

    @Override
    public ChannelConfig config()
    {
        return configuration;
    }

    @Override
    public boolean isOpen()
    {
        return parent().isOpen();
    }

    @Override
    public boolean isActive()
    {
        return parent().isActive();
    }

    @Override
    public ChannelMetadata metadata()
    {
        return METADATA;
    }

    @Override
    protected AbstractUnsafe newUnsafe()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isCompatible( EventLoop loop )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected SocketAddress localAddress0()
    {
        return parent().localAddress();
    }

    @Override
    protected SocketAddress remoteAddress0()
    {
        return parent().remoteAddress();
    }

    @Override
    protected void doBind( SocketAddress localAddress )
        throws Exception
    {
        // do nothing
    }

    @Override
    protected void doDisconnect()
        throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doClose()
        throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doBeginRead()
        throws Exception
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected int doWrite( MessageList<Object> msgs, int index )
        throws Exception
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected boolean isFlushPending()
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void bind( int port )
    {
        this.sequenceInitial = new Random().nextInt( 255 );
        this.sequenceNext = this.sequenceInitial + 1;
        this.sequenceUna = this.sequenceInitial;
        this.state = RdpChannelState.LISTEN;

        ChannelFuture future = channel.bind( new InetSocketAddress( port ) );
        future.addListener( new ChannelFutureListener()
        {

            @Override
            public void operationComplete( ChannelFuture future )
                throws Exception
            {
                if ( future.cause() != null )
                {
                    state = RdpChannelState.CLOSED;
                }
            }
        } );
    }

    public ChannelFuture connect( String host, int port )
    {
        this.sequenceInitial = new Random().nextInt( 255 );
        this.sequenceNext = this.sequenceInitial + 1;
        this.sequenceUna = this.sequenceInitial;

        try
        {
            bind( findFreePort() );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to select free local port ", e );
        }

        ByteBuf buffer = allocator.ioBuffer( RdpSegment.RUDP_FRAME_HEADER_SIZE );
        ChannelFuture future = write0( new SynFrame().writeBytes( buffer ) );
        future.addListener( new ChannelFutureListener()
        {

            @Override
            public void operationComplete( ChannelFuture future )
                throws Exception
            {
                state = RdpChannelState.SYN_SENT;
            }
        } );

        return future;
    }

    private ChannelFuture write0( ByteBuf data )
    {
        return channel.write( new DatagramPacket( data, recipient ) );
    }

    public static int findFreePort()
        throws IOException
    {
        ServerSocket server = new ServerSocket( 0 );
        int port = server.getLocalPort();
        server.close();
        return port;
    }

    private class QueuedRdpSegment
    {
        private volatile RdpSegment segment;

        private InetSocketAddress recipient;
    }

    private class SequentialReceiveBuffer
    {
        private final Queue<RdpSegment> bufferedSegments = new PriorityBlockingQueue<>( sequenceMax );
    }

}
