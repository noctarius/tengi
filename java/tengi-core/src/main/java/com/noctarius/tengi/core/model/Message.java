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
package com.noctarius.tengi.core.model;

import com.noctarius.tengi.core.impl.Validate;

/**
 * The <tt>Message</tt> interface defines a message which consists of an
 * unique identifier and a message body which is any kind of object that
 * have a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
 * or is an internally handled type.
 */
public final class Message {

    private final Identifier messageId;
    private final Object body;

    private Message(Identifier messageId, Object body) {
        Validate.notNull("messageId", messageId);
        Validate.notNull("body", body);
        this.messageId = messageId;
        this.body = body;
    }

    /**
     * Returns the unique message Id.
     *
     * @return the message id
     */
    public Identifier getMessageId() {
        return messageId;
    }

    /**
     * Returns the messages body object. This object is the real value of
     * the message and can be of any kind of object that
     * have a registered {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * or is an internally handled type.
     *
     * @param <O> the type of the messages object body
     * @return the message body
     */
    public <O> O getBody() {
        return (O) body;
    }

    public static Message create(Object body) {
        Identifier messageId = Identifier.randomIdentifier();
        return new Message(messageId, body);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }

        Message message = (Message) o;

        if (!body.equals(message.body)) {
            return false;
        }
        if (!messageId.equals(message.messageId)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = messageId.hashCode();
        result = 31 * result + body.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Message{" + "messageId=" + messageId + ", body=" + body + '}';
    }

    public static Message create(Identifier messageId, Object body) {
        return new Message(messageId, body);
    }

}
