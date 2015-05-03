package com.noctarius.tengi.serialization.impl;

import com.noctarius.tengi.Identifier;
import com.noctarius.tengi.Message;
import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.marshaller.Marshaller;

enum MessageMarshaller
        implements Marshaller<Message> {

    INSTANCE;

    @Override
    public short getMarshallerId() {
        return DefaultProtocolConstants.SERIALIZED_TYPE_MESSAGE;
    }

    @Override
    public Message unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        Identifier messageId = memoryBuffer.readIdentifier();
        Object body = memoryBuffer.readObject(protocol);
        return Message.create(messageId, body);
    }

    @Override
    public void marshall(Message message, WritableMemoryBuffer memoryBuffer, Protocol protocol)
            throws Exception {

        Identifier messageId = message.getMessageId();
        Object body = message.getBody();

        memoryBuffer.writeIdentifier(messageId);
        memoryBuffer.writeObject(body, protocol);
    }

}
