package com.github.tengi.rdp.frames;

public class RstFrame
    extends ReliableUdpFrame
{

    protected RstFrame()
    {
        super( FrameType.RST );
    }

}
