package com.noctarius.tengi.server.transport.impl.negotiation;

import com.noctarius.tengi.server.server.ConnectionManager;
import com.noctarius.tengi.server.transport.impl.tcp.TcpConnectionProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.compression.SnappyFrameDecoder;
import io.netty.handler.codec.compression.SnappyFrameEncoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslHandler;

public class TcpBinaryNegotiator
        extends ChannelInboundHandlerAdapter {

    private final boolean detectSsl;
    private final boolean detectCompression;
    private final ConnectionManager connectionManager;

    public TcpBinaryNegotiator(boolean detectSsl, boolean detectCompression, ConnectionManager connectionManager) {
        this.detectSsl = detectSsl;
        this.detectCompression = detectCompression;
        this.connectionManager = connectionManager;
    }

    public void channelRead(ChannelHandlerContext ctx, Object object)
            throws Exception {

        if (!(object instanceof ByteBuf)) {
            return;
        }

        ByteBuf in = (ByteBuf) object;

        if (in.readableBytes() < 5) {
            // Not enough data to negotiate the protocol's magic header
            return;
        }

        if (isSsl(in)) {
            // Seems like an SSL connection, so activate it
            enableSsl(ctx);

        } else {
            // Read the magic header
            int magicByte0 = in.getUnsignedByte(in.readerIndex());
            int magicByte1 = in.getUnsignedByte(in.readerIndex() + 1);
            int magicByte2 = in.getUnsignedByte(in.readerIndex() + 2);
            int magicByte3 = in.getUnsignedByte(in.readerIndex() + 3);

            boolean acceptedProtocol = switchProtocol(magicByte0, magicByte1, magicByte2, magicByte3, ctx);
            if (!acceptedProtocol) {
                // Illegal protocol header or unknown protocol request
                in.clear();
                ctx.close();
                return;
            }
        }
        ctx.fireChannelRead(object);
    }

    private boolean switchProtocol(int magicByte0, int magicByte1, int magicByte2, int magicByte3, ChannelHandlerContext ctx) {
        switch (magicByte0) {
            case 0x1F:
                if (magicByte1 == 0x8B && detectCompression) {
                    enableGZIP(ctx);
                }
                break;

            case 0xFF:
                if (magicByte1 == 's' && magicByte2 == 'N' && magicByte3 == 'a' && detectCompression) {
                    enableSnappy(ctx);
                }
                break;

            case 'G':
                if (magicByte1 == 'E' && magicByte2 == 'T') {
                    switchToHttpNegotiation(ctx);
                }
                break;

            case 'P':
                if (magicByte1 == 'O' && magicByte2 == 'S' && magicByte3 == 'T') {
                    switchToHttpNegotiation(ctx);
                }
                break;

            case 'C':
                if (magicByte1 == 'O' && magicByte2 == 'N' && magicByte3 == 'N') {
                    switchToHttpNegotiation(ctx);
                }
                break;

            case 'T':
                if (magicByte1 == 'e' && magicByte2 == 'N' && magicByte3 == 'g') {
                    switchToNativeTcp(ctx);
                }
                break;

            default:
                return false;
        }

        return true;
    }

    private void switchToHttpNegotiation(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("httpNegotiator", new Http2Negotiator(1024 * 1024));
        pipeline.remove(this);
    }

    private void switchToNativeTcp(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("tcp-connection-processor", new TcpConnectionProcessor());
        pipeline.remove(this);
    }

    private boolean isSsl(ByteBuf in) {
        if (detectSsl) {
            return SslHandler.isEncrypted(in);
        }
        return false;
    }

    private void enableSsl(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("ssl", connectionManager.getSslContext().newHandler(ctx.alloc()));
        pipeline.addLast("negotiationSSL", new TcpBinaryNegotiator(false, true, connectionManager));
        pipeline.remove(this);
    }

    private void enableGZIP(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("gzipinflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast("gzipdeflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        pipeline.addLast("negotiationGZIP", new TcpBinaryNegotiator(true, false, connectionManager));
        pipeline.remove(this);
    }

    private void enableSnappy(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addLast("snappyinflater", new SnappyFrameEncoder());
        pipeline.addLast("snappydeflater", new SnappyFrameDecoder());
        pipeline.addLast("negotiationSnappy", new TcpBinaryNegotiator(true, false, connectionManager));
        pipeline.remove(this);
    }

}
