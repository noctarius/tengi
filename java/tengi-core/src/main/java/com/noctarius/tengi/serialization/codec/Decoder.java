package com.noctarius.tengi.serialization.codec;

import com.noctarius.tengi.buffer.ReadableMemoryBuffer;

public interface Decoder {

    int readBytes(byte[] bytes);

    int readBytes(byte[] bytes, int offset, int length);

    boolean readBoolean();

    boolean[] readBoolArray();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    char readChar();

    int readInt();

    long readLong();

    float readFloat();

    double readDouble();

    String readString();

    <O> O readObject()
            throws Exception;

    <O> O readNullableObject()
            throws Exception;

    ReadableMemoryBuffer getReadableMemoryBuffer();

}
