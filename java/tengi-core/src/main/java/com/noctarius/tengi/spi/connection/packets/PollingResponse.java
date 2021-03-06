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
package com.noctarius.tengi.spi.connection.packets;

import com.noctarius.tengi.core.model.Message;
import com.noctarius.tengi.core.serialization.Marshallable;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The <tt>PollingResponse</tt> class describes a basic packet to respond with
 * cached messages on non-streaming transports.
 */
@TypeId(DefaultProtocolConstants.TYPEID_POLLING_RESPONSE)
public final class PollingResponse
        implements Marshallable {

    private Collection<Message> messages;

    /**
     * Constructs a basic <tt>PollingResponse</tt> instance on deserialization.
     */
    public PollingResponse() {
    }

    /**
     * Constructs a <tt>PollingResponse</tt> instance for responding to a polling request.
     *
     * @param messages the cached messages to send
     */
    public PollingResponse(Collection<Message> messages) {
        this.messages = messages;
    }

    @Override
    public void marshall(Encoder encoder, Protocol protocol)
            throws Exception {

        encoder.writeInt32("length", messages.size());
        for (Message message : messages) {
            encoder.writeObject("message", message);
        }
    }

    @Override
    public void unmarshall(Decoder decoder, Protocol protocol)
            throws Exception {

        int length = decoder.readInt32();
        messages = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            messages.add(decoder.readObject());
        }
    }

    /**
     * Returns the delivered messages for message handling on client-side.
     *
     * @return a collection of messages from the server
     */
    public Collection<Message> getMessages() {
        return messages;
    }

}
