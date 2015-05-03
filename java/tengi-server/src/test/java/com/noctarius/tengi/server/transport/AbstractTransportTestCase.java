package com.noctarius.tengi.server.transport;

import com.noctarius.tengi.Transport;
import com.noctarius.tengi.config.Configuration;
import com.noctarius.tengi.server.server.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public abstract class AbstractTransportTestCase {

    protected static void practice(Initializer initializer, Runner runner, boolean ssl, Transport... serverTransports)
            throws Exception {

        Configuration configuration = new Configuration.Builder().addTransport(serverTransports).build();
        Server server = Server.create(configuration);
        server.start(System.out::println);

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap().group(group) //
                    .channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel)
                                throws Exception {

                            ChannelPipeline pipeline = channel.pipeline();

                            if (ssl) {
                                SslContext sslContext = SslContextBuilder //
                                        .forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

                                pipeline.addLast(sslContext.newHandler(channel.alloc(), "localhost", 8080));
                            }

                            initializer.initChannel(pipeline);
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8080);
            Channel channel = future.sync().channel();
            runner.run(channel);

        } finally {
            group.shutdownGracefully();
            server.stop();
        }
    }

    protected static SimpleChannelInboundHandler<HttpObject> inboundHandler(ChannelReader channelReader) {
        return new SimpleChannelInboundHandler<HttpObject>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpObject object)
                    throws Exception {

                channelReader.channelRead(ctx, object);
            }
        };
    }

    protected static interface Initializer {
        void initChannel(ChannelPipeline pipeline)
                throws Exception;
    }

    protected static interface Runner {
        void run(Channel channel)
                throws Exception;
    }

    protected static interface ChannelReader {
        void channelRead(ChannelHandlerContext ctx, HttpObject object)
                throws Exception;
    }
}
