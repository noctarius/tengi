package com.noctarius.tengi.server.transport.impl.http;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.Transport;
import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.connection.Connection;
import com.noctarius.tengi.connection.ConnectionContext;
import com.noctarius.tengi.connection.impl.LongPollingRequest;
import com.noctarius.tengi.serialization.Protocol;
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

    HttpConnectionContext(Channel channel, Identifier connectionId, Protocol protocol, Transport transport) {
        super(connectionId, protocol, transport);
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
