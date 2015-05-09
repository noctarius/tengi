package com.noctarius.tengi.connection.impl;

import com.noctarius.tengi.Packet;
import com.noctarius.tengi.serialization.TypeId;
import com.noctarius.tengi.serialization.impl.DefaultProtocolConstants;

@TypeId(DefaultProtocolConstants.TYPEID_HANDSHAKE_RESPONSE)
public class HandshakeResponse
        extends Packet {

    public HandshakeResponse() {
        super("HandshakeResponse");
    }
}
