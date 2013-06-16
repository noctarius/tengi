package com.github.tengi.rdp.frames;

public class NulFrame
    extends ReliableUdpFrame
{

    protected NulFrame()
    {
        super( FrameType.NUL );
    }

}
