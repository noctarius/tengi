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
package com.noctarius.tengi.spi.serialization.codec.impl.utf8;

import com.noctarius.tengi.core.serialization.codec.Encoder;

abstract class AbstractBufferedUtfWriter implements UtfWriter {

    protected int buffering(byte[] buffer, int pos, byte value, Encoder encoder)
            throws Exception {

        try {
            buffer[pos] = value;
            return pos + 1;
        } catch (ArrayIndexOutOfBoundsException e) {
            // Array bounds check by programmatically is not needed like
            // "if (pos < buffer.length)".
            // JVM checks instead of us, so it is unnecessary.
            encoder.writeBytes("data", buffer, 0, buffer.length);
            buffer[0] = value;
            return 1;
        }
    }

}
