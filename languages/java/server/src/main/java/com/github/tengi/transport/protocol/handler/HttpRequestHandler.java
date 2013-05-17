package com.github.tengi.transport.protocol.handler;
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

import java.nio.charset.Charset;
import java.util.EnumSet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import com.github.tengi.Connection;
import com.github.tengi.ConnectionManager;
import com.github.tengi.TransportType;
import com.github.tengi.UniqueId;
import com.github.tengi.transport.AbstractChannelConnection;

public class HttpRequestHandler
    extends MessageToMessageCodec<HttpRequest, ByteBuf>
{

    private static final String HTTP_HEADER_NAME_CONNECTIONID = "XX-tengi-connection-id";

    private static final String HTTP_HEADER_NAME_SUPPORTED_TRANSPORT_TYPES = "XX-tengi-transport-types";

    private static final String HTTP_HEADER_NAME_TRANSPORT_TYPE = "XX-tengi-transport-type";

    private final ConnectionManager connectionManager;

    public HttpRequestHandler( ConnectionManager connectionManager )
    {
        this.connectionManager = connectionManager;
    }

    @Override
    protected void encode( ChannelHandlerContext ctx, ByteBuf msg, MessageBuf<Object> out )
        throws Exception
    {
        DefaultFullHttpResponse response =
            new DefaultFullHttpResponse( HttpVersion.HTTP_1_1, HttpResponseStatus.OK, msg );
        out.add( response );
    }

    @Override
    protected void decode( ChannelHandlerContext ctx, HttpRequest msg, MessageBuf<Object> out )
        throws Exception
    {
        if ( !msg.getMethod().equals( HttpMethod.POST ) )
        {
            HttpHeaders headers = msg.headers();
            String id = headers.get( HTTP_HEADER_NAME_CONNECTIONID );
            if ( id == null || id.length() == 0 )
            {
                writeErrorResponse( ctx, msg, HttpResponseStatus.BAD_REQUEST, "Illegal connectionId received" );
            }

            UniqueId connectionId = UniqueId.fromString( id );
            Connection connection = connectionManager.getConnectionByConnectionId( connectionId );

            if ( connection == null && !msg.getMethod().equals( HttpMethod.HEAD ) )
            {
                writeErrorResponse( ctx, msg, HttpResponseStatus.BAD_REQUEST,
                                    "No known connectionId but not an supported-transport-types request" );
            }
            else
            {
                writeSupportedTransportTypes( ctx, msg );
            }
        }
        else
        {
            // Since this is a POST request we'll try to parse it and handle it over to the assigned HttpConnection
            HttpHeaders headers = msg.headers();
            String id = headers.get( HTTP_HEADER_NAME_CONNECTIONID );
            if ( id == null || id.length() == 0 )
            {
                writeErrorResponse( ctx, msg, HttpResponseStatus.BAD_REQUEST, "Illegal connectionId received" );
                return;
            }

            UniqueId connectionId = UniqueId.fromString( id );
            Connection connection = connectionManager.getConnectionByConnectionId( connectionId );

            if ( connection == null )
            {
                // Connection is not yet registered at the ConnectionManager so we need to create a new HttpConnection
                // and pass over handling
                String transport = headers.get( HTTP_HEADER_NAME_TRANSPORT_TYPE );
                TransportType transportType = TransportType.byTransport( transport );
                if ( transportType == null )
                {
                    writeErrorResponse( ctx, msg, HttpResponseStatus.BAD_REQUEST, "Illegal transport-type: "
                        + transport );
                    return;
                }

                connection = connectionManager.registerConnection( connectionId, ctx.channel(), transportType );
            }

            AbstractChannelConnection channelConnection = (AbstractChannelConnection) connection;
            ChannelInboundMessageHandlerAdapter<ByteBuf> messageDecoder = channelConnection.getMessageDecoder();
            messageDecoder.messageReceived( ctx, ( (DefaultFullHttpRequest) msg ).content() );
        }
    }

    private void writeSupportedTransportTypes( ChannelHandlerContext ctx, HttpRequest msg )
    {
        StringBuilder sb = new StringBuilder();
        EnumSet<TransportType> supportedTransportTypes = connectionManager.getSupportedTransportTypes();
        for ( TransportType transportType : supportedTransportTypes )
        {
            sb.append( transportType.getTransport() ).append( "," );
        }
        sb.deleteCharAt( sb.length() - 1 );

        HttpResponse response =
            new DefaultFullHttpResponse( msg.getProtocolVersion(), HttpResponseStatus.ACCEPTED, Unpooled.EMPTY_BUFFER );

        HttpHeaders headers = response.headers();
        headers.set( HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8" );
        headers.set( HTTP_HEADER_NAME_SUPPORTED_TRANSPORT_TYPES, sb.toString() );
        ChannelFuture future = ctx.write( response );
        future.addListener( ChannelFutureListener.CLOSE );
    }

    private void writeErrorResponse( ChannelHandlerContext ctx, HttpRequest msg, HttpResponseStatus status,
                                     String errorMessage )
    {
        ByteBuf content = Unpooled.copiedBuffer( errorMessage, Charset.forName( "UTF-8" ) );
        HttpResponse response = new DefaultFullHttpResponse( msg.getProtocolVersion(), status, content );
        HttpHeaders headers = response.headers();
        headers.set( HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8" );
        ChannelFuture future = ctx.write( response );
        future.addListener( ChannelFutureListener.CLOSE );
    }
}
