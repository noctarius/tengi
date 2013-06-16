package com.github.tengi.rdp.frames;

public class SynFrame
    extends ReliableUdpFrame
{

    public SynFrame()
    {
        super( FrameType.SYN );
    }

}
