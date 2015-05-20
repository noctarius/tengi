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
import com.noctarius.tengi.core.serialization.Marshallable;
import com.noctarius.tengi.core.serialization.TypeId;
import com.noctarius.tengi.core.serialization.codec.Decoder;
import com.noctarius.tengi.core.serialization.codec.Encoder;
import com.noctarius.tengi.spi.serialization.Protocol;
import com.noctarius.tengi.spi.serialization.impl.DefaultProtocolConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>The <tt>Packet</tt> class is a convenient way to design network packets which
 * can either be used directly or by subclassing it.</p>
 * <pre>
 *   Packet packet = new Packet("login");
 *   packet.setValue("username", "myUserName");
 *   packet.setValue("password", hash("myPassword"));
 *   packet.setValue("metadata", "some dynamic value");
 *   connection.writeObject(packet);
 * </pre>
 * <p>By default all properties, defined with {@link #setValue(String, Object)}, are
 * serialized as objects which can be quiet inefficient, by subclassing the Packet
 * instance, serialization for known properties might be optimized. That way fixed
 * properties and dynamic properties can be combined.</p>
 * <pre>
 *   &#64;TypeId(12345)
 *   public class LoginPacket extends Packet {
 *     private String username;
 *     private String password;
 *
 *     public LoginPacket() {
 *       super("LoginPacket");
 *     }
 *
 *     public void setUsername(String username) {
 *       notNull("username", username);
 *       this.username = username;
 *     }
 *     public String getUsername() { return username; }
 *
 *     public void setPassword(String password) {
 *       notNull("password", password);
 *       this.password = password;
 *     }
 *     public String getPassword() { return password; }
 *
 *     protected void marshall0(Encoder encoder, Protocol protocol) {
 *       encoder.writeString(username);
 *       encoder.writeString(password);
 *     }
 *
 *     protected void unmarshall0(Decoder decoder, Protocol protocol) {
 *       username = decoder.readString();
 *       password = decoder.readString();
 *     }
 *   }
 *
 *   LoginPacket packet = new LoginPacket();
 *   packet.setUsername("myUserName");
 *   packet.setPassword(hash("myPassword"));
 *   packet.setValue("metadata", "some dynamic value");
 *   connection.writeObject(packet);
 * </pre>
 * <p><tt>Packet</tt> subclasses need to register their own typeid when using
 * the default protocol to be uniquely identified. This can either be achieved
 * by annotating the class with {@link com.noctarius.tengi.core.serialization.TypeId}
 * or by implementing {@link com.noctarius.tengi.core.serialization.Identifiable}
 * interface. A special {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
 * is also an option to completely adopt the default serialization but it is not recommended
 * to use a <tt>Packet</tt> subclass in this case.</p>
 * <p>For more information on custom serialization please see
 * {@link com.noctarius.tengi.core.serialization.codec.Encoder},
 * {@link com.noctarius.tengi.core.serialization.codec.Decoder} and
 * {@link com.noctarius.tengi.spi.serialization.Protocol}.</p>
 */
@TypeId(DefaultProtocolConstants.TYPEID_PACKET)
public class Packet
        implements Marshallable {

    private final Map<String, Object> values = new HashMap<>();
    private String packetName;

    /**
     * Creates a new <tt>Packet</tt> instance with the given packet name. The name is
     * not used internally but can be used by user-code to distinguish different logic
     * based on its given name.
     *
     * @param packetName the given name of the packet to distinguish different types of packets
     */
    public Packet(String packetName) {
        Validate.notNull("packageName", packetName);
        this.packetName = packetName;
    }

    /**
     * Assigns a key to a value. The value must be a of a serializable type, otherwise serialization
     * will fail with an exception while sending the packet. Both key and value must be non-null,
     * otherwise a {@link java.lang.NullPointerException} is thrown. If called with an already existing
     * key, any previously assigned value to the key will be overridden.
     *
     * @param key   the key to assign the value to
     * @param value the assigned value
     * @param <V>   the type of the value
     * @throws java.lang.NullPointerException whenever key or value are null
     */
    public <V> void setValue(String key, V value) {
        Validate.notNull("key", key);
        Validate.notNull("value", value);
        values.put(key, value);
    }

    /**
     * <p>Retrieves a value from the internal value set assigned to the given key. If the key doesn't have
     * an assigned value <tt>null</tt> is returned. The given key must be non-null, otherwise a
     * {@link java.lang.NullPointerException} is thrown.</p>
     * <p>The value is implicitly casted to the type of the assignment and might throw a
     * {@link java.lang.ClassCastException} if the retrieve value and the assignment type are not assignable
     * to each other.</p>
     *
     * @param key the key to retrieve its assigned value
     * @param <V> the implicit type of the value
     * @return the assigned value, if exists, otherwise null
     * @throws java.lang.NullPointerException whenever key is null
     * @throws java.lang.ClassCastException   whenever the returned value is not assignment compatible to the assignment type
     */
    public <V> V getValue(String key) {
        Validate.notNull("key", key);
        return (V) values.get(key);
    }

    /**
     * Returns the packet's name which was given while creating the packet.
     *
     * @return the packet's name
     */
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

    /**
     * This method can be used in subclasses to optimize the internal serialization of the packet.
     *
     * @param encoder  the encoder to write values to
     * @param protocol the protocol to write additional type information
     */
    protected void marshall0(Encoder encoder, Protocol protocol) {
    }

    /**
     * This method can be used in subclasses to optimize the internal deserialization of the packet.
     *
     * @param decoder  the decoder to read values from
     * @param protocol the protocol to read additional type information
     */
    protected void unmarshall0(Decoder decoder, Protocol protocol) {
    }

}
