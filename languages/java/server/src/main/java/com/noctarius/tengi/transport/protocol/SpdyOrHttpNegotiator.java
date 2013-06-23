package com.noctarius.tengi.transport.protocol;

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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.spdy.SpdyOrHttpChooser;

import javax.net.ssl.SSLEngine;

import org.eclipse.jetty.npn.NextProtoNego;

import com.noctarius.tengi.ConnectionManager;
import com.noctarius.tengi.transport.protocol.handler.HttpRequestHandler;
import com.noctarius.tengi.transport.protocol.handler.SpdyRequestHandler;
import com.noctarius.tengi.transport.protocol.handler.WebsocketRequestHandler;

public class SpdyOrHttpNegotiator
    extends SpdyOrHttpChooser
{

    private final ConnectionManager connectionManager;

    private final int maxSpdyContentLength;

    private final int maxHttpContentLength;

    public SpdyOrHttpNegotiator( int maxSpdyContentLength, int maxHttpContentLength, ConnectionManager connectionManager )
    {
        super( maxSpdyContentLength, maxHttpContentLength );
        this.connectionManager = connectionManager;
        this.maxSpdyContentLength = maxSpdyContentLength;
        this.maxHttpContentLength = maxHttpContentLength;
    }

    @Override
    protected void addHttpHandlers( ChannelHandlerContext ctx )
    {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast( "httpRquestDecoder", new HttpRequestDecoder() );
        pipeline.addLast( "httpResponseEncoder", new HttpResponseEncoder() );
        pipeline.addLast( "httpChunkAggregator", new HttpObjectAggregator( maxHttpContentLength ) );
        pipeline.addLast( "websocketHandler", new WebSocketServerProtocolHandler( "/wss" ) );
        pipeline.addLast( "websocketRequestHandler", new WebsocketRequestHandler() );
        pipeline.addLast( "httpRquestHandler", createHttpRequestHandlerForHttp() );
    }

    @Override
    protected SelectedProtocol getProtocol( SSLEngine engine )
    {
        NGNServerProvider provider = (NGNServerProvider) NextProtoNego.get( engine );
        String protocol = provider.getNegotiatedProtocol();

        switch ( protocol )
        {
            case NGNServerProvider.PROTOCOL_TYPE_HTTP_V1_0:
                return SelectedProtocol.HTTP_1_0;

            case NGNServerProvider.PROTOCOL_TYPE_HTTP_V1_1:
                return SelectedProtocol.HTTP_1_1;

            case NGNServerProvider.PROTOCOL_TYPE_SPDY_V2:
                return SelectedProtocol.SPDY_2;

            case NGNServerProvider.PROTOCOL_TYPE_SPDY_V3:
                return SelectedProtocol.SPDY_2;

            default:
                return SelectedProtocol.UNKNOWN;
        }
    }

    @Override
    protected MessageToMessageCodec<?, ?> createHttpRequestHandlerForHttp()
    {
        return new HttpRequestHandler( connectionManager );
    }

    @Override
    protected MessageToMessageCodec<?, ?> createHttpRequestHandlerForSpdy()
    {
        return new SpdyRequestHandler( connectionManager );
    }

}
