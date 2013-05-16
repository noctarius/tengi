package com.github.tengi.transport.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.npn.NextProtoNego.ServerProvider;

public class NGNServerProvider
    implements ServerProvider
{
    static final String PROTOCOL_TYPE_SPDY_V2 = "spdy/2";

    static final String PROTOCOL_TYPE_SPDY_V3 = "spdy/3";

    static final String PROTOCOL_TYPE_HTTP_V1_1 = "http/1.1";

    static final String PROTOCOL_TYPE_HTTP_V1_0 = "http/1.0";

    @SuppressWarnings( "serial" )
    private static final List<String> SUPPORTED_PROTOCOLS = new ArrayList<String>( 4 )
    {
        {
            addAll( Arrays.asList( new String[] { PROTOCOL_TYPE_SPDY_V2, PROTOCOL_TYPE_SPDY_V3, PROTOCOL_TYPE_HTTP_V1_1,
                PROTOCOL_TYPE_HTTP_V1_0 } ) );
        }
    };

    private volatile String protocol = null;

    @Override
    public void unsupported()
    {
        protocol = PROTOCOL_TYPE_HTTP_V1_1;
    }

    @Override
    public List<String> protocols()
    {
        return SUPPORTED_PROTOCOLS;
    }

    @Override
    public void protocolSelected( String protocol )
    {
        this.protocol = protocol;
    }

    public String getNegotiatedProtocol()
    {
        return protocol;
    }

}
