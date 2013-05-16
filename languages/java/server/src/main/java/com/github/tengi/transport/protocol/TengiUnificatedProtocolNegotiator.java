package com.github.tengi.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.SnappyFramedDecoder;
import io.netty.handler.codec.compression.SnappyFramedEncoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslHandler;

import com.github.tengi.ConnectionManager;

public class TengiUnificatedProtocolNegotiator
    extends ChannelInboundByteHandlerAdapter
{

    private final ConnectionManager connectionManager;

    private final boolean tryDetectSSL;

    private final boolean tryDetectCompression;

    public TengiUnificatedProtocolNegotiator( boolean tryDetectSSL, boolean tryDetectCompression,
                                              ConnectionManager connectionManager )
    {
        this.connectionManager = connectionManager;
        this.tryDetectSSL = tryDetectSSL;
        this.tryDetectCompression = tryDetectCompression;
    }

    @Override
    protected void inboundBufferUpdated( ChannelHandlerContext ctx, ByteBuf in )
        throws Exception
    {
        // Magic header cannot be read at this point
        if ( in.readableBytes() < 5 )
        {
            return;
        }

        if ( isSSLConnection( in ) )
        {
            // Seems like we need to switch to SSL support
            enableSSL( ctx );
        }
        else
        {
            // Let's try to find the real underlying protocol
            int readerIndex = in.readerIndex();

            // Read the magic header
            int magicByte0 = in.readUnsignedByte();
            int magicByte1 = in.readUnsignedByte();
            int magicByte2 = in.readUnsignedByte();
            int magicByte3 = in.readUnsignedByte();

            // Reset the buffer to the streams beginning
            in.readerIndex( readerIndex );

            boolean acceptedProtocol = switchProtocol( magicByte0, magicByte1, magicByte2, magicByte3, ctx );
            if ( !acceptedProtocol )
            {
                // Illegal protocol header or unknown protocol request
                in.skipBytes( in.readableBytes() );
                ctx.close();
            }
        }
    }

    private boolean switchProtocol( int magicByte0, int magicByte1, int magicByte2, int magicByte3,
                                    ChannelHandlerContext ctx )
    {
        switch ( magicByte0 )
        {
            case 0x1F:
                if ( magicByte1 == 0x8B && tryDetectCompression )
                    enableGZIP( ctx );
                break;

            case 0xFF:
                if ( magicByte1 == 's' && magicByte2 == 'N' && magicByte3 == 'a' && tryDetectCompression )
                    enableSnappy( ctx );
                break;

            case 'G':
                if ( magicByte1 == 'E' && magicByte2 == 'T' )
                    switchToHTTP( ctx );
                break;

            case 'P':
                if ( magicByte1 == 'O' && magicByte2 == 'S' && magicByte3 == 'T' )
                    switchToHTTP( ctx );
                break;

            case 'C':
                if ( magicByte1 == 'O' && magicByte2 == 'N' && magicByte3 == 'N' )
                    switchToHTTP( ctx );
                break;

            case 'T':
                if ( magicByte1 == 'e' && magicByte2 == 'N' && magicByte3 == 'g' )
                    switchToTengiByteStream( ctx );
                break;

            default:
                return false;
        }

        return true;
    }

    private boolean isSSLConnection( ByteBuf buffer )
    {
        if ( tryDetectSSL )
        {
            return SslHandler.isEncrypted( buffer );
        }

        return false;
    }

    private void switchToHTTP( ChannelHandlerContext ctx )
    {
        ChannelPipeline pipeline = ctx.pipeline();

        pipeline.addLast( "httpNegotiator", new SpdyOrHttpNegotiator( 1024 * 1024, 1024 * 1024, connectionManager ) );

        pipeline.remove( this );
    }

    private void switchToTengiByteStream( ChannelHandlerContext ctx )
    {
        ChannelPipeline pipeline = ctx.pipeline();

        pipeline.remove( this );
    }

    private void enableSSL( ChannelHandlerContext ctx )
    {
        ChannelPipeline pipeline = ctx.pipeline();

        pipeline.addLast( "ssl", new SslHandler( connectionManager.getSSLEngine() ) );
        pipeline.addLast( "negotiationSSL", new TengiUnificatedProtocolNegotiator( false, true, connectionManager ) );

        pipeline.remove( this );
    }

    private void enableGZIP( ChannelHandlerContext ctx )
    {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast( "gzipinflater", ZlibCodecFactory.newZlibEncoder( ZlibWrapper.GZIP ) );
        pipeline.addLast( "gzipdeflater", ZlibCodecFactory.newZlibDecoder( ZlibWrapper.GZIP ) );
        pipeline.addLast( "negotiationGZIP", new TengiUnificatedProtocolNegotiator( true, false, connectionManager ) );

        pipeline.remove( this );
    }

    public void enableSnappy( ChannelHandlerContext ctx )
    {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast( "snappyinflater", new SnappyFramedEncoder() );
        pipeline.addLast( "snappydeflater", new SnappyFramedDecoder() );
        pipeline.addLast( "negotiationSnappy", new TengiUnificatedProtocolNegotiator( true, false, connectionManager ) );

        pipeline.remove( this );
    }

}
