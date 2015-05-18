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
package com.noctarius.tengi.spi.serialization.codec.impl;

import com.noctarius.tengi.spi.buffer.ReadableMemoryBuffer;
import com.noctarius.tengi.spi.buffer.WritableMemoryBuffer;

final class BitSetCompressor {

    private static final byte BASE_CHUNK_SINGLE = (byte) (0b01000_000);
    private static final short BASE_CHUNK_DOUBLE = (short) (0b1000_0000_0000_0000);
    private static final int BASE_CHUNK_QUAD = 0b1100_0000_0000_0000_0000_0000_0000_0000;

    private static final byte MASK_CHUNK_TYPE_SINGLE = (byte) 0b0100_0000;
    private static final byte MASK_CHUNK_TYPE_DOUBLE = (byte) 0b1000_0000;
    private static final byte MASK_CHUNK_TYPE_QUAD = (byte) 0b1100_0000;

    private static final byte NULL_CHUNK = BASE_CHUNK_SINGLE;
    private static final byte MASK_NULL_CHUNK = (byte) 0b0011_1110;

    private static final int MASK_SIZE_SINGLE = 0b11;
    private static final int MASK_SIZE_DOUBLE = 0b111;
    private static final int MASK_SIZE_QUAD = 0b1111;

    private static final int SHIFT_SIZE_SINGLE = 4;
    private static final int SHIFT_SIZE_DOUBLE = 11;
    private static final int SHIFT_SIZE_QUAD = 26;

    private static final int MIN_SLOTS_SINGLE = 1;
    private static final int MAX_SLOTS_SINGLE = 3;

    private static final int MIN_SLOTS_DOUBLE = 4;
    private static final int MAX_SLOTS_DOUBLE = 10;

    private static final int MIN_SLOTS_QUAD = 11;
    private static final int MAX_SLOTS_QUAD = 25;

    private static final int MIN_SIZE_QUAD_CHUNK_SELECTOR = 14;

    static void writeBitSet(boolean[] value, WritableMemoryBuffer memoryBuffer) {
        int remainingSlots = value != null ? value.length : 0;
        do {
            ChunkType chunkType = selectChunkType(remainingSlots);

            // Null chunks can be handled immediately
            if (chunkType == ChunkType.Null) {
                memoryBuffer.writeByte(NULL_CHUNK);
                return;
            }

            // Calculate usable slots
            int useSlots = minSlots(remainingSlots, chunkType);

            // Need further chunks
            boolean furtherChunks = useSlots < remainingSlots;

            // Starting position inside the array
            int start = value.length - remainingSlots;

            // Write chunk
            switch (chunkType) {
                case Single:
                    writeSingleChunk(value, start, useSlots, furtherChunks, memoryBuffer);
                    break;

                case Double:
                    writeDoubleChunk(value, start, useSlots, furtherChunks, memoryBuffer);
                    break;

                default:
                    writeQuadChunk(value, start, useSlots, furtherChunks, memoryBuffer);
            }

            // Subtract written slots
            remainingSlots -= useSlots;
        } while (remainingSlots > 0);
    }

    static boolean[] readBitSet(ReadableMemoryBuffer memoryBuffer) {
        // Read first byte to find out about the chunk type
        int readerIndex = memoryBuffer.readerIndex();
        byte header = memoryBuffer.readByte();
        memoryBuffer.readerIndex(readerIndex);

        // Find chunk type
        ChunkType chunkType = findChunkType(header);

        // If NULL chunk, return here
        if (chunkType == ChunkType.Null) {
            return null;
        }

        // Read the current chunk
        int chunk = readChunk(chunkType, memoryBuffer);

        // Lookup if chunks are to follow up
        boolean furtherChunks = (chunk & 0x1) == 1;

        // Read the number of stored slots in this chunk
        int slots = readSlots(chunk, chunkType);

        // Read values
        boolean[] values = readValues(chunk, slots, chunkType);

        // If no chunks follow up return here
        if (!furtherChunks) {
            return values;
        }

        // Otherwise read further chunks
        boolean[] moreValues = readBitSet(memoryBuffer);

        // Combine data and return
        boolean[] combined = new boolean[values.length + moreValues.length];
        System.arraycopy(values, 0, combined, 0, values.length);
        System.arraycopy(moreValues, 0, combined, values.length, moreValues.length);

        return combined;
    }

    private static boolean[] readValues(int chunk, int slots, ChunkType chunkType) {
        boolean[] values = new boolean[slots];

        int shiftBase = getShiftBase(chunkType);
        for (int i = 0; i < slots; i++) {
            int shiftFactor = shiftBase - i;
            values[i] = ((chunk >> shiftFactor) & 0x1) == 1;
        }
        return values;
    }

    private static int getShiftBase(ChunkType chunkType) {
        switch (chunkType) {
            case Single:
                return SHIFT_SIZE_SINGLE - 1;

            case Double:
                return SHIFT_SIZE_DOUBLE - 1;

            default:
                return SHIFT_SIZE_QUAD - 1;
        }
    }

    private static int readSlots(int chunk, ChunkType chunkType) {
        switch (chunkType) {
            case Single:
                return ((chunk >> SHIFT_SIZE_SINGLE) & MASK_SIZE_SINGLE);

            case Double:
                return ((chunk >> SHIFT_SIZE_DOUBLE) & MASK_SIZE_DOUBLE) + MIN_SLOTS_DOUBLE - 1;

            default:
                return ((chunk >> SHIFT_SIZE_QUAD) & MASK_SIZE_QUAD) + MIN_SLOTS_QUAD - 1;
        }
    }

    private static int readChunk(ChunkType chunkType, ReadableMemoryBuffer memoryBuffer) {
        switch (chunkType) {
            case Single:
                return memoryBuffer.readByte();

            case Double:
                return ByteOrderUtils.getShort(memoryBuffer);

            default:
                return ByteOrderUtils.getInt(memoryBuffer);
        }
    }

    private static void writeQuadChunk(boolean[] value, int start, int useSlots, //
                                       boolean furtherChunks, WritableMemoryBuffer memoryBuffer) {

        int chunk = writeChunk(value, BASE_CHUNK_QUAD, start, useSlots, MIN_SLOTS_QUAD, SHIFT_SIZE_QUAD, furtherChunks);
        ByteOrderUtils.putInt(chunk, memoryBuffer);
    }

    private static void writeDoubleChunk(boolean[] value, int start, int useSlots, //
                                         boolean furtherChunks, WritableMemoryBuffer memoryBuffer) {

        int chunk = writeChunk(value, BASE_CHUNK_DOUBLE, start, useSlots, MIN_SLOTS_DOUBLE, SHIFT_SIZE_DOUBLE, furtherChunks);
        ByteOrderUtils.putShort((short) chunk, memoryBuffer);
    }

    private static void writeSingleChunk(boolean[] value, int start, int useSlots, //
                                         boolean furtherChunks, WritableMemoryBuffer memoryBuffer) {

        int chunk = writeChunk(value, BASE_CHUNK_SINGLE, start, useSlots, MIN_SLOTS_SINGLE, SHIFT_SIZE_SINGLE, furtherChunks);
        memoryBuffer.writeByte(chunk);
    }

    private static int writeChunk(boolean[] value, int chunk, int start, int useSlots, //
                                  int minSlots, int shiftBase, boolean furtherChunks) {

        // Mark follow up if necessary
        if (furtherChunks) {
            chunk |= 0x1;
        }

        // Write size
        chunk |= ((useSlots - minSlots + 1) << shiftBase);

        // Write slots
        for (int i = 0; i < useSlots; i++) {
            int index = start + i;
            int shift = shiftBase - i - 1;
            chunk |= ((value[index] ? 1 : 0) << shift);
        }
        return chunk;
    }

    private static int minSlots(int remainingSlots, ChunkType chunkType) {
        int maxSlots;
        switch (chunkType) {
            case Single:
                maxSlots = MAX_SLOTS_SINGLE;
                break;

            case Double:
                maxSlots = MAX_SLOTS_DOUBLE;
                break;

            default:
                maxSlots = MAX_SLOTS_QUAD;
        }
        return Math.min(maxSlots, remainingSlots);
    }

    private static ChunkType findChunkType(byte header) {
        if ((header & MASK_CHUNK_TYPE_QUAD) == MASK_CHUNK_TYPE_QUAD) {
            return ChunkType.Quad;
        } else if ((header & MASK_CHUNK_TYPE_DOUBLE) == MASK_CHUNK_TYPE_DOUBLE) {
            return ChunkType.Double;
        }
        if ((header & MASK_CHUNK_TYPE_SINGLE) == MASK_CHUNK_TYPE_SINGLE) {
            if ((header & MASK_NULL_CHUNK) == 0) {
                return ChunkType.Null;
            }
            return ChunkType.Single;
        }
        throw new IllegalStateException("Illegal chunk type detected");
    }

    private static ChunkType selectChunkType(int requiredSlots) {
        if (requiredSlots == 0) {
            return ChunkType.Null;
        } else if (requiredSlots <= MAX_SLOTS_SINGLE) {
            return ChunkType.Single;
        } else if (requiredSlots <= MAX_SLOTS_DOUBLE) {
            return ChunkType.Double;
        } else if (requiredSlots <= MIN_SIZE_QUAD_CHUNK_SELECTOR) {
            // Will first select a Double chunk and a Single chunk in the next round
            return ChunkType.Double;
        }
        return ChunkType.Quad;
    }

    private static enum ChunkType {
        Null,
        Single,
        Double,
        Quad
    }

    private BitSetCompressor() {
    }

}
