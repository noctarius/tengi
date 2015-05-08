package com.noctarius.tengi.serialization.codec;

import com.noctarius.tengi.buffer.WritableMemoryBuffer;

public interface Encoder {

    void writeBytes(String fieldName, byte[] bytes);

    void writeBytes(String fieldName, byte[] bytes, int offset, int length);

    void writeBoolean(String fieldName, boolean value);

    void writeBitSet(String fieldName, boolean[] values);

    void writeByte(String fieldName, int value);

    void writeUnsignedByte(String fieldName, short value);

    void writeShort(String fieldName, short value);

    void writeChar(String fieldName, char value);

    void writeInt32(String fieldName, int value);

    void writeCompressedInt32(String fieldName, int value);

    void writeInt64(String fieldName, long value);

    void writeCompressedInt64(String fieldName, long value);

    void writeFloat(String fieldName, float value);

    void writeDouble(String fieldName, double value);

    void writeString(String fieldName, String value);

    void writeObject(String fieldName, Object object)
            throws Exception;

    void writeNullableObject(String fieldName, Object object)
            throws Exception;

    WritableMemoryBuffer getWritableMemoryBuffer();

}
