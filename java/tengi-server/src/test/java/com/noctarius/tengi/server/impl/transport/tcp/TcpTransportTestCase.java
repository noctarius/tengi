/*
 * Copyright (c) 2015-2016, Christoph Engelbert (aka noctarius) and
 * contributors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.noctarius.tengi.server.impl.transport.tcp;

import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.model.Packet;
import com.noctarius.tengi.server.ServerTransports;
import com.noctarius.tengi.server.impl.transport.AbstractStreamingTransportTestCase;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.packets.Handshake;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.codec.impl.DefaultCodec;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TcpTransportTestCase
        extends AbstractStreamingTransportTestCase {

    @Test(timeout = 120000)
    public void test_tcp_transport()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Object> future = new CompletableFuture<>();
        Packet packet = new Packet("login");
        packet.setValue("username", "Stan");
        Message message = Message.create(packet);

        ChannelReader<ChannelHandlerContext, ByteBuf> channelReader = (ctx, buffer) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            boolean loggedIn = codec.readBoolean();
            Identifier connectionId = codec.readObject();
            Object object = codec.readObject();
            if (loggedIn && object instanceof Handshake) {
                writeChannel(serializer, ctx, connectionId, message);
                return;
            }

            future.complete(object);
        };

        Initializer initializer = (pipeline) -> pipeline.addLast(inboundHandler(channelReader));

        Runner<Object, Channel> runner = (channel) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBytes("magic", DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER);
            codec.writeBoolean("loggedIn", false);
            codec.writeObject("handshake", new Handshake());
            channel.writeAndFlush(buffer);
            channel.closeFuture().addListener((ChannelFutureListener) (f) -> future.complete(null));

            Object result = future.get(120, TimeUnit.SECONDS);
            channel.close().sync();
            return result;
        };

        Object response = practice(runner, clientFactory(initializer), false, ServerTransports.TCP_TRANSPORT);
        assertEquals(message, response);
    }

    @Test(timeout = 120000)
    public void test_tcp_transport_ping_pong()
            throws Exception {

        Serializer serializer = Serializer.create(new DefaultProtocol(Collections.emptyList()));

        CompletableFuture<Packet> future = new CompletableFuture<>();

        ChannelReader<ChannelHandlerContext, ByteBuf> channelReader = (ctx, buffer) -> {
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            boolean loggedIn = codec.readBoolean();
            Identifier connectionId = codec.readObject();

            Object object = codec.readObject();
            if (object instanceof Handshake) {
                Packet packet = new Packet("pingpong");
                packet.setValue("counter", 1);
                Message message = Message.create(packet);
                writeChannel(serializer, ctx, connectionId, message);
                return;
            }

            Message message = (Message) object;
            Packet packet = message.getBody();

            int counter = packet.getValue("counter");
            if (counter == 4) {
                future.complete(packet);
            } else {
                packet.setValue("counter", counter + 1);
                message = Message.create(packet);

                ByteBuf buffer2 = Unpooled.buffer();
                MemoryBuffer memoryBuffer2 = MemoryBufferFactory.create(buffer2);
                DefaultCodec codec2 = new DefaultCodec(serializer.getProtocol(), memoryBuffer2);

                codec2.writeBoolean("loggedIn", loggedIn);
                codec2.writeObject("connectionId", connectionId);
                serializer.writeObject("message", message, codec2);

                ctx.channel().writeAndFlush(buffer2);
            }
        };

        Initializer initializer = (pipeline) -> pipeline.addLast(inboundHandler(channelReader));

        Runner<Packet, Channel> runner = (channel) -> {
            ByteBuf buffer = Unpooled.buffer();
            MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
            DefaultCodec codec = new DefaultCodec(serializer.getProtocol(), memoryBuffer);

            codec.writeBytes("magic", DefaultProtocolConstants.PROTOCOL_MAGIC_HEADER);
            codec.writeBoolean("loggedIn", false);
            codec.writeObject("handshake", new Handshake());
            channel.writeAndFlush(buffer);
            channel.closeFuture().addListener((ChannelFutureListener) (f) -> future.complete(null));

            Packet result = future.get(120, TimeUnit.SECONDS);
            channel.close().sync();
            return result;
        };

        Packet response = practice(runner, clientFactory(initializer), false, ServerTransports.TCP_TRANSPORT);
        assertEquals(4, (int) response.getValue("counter"));
    }

    protected static <T> SimpleChannelInboundHandler<T> inboundHandler(ChannelReader<ChannelHandlerContext, T> channelReader) {
        return new SimpleChannelInboundHandler<T>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, T object)
                    throws Exception {

                channelReader.channelRead(ctx, object);
            }
        };
    }

    private static void writeChannel(Serializer serializer, ChannelHandlerContext ctx, Identifier connectionId, Object value)
            throws Exception {

        ByteBuf buffer = ctx.alloc().directBuffer();
        MemoryBuffer memoryBuffer = MemoryBufferFactory.create(buffer);
        try (AutoClosableEncoder encoder = serializer.retrieveEncoder(memoryBuffer)) {
            encoder.writeBoolean("loggedIn", true);
            encoder.writeObject("connectionId", connectionId);
            serializer.writeObject("value", value, encoder);
        }
        ctx.channel().writeAndFlush(buffer);
    }

    private static ClientFactory<Channel> clientFactory(Initializer initializer) {
        return (host, port, ssl, group) -> {
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
            return future.sync().channel();
        };
    }

    private static interface Initializer {
        void initChannel(ChannelPipeline pipeline)
                throws Exception;
    }

}
