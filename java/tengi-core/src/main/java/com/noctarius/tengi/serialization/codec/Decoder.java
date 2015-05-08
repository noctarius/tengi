package com.noctarius.tengi.serialization.codec;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;

public interface Decoder {

    int readBytes(byte[] bytes);

    int readBytes(byte[] bytes, int offset, int length);

    boolean readBoolean();

    boolean[] readBitSet();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    char readChar();

    int readInt32();

    int readCompressedInt32();

    long readInt64();

    long readCompressedInt64();

    float readFloat();

    double readDouble();

    String readString();

    <O> O readObject()
            throws Exception;

    <O> O readNullableObject()
            throws Exception;

    ReadableMemoryBuffer getReadableMemoryBuffer();

}
