package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.server.server.ConnectionManager;
import com.noctarius.tengi.server.transport.ServerTransport;
import com.noctarius.tengi.server.transport.impl.ConnectionProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpConnectionProcessor
        extends ConnectionProcessor<FullHttpRequest> {

    public HttpConnectionProcessor(ConnectionManager connectionManager, Serializer serializer) {
        super(connectionManager, serializer, ServerTransport.HTTP_TRANSPORT);
    }

    @Override
    protected ReadableMemoryBuffer decode(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {

        // Only POST requests are allowed, kill the request
        if (HttpMethod.POST != request.method() && HttpMethod.GET != request.method()) {
            sendHttpResponse(ctx.channel(), request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED));
            return null;
        }

        // Unknown uri, kill the request
        if (!"/channel".equals(request.uri())) {
            sendHttpResponse(ctx.channel(), request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return null;
        }

        String mimeType = getSerializer().getProtocol().getMimeType();
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);

        // Wrong content type, kill the request
        if (!mimeType.equals(contentType)) {
            sendHttpResponse(ctx.channel(), request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_ACCEPTABLE));
            return null;
        }

        sendHttpResponse(ctx.channel(), request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));

        // TODO: Pool MemoryBuffers
        return MemoryBufferFactory.unpooled(request.content(), getSerializer().getProtocol());
    }

    @Override
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return new HttpConnectionContext(ctx.channel(), connectionId, getSerializer().getProtocol(), getTransport());
    }

    static void sendHttpResponse(Channel channel, FullHttpRequest request, FullHttpResponse response) {
        // Generate an error page if response getStatus code is not OK (200).
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            HttpHeaderUtil.setContentLength(response, response.content().readableBytes());
        }

        // Send the response and close the connection if necessary.
        ChannelFuture f = channel.writeAndFlush(response);
        if (!HttpHeaderUtil.isKeepAlive(request) || response.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
