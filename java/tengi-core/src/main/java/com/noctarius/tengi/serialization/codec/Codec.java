package com.noctarius.tengi.serialization.codec;

import com.noctarius.tengi.serialization.Protocol;

public interface Codec
        extends Decoder, Encoder {

    Protocol getProtocol();

}
