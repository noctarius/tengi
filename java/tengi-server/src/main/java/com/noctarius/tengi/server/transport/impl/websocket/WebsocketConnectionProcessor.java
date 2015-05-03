package com.noctarius.tengi.server.transport.impl.websocket;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.server.server.ConnectionManager;
import com.noctarius.tengi.server.transport.ServerTransport;
import com.noctarius.tengi.server.transport.impl.ConnectionProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class WebsocketConnectionProcessor
        extends ConnectionProcessor<WebSocketFrame> {

    private final WebSocketServerHandshaker handshaker;

    public WebsocketConnectionProcessor(WebSocketServerHandshaker handshaker, //
                                        ConnectionManager connectionManager, Serializer serializer) {

        super(connectionManager, serializer, ServerTransport.WEBSOCKET_TRANSPORT);
        this.handshaker = handshaker;
    }

    @Override
    protected ReadableMemoryBuffer decode(ChannelHandlerContext ctx, WebSocketFrame frame)
            throws Exception {

        return null;
    }

    @Override
    protected ConnectionContext createConnectionContext(ChannelHandlerContext ctx, Identifier connectionId) {
        return null;
    }
}
