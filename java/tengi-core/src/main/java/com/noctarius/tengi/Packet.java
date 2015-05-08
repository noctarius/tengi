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
package com.noctarius.tengi;

import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.codec.Decoder;
import com.noctarius.tengi.serialization.codec.Encoder;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;
import com.noctarius.tengi.utils.Validate;

import java.util.HashMap;
import java.util.Map;

@TypeId(DefaultProtocolConstants.TYPEID_PACKET)
public class Packet
        implements Marshallable {

    private final Map<String, Object> values = new HashMap<>();
    private String packetName;

    public Packet(String packetName) {
        Validate.notNull("packageName", packetName);
        this.packetName = packetName;
    }

    public <V> void setValue(String key, V value) {
        Validate.notNull("key", key);
        Validate.notNull("value", value);
        values.put(key, value);
    }

    public <V> V getValue(String key) {
        Validate.notNull("key", key);
        return (V) values.get(key);
    }

    public String getPacketName() {
        return packetName;
    }

    @Override
    public final void marshall(Encoder encoder, Protocol protocol)
            throws Exception {

        encoder.writeInt32("size", values.size());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            encoder.writeString("key", entry.getKey());
            encoder.writeObject("value", entry.getValue());
        }
        marshall0(encoder, protocol);
    }

    @Override
    public final void unmarshall(Decoder decoder, Protocol protocol)
            throws Exception {

        int size = decoder.readInt32();
        for (int i = 0; i < size; i++) {
            String key = decoder.readString();
            Object value = decoder.readObject();
            values.put(key, value);
        }
        unmarshall0(decoder, protocol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Packet)) {
            return false;
        }

        Packet packet = (Packet) o;

        if (!packetName.equals(packet.packetName)) {
            return false;
        }
        if (!values.equals(packet.values)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = values.hashCode();
        result = 31 * result + packetName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Packet{" + "values=" + values + ", packetName='" + packetName + '\'' + '}';
    }

    protected void marshall0(Encoder encoder, Protocol protocol) {
    }

    protected void unmarshall0(Decoder decoder, Protocol protocol) {
    }

}
