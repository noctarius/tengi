package com.github.tengi.transport.protocol.handler;

import com.github.tengi.ConnectionManager;

public class SpdyRequestHandler
    extends HttpRequestHandler
{

    public SpdyRequestHandler( ConnectionManager connectionManager )
    {
        super( connectionManager );
    }

}
