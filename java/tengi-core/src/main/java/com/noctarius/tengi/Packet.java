package com.noctarius.tengi;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.buffer.WritableMemoryBuffer;
import com.noctarius.tengi.serialization.Marshallable;
import com.noctarius.tengi.serialization.Protocol;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;
import com.noctarius.tengi.utils.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public final void marshall(WritableMemoryBuffer memoryBuffer, Protocol protocol) {
        memoryBuffer.writeInt(values.size());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            memoryBuffer.writeString(entry.getKey());
            memoryBuffer.writeObject(entry.getValue(), protocol);
        }
    }

    @Override
    public final void unmarshall(ReadableMemoryBuffer memoryBuffer, Protocol protocol) {
        int size = memoryBuffer.readInt();
        for (int i = 0; i < size; i++) {
            String key = memoryBuffer.readString();
            Objects value = memoryBuffer.readObject(protocol);
            values.put(key, value);
        }
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
}
