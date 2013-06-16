package com.github.tengi.rdp;

import com.github.tengi.rdp.frames.FrameType;

import io.netty.buffer.ByteBuf;

public class RdpSegment
{

    public static final byte SYN_BIT_MASK = (byte) 0x80;

    public static final byte ACK_BIT_MASK = (byte) 0x40;

    public static final byte EAK_BIT_MASK = (byte) 0x20;

    public static final byte RST_BIT_MASK = (byte) 0x10;

    public static final byte NUL_BIT_MASK = (byte) 0x08;

    public static final int RUDP_FRAME_HEADER_SIZE = 6;

    private int protocolVersion = 2;

    private RdpOpcode opcode;

    private int headerLength;

    private int sourcePort;

    private int destinationPort;

    private int dataLength;

    private int sequenceNumber;

    private int acknowledgementNumber;

    private int checksum;

    protected void writeBaseHeader( ByteBuf data )
    {
        /*
         *     0             0 0 1           1
         *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5
         *   +-+-+-+-+-+-+---+---------------+
         *   |S|A|E|R|N| |Ver|    Header     |
         * 0 |Y|C|A|S|U|0|No.|    Length     |
         *   |N|K|K|T|L| |   |               |
         *   +-+-+-+-+-+-+---+---------------+
         * 1 |          Source Port          |
         *   +---------------+---------------+
         * 2 |        Destination Port       |
         *   +---------------+---------------+
         * 3 |          Data Length          |
         *   +---------------+---------------+
         * 4 |                               |
         *   +---     Sequence Number     ---+
         * 5 |                               |
         *   +---------------+---------------+
         * 6 |                               |
         *   +---  Acknowledgement Number ---+
         * 7 |                               |
         *   +---------------+---------------+
         * 8 |           Checksum            |
         *   +---------------+---------------+
         * 9 |     Variable Header Area      |
         *   .                               .
         *   .                               .
         *   |                               |
         *   +---------------+---------------+
         */
        int mask = getOpcodeMask( opcode );
        data.writeByte( ( mask | protocolVersion ) );
        data.writeByte( RUDP_FRAME_HEADER_SIZE );
        data.writeShort( sourcePort );
        data.writeShort( destinationPort );
        data.writeShort( sequenceNumber );
        data.writeShort( acknowledgementNumber );
        data.writeShort( checksum );
    }

    protected final int getOpcodeMask( RdpOpcode opcode )
    {
        if ( opcode == null )
        {
            throw new NullPointerException( "opcode must not be null" );
        }

        switch ( opcode )
        {
            case SYN:
                return SYN_BIT_MASK;

            case ACK:
                return ACK_BIT_MASK;

            case EAK:
                return EAK_BIT_MASK;

            case RST:
                return RST_BIT_MASK;

            case NUL:
                return NUL_BIT_MASK;
        }

        throw new IllegalArgumentException( "opcode is unknown" );
    }

    protected final RdpOpcode getOpcode( int mask )
    {
        if ( ( mask & SYN_BIT_MASK ) != 0 )
        {
            return RdpOpcode.SYN;
        }
        if ( ( mask & ACK_BIT_MASK ) != 0 )
        {
            return RdpOpcode.ACK;
        }
        else if ( ( mask & EAK_BIT_MASK ) != 0 )
        {
            return RdpOpcode.EAK;
        }
        else if ( ( mask & RST_BIT_MASK ) != 0 )
        {
            return RdpOpcode.RST;
        }
        else if ( ( mask & NUL_BIT_MASK ) != 0 )
        {
            return RdpOpcode.NUL;
        }

        throw new IllegalArgumentException( "Illegal opcode mask" );
    }

}
