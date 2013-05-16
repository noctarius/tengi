package com.github.tengi.transport.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.spdy.SpdyOrHttpChooser;

import javax.net.ssl.SSLEngine;

import org.eclipse.jetty.npn.NextProtoNego;

import com.github.tengi.ConnectionManager;
import com.github.tengi.transport.protocol.handler.HttpRequestHandler;
import com.github.tengi.transport.protocol.handler.SpdyRequestHandler;
import com.github.tengi.transport.protocol.handler.WebsocketRequestHandler;

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
    protected ChannelInboundMessageHandler<?> createHttpRequestHandlerForHttp()
    {
        return new HttpRequestHandler( connectionManager );
    }

    @Override
    protected ChannelInboundMessageHandler<?> createHttpRequestHandlerForSpdy()
    {
        return new SpdyRequestHandler( connectionManager );
    }

}
