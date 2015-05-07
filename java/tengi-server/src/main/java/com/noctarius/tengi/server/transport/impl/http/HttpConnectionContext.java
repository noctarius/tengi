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
package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.connection.impl.LongPollingRequest;
import com.noctarius.tengi.serialization.Serializer;
import com.noctarius.tengi.utils.CompletableFutureUtil;
import com.noctarius.tengi.utils.ExceptionUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

class HttpConnectionContext
        extends ConnectionContext {

    private final AtomicReference<Channel> channelRef = new AtomicReference<>();
    private final Queue<QueueEntry> messageQueue = new ConcurrentLinkedQueue<>();

    HttpConnectionContext(Channel channel, Identifier connectionId, Serializer serializer, Transport transport) {
        super(connectionId, serializer, transport);
        this.channelRef.set(channel);
    }

    @Override
    public CompletableFuture<Message> writeMemoryBuffer(MemoryBuffer memoryBuffer, Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        messageQueue.add(new QueueEntry(memoryBuffer, message, future));
        return future;
    }

    @Override
    public CompletableFuture<Connection> close(Connection connection) {
        return CompletableFutureUtil.executeAsync(() -> {
            getChannel().close().sync();
            return connection;
        });
    }

    @Override
    public void processLongPollingRequest(LongPollingRequest request) {
        try {
            if (!getConnectionId().equals(request.getConnectionId())) {
                getChannel().close().sync();
                return;
            }

            Collection<QueueEntry> messages = drainMessageQueue();

        } catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    void setChannel(Channel channel) {
        channelRef.set(channel);
    }

    private Channel getChannel() {
        return channelRef.get();
    }

    private Collection<QueueEntry> drainMessageQueue() {
        Collection<QueueEntry> queueEntries = new ArrayList<>();

        QueueEntry queueEntry;
        while ((queueEntry = messageQueue.poll()) != null) {
            queueEntries.add(queueEntry);
        }
        return queueEntries;
    }

    static final class QueueEntry {
        final MemoryBuffer memoryBuffer;
        final Message message;
        final CompletableFuture<Message> future;

        private QueueEntry(MemoryBuffer memoryBuffer, Message message, CompletableFuture<Message> future) {
            this.memoryBuffer = memoryBuffer;
            this.message = message;
            this.future = future;
        }
    }
}
