package com.github.tengi.rdp.frames;

public class AckFrame
    extends ReliableUdpFrame
{

    protected AckFrame()
    {
        super( FrameType.ACK );
    }

    public AckFrame( int sequenceNumber, int ackNumber )
    {
        super( FrameType.ACK );
        setSequenceNumber( sequenceNumber );
        setAckNumber( ackNumber );
    }

}
