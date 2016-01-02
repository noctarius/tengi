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
package com.noctarius.tengi.spi.serialization.codec.impl.utf8;

import com.noctarius.tengi.core.serialization.codec.Encoder;

import java.io.UTFDataFormatException;

abstract class AbstractCharArrayUtfWriter
        extends AbstractBufferedUtfWriter {

    @Override
    public final void writeShortUTF(Encoder encoder, String value, int beginIndex, int endIndex, byte[] buffer)
            throws Exception {

        char[] characters = getCharArray(value);

        int i;
        int c;
        int bufferPos = 0;
        int utfLengthLimit;

        int utfLength = calculateUtf8Length(characters, beginIndex, endIndex);
        if (utfLength > 65535) {
            throw new UTFDataFormatException("encoded string too long:" + utfLength + " bytes");
        }

        utfLengthLimit = utfLength;

        encoder.writeShort("length", (short) utfLength);

        if (buffer.length >= utfLengthLimit) {
            for (i = beginIndex; i < endIndex; i++) {
                c = characters[i];
                if (!(c <= 0x007F && c >= 0x0001)) {
                    break;
                }
                buffer[bufferPos++] = (byte) c;
            }

            for (; i < endIndex; i++) {
                c = characters[i];
                if (c <= 0x007F && c >= 0x0001) {
                    buffer[bufferPos++] = (byte) c;
                } else if (c > 0x07FF) {
                    buffer[bufferPos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                    buffer[bufferPos++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                    buffer[bufferPos++] = (byte) (0x80 | ((c) & 0x3F));
                } else {
                    buffer[bufferPos++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                    buffer[bufferPos++] = (byte) (0x80 | ((c) & 0x3F));
                }
            }

            encoder.writeBytes("data", buffer, 0, bufferPos);
        } else {
            for (i = beginIndex; i < endIndex; i++) {
                c = characters[i];
                if (!(c <= 0x007F && c >= 0x0001)) {
                    break;
                }
                bufferPos = buffering(buffer, bufferPos, (byte) c, encoder);
            }

            for (; i < endIndex; i++) {
                c = characters[i];
                if (c <= 0x007F && c >= 0x0001) {
                    bufferPos = buffering(buffer, bufferPos, (byte) c, encoder);
                } else if (c > 0x07FF) {
                    bufferPos = buffering(buffer, bufferPos, (byte) (0xE0 | ((c >> 12) & 0x0F)), encoder);
                    bufferPos = buffering(buffer, bufferPos, (byte) (0x80 | ((c >> 6) & 0x3F)), encoder);
                    bufferPos = buffering(buffer, bufferPos, (byte) (0x80 | ((c) & 0x3F)), encoder);
                } else {
                    bufferPos = buffering(buffer, bufferPos, (byte) (0xC0 | ((c >> 6) & 0x1F)), encoder);
                    bufferPos = buffering(buffer, bufferPos, (byte) (0x80 | ((c) & 0x3F)), encoder);
                }
            }
            int length = bufferPos % buffer.length;
            encoder.writeBytes("data", buffer, 0, length == 0 ? buffer.length : length);
        }
    }

    protected abstract boolean isAvailable();

    protected abstract char[] getCharArray(String str);

    private int calculateUtf8Length(char[] value, int beginIndex, int endIndex) {
        int utfLength = 0;
        for (int i = beginIndex; i < endIndex; i++) {
            int c = value[i];
            if (c <= 0x007F && c >= 0x0001) {
                utfLength += 1;
            } else if (c > 0x07FF) {
                utfLength += 3;
            } else {
                utfLength += 2;
            }
        }
        return utfLength;
    }

}
