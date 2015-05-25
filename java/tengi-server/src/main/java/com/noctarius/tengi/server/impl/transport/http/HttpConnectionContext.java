/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.server.impl.transport.http;

import com.noctarius.tengi.core.connection.Connection;
import com.noctarius.tengi.core.model.Identifier;
import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.impl.CompletableFutureUtil;
import com.noctarius.tengi.core.impl.ExceptionUtil;
import com.noctarius.tengi.spi.buffer.MemoryBuffer;
import com.noctarius.tengi.spi.buffer.impl.MemoryBufferFactory;
import com.noctarius.tengi.spi.connection.ConnectionContext;
import com.noctarius.tengi.core.connection.Transport;
import com.noctarius.tengi.spi.connection.packets.PollingRequest;
import com.noctarius.tengi.spi.connection.packets.PollingResponse;
import com.noctarius.tengi.spi.serialization.Serializer;
import com.noctarius.tengi.spi.serialization.codec.AutoClosableEncoder;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class HttpConnectionContext
        extends ConnectionContext<Channel> {

    private final Queue<QueueEntry> messageQueue = new ConcurrentLinkedQueue<>();

    private final Lock longPollingLock = new ReentrantLock();
    private final Condition longPollingWaitCondition = longPollingLock.newCondition();

    HttpConnectionContext(Identifier connectionId, Serializer serializer, Transport transport) {
        super(connectionId, serializer, transport);
    }

    @Override
    public CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        messageQueue.add(new QueueEntry(memoryBuffer, message, future));

        longPollingLock.lock();
        try {
            longPollingWaitCondition.signal();
        } finally {
            longPollingLock.unlock();
        }
        return future;
    }

    @Override
    public CompletableFuture<Connection> writeSocket(Channel channel, Connection connection, MemoryBuffer memoryBuffer)
            throws Exception {

        ByteBuf bb = channel.alloc().directBuffer();
        MemoryBuffer buffer = preparePacket(MemoryBufferFactory.create(bb));
        buffer.writeBuffer(memoryBuffer);
        return CompletableFutureUtil.executeAsync(() -> {
            sendHttpResponse(channel, bb);
            return connection;
        });
    }

    @Override
    public CompletableFuture<Connection> close(Connection connection) {
        return CompletableFutureUtil.executeAsync(() -> connection);
    }

    @Override
    public void processPollingRequest(Channel channel, Connection connection, PollingRequest request) {
        try {
            if (!getConnectionId().equals(connection.getConnectionId())) {
                channel.close().sync();
                return;
            }

            Collection<QueueEntry> messages = drainMessageQueue();
            if (messages.size() == 0) {
                longPollingLock.lock();
                try {
                    longPollingWaitCondition.await(5, TimeUnit.SECONDS);
                    messages = drainMessageQueue();
                } catch (InterruptedException e) {
                    // ignore and just return an empty response
                } finally {
                    longPollingLock.unlock();
                }
            }

            PollingResponse pollingResponse = new PollingResponse(new QueueEntryMessageList(messages));

            ByteBuf buffer = channel.alloc().directBuffer();
            MemoryBuffer memoryBuffer = preparePacket(MemoryBufferFactory.create(buffer));
            try (AutoClosableEncoder encoder = getSerializer().retrieveEncoder(memoryBuffer)) {
                encoder.writeObject("pollingResponse", Message.create(pollingResponse));
            }

            sendHttpResponse(channel, buffer);

        } catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private void sendHttpResponse(Channel channel, ByteBuf buffer) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, getProtocol().getMimeType());
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, buffer.writerIndex());
        response.headers().set(HttpHeaderNames.CONNECTION, "close");
        ChannelFuture channelFuture = channel.writeAndFlush(response);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future)
                    throws Exception {

                future.channel().close().sync();
            }
        });
    }

    private Collection<QueueEntry> drainMessageQueue() {
        Collection<QueueEntry> queueEntries = new ArrayList<>();

        QueueEntry queueEntry;
        while ((queueEntry = messageQueue.poll()) != null) {
            queueEntries.add(queueEntry);
        }
        return queueEntries;
    }

    private static final class QueueEntry {
        final MemoryBuffer memoryBuffer;
        final Message message;
        final CompletableFuture<Message> future;

        private QueueEntry(MemoryBuffer memoryBuffer, Message message, CompletableFuture<Message> future) {
            this.memoryBuffer = memoryBuffer;
            this.message = message;
            this.future = future;
        }
    }

    private static final class QueueEntryMessageList
            extends AbstractCollection<Message>
            implements List<Message> {

        private final Collection<QueueEntry> messages;

        private QueueEntryMessageList(Collection<QueueEntry> messages) {
            this.messages = messages;
        }

        @Override
        public Iterator<Message> iterator() {
            final Iterator<QueueEntry> iterator = messages.iterator();
            return new Iterator<Message>() {

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Message next() {
                    return iterator.next().message;
                }
            };
        }

        @Override
        public boolean addAll(int index, Collection<? extends Message> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message get(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message set(int index, Message element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, Message element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Message remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int lastIndexOf(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<Message> listIterator() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ListIterator<Message> listIterator(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Message> subList(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return messages.size();
        }
    }

}
