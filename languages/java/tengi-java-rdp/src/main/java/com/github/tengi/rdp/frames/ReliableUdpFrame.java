package com.github.tengi.rdp.frames;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

public abstract class ReliableUdpFrame
{

    public static final byte ACK_BIT_MASK = (byte) 0x40;

    public static final byte CHK_BIT_MASK = (byte) 0x04;

    public static final byte EAK_BIT_MASK = (byte) 0x20;

    public static final byte NUL_BIT_MASK = (byte) 0x08;

    public static final byte RST_BIT_MASK = (byte) 0x10;

    public static final byte SYN_BIT_MASK = (byte) 0x80;

    public static final byte TCS_BIT_MASK = (byte) 0x02;

    private static final int RUDP_FRAME_HEADER_SIZE = 6;

    private final FrameType frameType;

    private int headerLength = RUDP_FRAME_HEADER_SIZE;

    private int sequenceNumber;

    private int ackNumber;

    protected ReliableUdpFrame( FrameType frameType )
    {
        this.frameType = frameType;
    }

    public int getHeaderLength()
    {
        return headerLength;
    }

    public void setHeaderLength( int headerLength )
    {
        this.headerLength = headerLength;
    }

    public int getSequenceNumber()
    {
        return sequenceNumber;
    }

    public void setSequenceNumber( int sequenceNumber )
    {
        this.sequenceNumber = sequenceNumber;
    }

    public int getAckNumber()
    {
        return ackNumber;
    }

    public void setAckNumber( int ackNumber )
    {
        this.ackNumber = ackNumber;
    }

    public FrameType getFrameType()
    {
        return frameType;
    }

    protected ReliableUdpFrame readBytes( ByteBuf byteBuf, int offset, int length )
    {
        byteBuf.readByte(); // Clear packetType byte
        headerLength = byteBuf.readUnsignedByte();
        sequenceNumber = byteBuf.readUnsignedByte();
        ackNumber = byteBuf.readUnsignedByte();
        return this;
    }

    public ByteBuf writeBytes( ByteBuf byteBuf )
    {
        byteBuf.writeByte( getPacketType( frameType ) );
        byteBuf.writeByte( headerLength );
        byteBuf.writeByte( sequenceNumber );
        byteBuf.writeByte( ackNumber );
        return byteBuf;
    }

    public static ReliableUdpFrame readFrame( ByteBuf byteBuf )
        throws IOException
    {
        return readFrame( byteBuf, byteBuf.readerIndex(), byteBuf.readableBytes() );
    }

    public static ReliableUdpFrame readFrame( ByteBuf byteBuf, int offset, int length )
        throws IOException
    {
        if ( byteBuf.readableBytes() < RUDP_FRAME_HEADER_SIZE )
        {
            return null;
        }

        ReliableUdpFrame frame = null;
        byte packetType = byteBuf.getByte( offset );
        switch ( getFrameType( packetType ) )
        {
            case ACK:
                frame = new AckFrame();
                break;

            case CHK:
                frame = new ChkFrame();
                break;

            case EAK:
                frame = new EakFrame();
                break;

            case NUL:
                frame = new NulFrame();
                break;

            case RST:
                frame = new RstFrame();
                break;

            case SYN:
                frame = new SynFrame();
                break;

            case TCS:
                frame = new TcsFrame();
                break;
        }

        if ( frame == null )
        {
            return null;
        }

        return frame.readBytes( byteBuf, offset, length );
    }

    private static FrameType getFrameType( int packetType )
        throws IOException
    {
        if ( ( packetType & ACK_BIT_MASK ) != 0 )
        {
            return FrameType.ACK;
        }
        else if ( ( packetType & CHK_BIT_MASK ) != 0 )
        {
            return FrameType.CHK;
        }
        else if ( ( packetType & EAK_BIT_MASK ) != 0 )
        {
            return FrameType.EAK;
        }
        else if ( ( packetType & NUL_BIT_MASK ) != 0 )
        {
            return FrameType.NUL;
        }
        else if ( ( packetType & RST_BIT_MASK ) != 0 )
        {
            return FrameType.RST;
        }
        else if ( ( packetType & SYN_BIT_MASK ) != 0 )
        {
            return FrameType.SYN;
        }
        else if ( ( packetType & TCS_BIT_MASK ) != 0 )
        {
            return FrameType.TCS;
        }

        throw new IOException( "Illegal frame type in reliable UDP stream" );
    }

    private static byte getPacketType( FrameType frameType )
    {
        switch ( frameType )
        {
            case ACK:
                return ACK_BIT_MASK;

            case CHK:
                return CHK_BIT_MASK;

            case EAK:
                return EAK_BIT_MASK;

            case NUL:
                return NUL_BIT_MASK;

            case RST:
                return RST_BIT_MASK;

            case SYN:
                return SYN_BIT_MASK;

            case TCS:
                return TCS_BIT_MASK;
        }

        throw new RuntimeException( "Illegal frame type in reliable UDP stream" );
    }

}
