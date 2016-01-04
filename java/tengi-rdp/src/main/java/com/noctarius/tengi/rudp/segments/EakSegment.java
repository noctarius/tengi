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
package com.noctarius.tengi.rudp.segments;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public class EakSegment
        extends AckSegment {

    private short[] acks;

    EakSegment(int sequence, int ack, short[] acks) {
        super(RUDPConstants.PACKET_FLAG_EAK, sequence, ack, RUDPConstants.RUDP_HEADER_LENGTH + acks.length);
        this.acks = acks;
    }

    public short[] acks() {
        return Arrays.copyOf(acks, acks.length);
    }

    @Override
    void write(ByteBuf buffer) {
        super.write(buffer);
        for (int i = 0; i < acks.length; i++) {
            buffer.writeByte(acks[i]);
        }
    }

}
