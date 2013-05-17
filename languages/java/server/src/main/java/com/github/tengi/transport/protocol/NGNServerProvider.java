package com.github.tengi.transport.protocol;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
