package com.noctarius.tengi.serialization.debugger.impl;

import com.noctarius.tengi.buffer.MemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.debugger.DebuggableProtocol;
import com.noctarius.tengi.serialization.debugger.SerializationDebugger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultSerializationDebugger
        implements SerializationDebugger {

    private static final ThreadLocal<Stack> STACK = new ThreadLocal<Stack>() {
        @Override
        protected Stack initialValue() {
            return new Stack();
        }
    };

    @Override
    public void push(Protocol protocol, MemoryBuffer memoryBuffer, Process process, Object value) {
        Stack stack = STACK.get();
        StackTraceElement stackTraceElement = buildStackFrame(protocol, memoryBuffer, process, value);
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

    private StackTraceElement buildStackFrame(Protocol protocol, MemoryBuffer memoryBuffer, Process process, Object value) {
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
        String methodName = buildMethodName(protocol, memoryBuffer, process, old.getMethodName(), value);
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
            return MemoryBuffer.class.isAssignableFrom(clazz);
        } catch (Exception e) {
            return false;
        }
    }

    private String buildMethodName(Protocol protocol, MemoryBuffer memoryBuffer, //
                                   Process process, String methodName, Object value) {

        Class<?> type = findType(protocol, memoryBuffer, process, value);
        String className = type == null ? "Unknown" : type.getName();

        StringBuilder sb = new StringBuilder(methodName);
        sb.append(" [").append(process.name()).append(" => ").append("type=").append(className);

        if (Debugger.STORE_VALUES) {
            sb.append(", value=").append(value);
        }

        return sb.append("]").toString();
    }

    private Class<?> findType(Protocol protocol, MemoryBuffer memoryBuffer, Process process, Object value) {
        if (value != null) {
            return value.getClass();
        }

        if (process != Process.DESERIALIZE) {
            return null;
        }

        if (protocol instanceof DebuggableProtocol) {
            DebuggableProtocol dp = (DebuggableProtocol) protocol;
            return dp.findType(memoryBuffer);

        } else {
            int readerIndex = memoryBuffer.readerIndex();
            try {
                return protocol.readTypeId(memoryBuffer);

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
