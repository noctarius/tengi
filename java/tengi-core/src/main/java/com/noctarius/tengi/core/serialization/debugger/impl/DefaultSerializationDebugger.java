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
package com.noctarius.tengi.core.serialization.debugger.impl;

import com.noctarius.tengi.core.serialization.debugger.DebuggableProtocol;
import com.noctarius.tengi.core.serialization.debugger.SerializationDebugger;
import com.noctarius.tengi.spi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.codec.Codec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultSerializationDebugger
        implements SerializationDebugger {

    /**
     * Static holder class to prevent eager initialization but provides thread-safe instantiation
     * of the default serialization debugger implementation.
     */
    public static final class Holder {

        /**
         * Static instance of the default serialization debugger implementation
         */
        public static final SerializationDebugger INSTANCE = new DefaultSerializationDebugger();
    }

    private static final ThreadLocal<Stack> STACK = new ThreadLocal<Stack>() {
        @Override
        protected Stack initialValue() {
            return new Stack();
        }
    };

    private DefaultSerializationDebugger() {
    }

    @Override
    public void push(Protocol protocol, Codec codec, Process process, Object value) {
        Stack stack = STACK.get();
        StackTraceElement stackTraceElement = buildStackFrame(protocol, codec, process, value);
        if (stackTraceElement == null) {
            return;
        }
        Node newHead = new Node(stackTraceElement);
        Node oldHead;
        do {
            oldHead = stack.head;
            newHead.next = oldHead;
        } while (!Stack.UPDATER.compareAndSet(stack, oldHead, newHead));
    }

    @Override
    public void pop() {
        Stack stack = STACK.get();
        Node oldHead;
        Node newHead;
        do {
            oldHead = stack.head;
            if (oldHead == null) {
                return;
            }
            newHead = oldHead.next;
        } while (!Stack.UPDATER.compareAndSet(stack, oldHead, newHead));
    }

    @Override
    public void fixFramesToStackTrace(Throwable throwable) {
        int serializationStackPosition = 0;
        StackTraceElement[] serializationStack = buildSerializationStack();

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            if (matches(stackTrace[i]) //
                    && equals(stackTrace[i], serializationStack[serializationStackPosition])) {

                stackTrace[i] = serializationStack[serializationStackPosition++];
                if (serializationStackPosition >= serializationStack.length) {
                    break;
                }
            }
        }
        throwable.setStackTrace(stackTrace);
    }

    private boolean equals(StackTraceElement current, StackTraceElement stored) {
        if (!current.getClassName().equals(stored.getClassName())) {
            return false;
        }
        if (!current.getFileName().equals(stored.getFileName())) {
            return false;
        }
        String[] split = stored.getMethodName().split(" ");
        if (split.length == 1) {
            return false;
        }
        return current.getMethodName().equals(split[0]);
    }

    private StackTraceElement[] buildSerializationStack() {
        try {
            Stack stack = STACK.get();

            List<Node> nodes = new ArrayList<>(20);

            // Read all nodes from stack
            Node node = stack.head;
            do {
                nodes.add(node);
            } while ((node = node.next) != null);

            StackTraceElement[] stackTraceElements = new StackTraceElement[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                stackTraceElements[i] = nodes.get(i).stackTraceElement;
            }

            return stackTraceElements;
        } catch (Exception e) {
            return new StackTraceElement[0];
        } finally {
            STACK.remove();
        }
    }

    private StackTraceElement buildStackFrame(Protocol protocol, Codec codec, Process process, Object value) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        int position = -1;
        for (int i = 0; i < stackTrace.length; i++) {
            if ("push".equals(stackTrace[i].getMethodName()) && matches(stackTrace[i + 1])) {
                position = i + 1;
                break;
            }
        }

        if (position == -1) {
            // No changeable stack information available
            return null;
        }

        StackTraceElement old = stackTrace[position];
        String methodName = buildMethodName(protocol, codec, process, old.getMethodName(), value);
        return new StackTraceElement(old.getClassName(), methodName, old.getFileName(), old.getLineNumber());
    }

    private boolean matches(StackTraceElement stackTraceElement) {
        if (!"writeObject".equals(stackTraceElement.getMethodName()) //
                && !"readObject".equals(stackTraceElement.getMethodName()) //
                && !stackTraceElement.getMethodName().contains("$writeObject$") //
                && !stackTraceElement.getMethodName().contains("$readObject$")) {
            return false;
        }

        try {
            Class<?> clazz = Class.forName(stackTraceElement.getClassName());
            return Codec.class.isAssignableFrom(clazz);
        } catch (Exception e) {
            return false;
        }
    }

    private String buildMethodName(Protocol protocol, Codec codec, //
                                   Process process, String methodName, Object value) {

        Class<?> type = findType(protocol, codec, process, value);
        String className = type == null ? "Unknown" : type.getName();

        StringBuilder sb = new StringBuilder(methodName);
        sb.append(" [").append(process.name()).append(" => ").append("type=").append(className);

        if (Debugger.STORE_VALUES) {
            sb.append(", value=").append(value);
        }

        return sb.append("]").toString();
    }

    private Class<?> findType(Protocol protocol, Codec codec, Process process, Object value) {
        if (value != null) {
            return value.getClass();
        }

        if (process != Process.DESERIALIZE) {
            return null;
        }

        if (protocol instanceof DebuggableProtocol) {
            DebuggableProtocol dp = (DebuggableProtocol) protocol;
            return dp.findType(codec);

        } else {
            ReadableMemoryBuffer memoryBuffer = codec.getReadableMemoryBuffer();
            int readerIndex = memoryBuffer.readerIndex();
            try {
                return protocol.readTypeId(codec);

            } finally {
                memoryBuffer.readerIndex(readerIndex);
            }
        }
    }

    private static class Stack {
        private static final AtomicReferenceFieldUpdater<Stack, Node> UPDATER = //
                AtomicReferenceFieldUpdater.newUpdater(Stack.class, Node.class, "head");

        private volatile Node head = null;
    }

    private static class Node {
        private final StackTraceElement stackTraceElement;

        private Node next;

        public Node(StackTraceElement stackTraceElement) {
            this.stackTraceElement = stackTraceElement;
        }
    }

}
