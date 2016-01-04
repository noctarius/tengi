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
package com.noctarius.tengi.rudp.nio;

import com.noctarius.tengi.spi.ringbuffer.RingBuffer;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReliableServerSocketChannel
        extends SelectableChannel
        implements NetworkChannel {

    private static final Set<SocketOption<?>> VALID_OPTIONS = defaultSocketOptions();

    private final DatagramChannel datagramChannel;

    private final int backlog;

    private final RingBuffer<AcceptRequest> acceptQueue;

    public static ReliableServerSocketChannel open(int backlog)
            throws IOException {

        return new ReliableServerSocketChannel(DatagramChannel.open(), backlog);
    }

    private ReliableServerSocketChannel(DatagramChannel datagramChannel, int backlog) {
        this.acceptQueue = RingBuffer.create(backlog);
        this.datagramChannel = datagramChannel;
        this.backlog = backlog;
    }

    @Override
    protected void implCloseChannel()
            throws IOException {

    }

    public ReliableChannel accept()
            throws IOException {

        if (!isOpen()) {
            throw new ClosedChannelException();
        }
        if (!isBound()) {
            throw new NotYetBoundException();
        }

        AcceptRequest acceptRequest = acceptQueue.read();
        return null;
    }

    // Property handlers

    @Override
    public <T> ReliableServerSocketChannel setOption(SocketOption<T> name, T value)
            throws IOException {

        if (StandardSocketOptions.SO_KEEPALIVE.equals(name)) {
            // TODO store locally
            return this;

        } else {
            datagramChannel.setOption(name, value);
            return this;
        }
    }

    @Override
    public <T> T getOption(SocketOption<T> name)
            throws IOException {

        if (StandardSocketOptions.SO_KEEPALIVE.equals(name)) {
            // TODO return locally
            return null;
        }

        return datagramChannel.getOption(name);
    }

    @Override
    public Set<SocketOption<?>> supportedOptions() {
        return VALID_OPTIONS;
    }

    public DatagramChannel socket() {
        return datagramChannel;
    }

    public boolean isBound()
            throws IOException {

        return getLocalAddress() != null;
    }

    // Delegates

    @Override
    public ReliableServerSocketChannel bind(SocketAddress local)
            throws IOException {

        datagramChannel.bind(local);
        return this;
    }

    @Override
    public SocketAddress getLocalAddress()
            throws IOException {

        return datagramChannel.getLocalAddress();
    }

    @Override
    public SelectorProvider provider() {
        return datagramChannel.provider();
    }

    @Override
    public int validOps() {
        return datagramChannel.validOps();
    }

    @Override
    public boolean isRegistered() {
        return datagramChannel.isRegistered();
    }

    @Override
    public SelectionKey keyFor(Selector sel) {
        return datagramChannel.keyFor(sel);
    }

    @Override
    public SelectionKey register(Selector sel, int ops, Object att)
            throws ClosedChannelException {

        return datagramChannel.register(sel, ops, att);
    }

    @Override
    public SelectableChannel configureBlocking(boolean block)
            throws IOException {

        return datagramChannel.configureBlocking(block);
    }

    @Override
    public boolean isBlocking() {
        return datagramChannel.isBlocking();
    }

    @Override
    public Object blockingLock() {
        return datagramChannel.blockingLock();
    }

    private static Set<SocketOption<?>> defaultSocketOptions() {
        Set<SocketOption<?>> validOptions = new HashSet<>(6);
        validOptions.add(StandardSocketOptions.SO_SNDBUF);
        validOptions.add(StandardSocketOptions.SO_RCVBUF);
        validOptions.add(StandardSocketOptions.SO_REUSEADDR);
        validOptions.add(StandardSocketOptions.SO_BROADCAST);
        validOptions.add(StandardSocketOptions.IP_TOS);
        validOptions.add(StandardSocketOptions.SO_KEEPALIVE);
        return Collections.unmodifiableSet(validOptions);
    }

    private static class AcceptRequest {

    }

}
