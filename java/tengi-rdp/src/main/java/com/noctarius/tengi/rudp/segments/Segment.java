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

public abstract class Segment {

    private int flags;
    private int length;
    private int sequence;
    private int ack;
    private int rtxCounter;

    protected Segment(int flags, int sequence, int length) {
        this.flags = flags;
        this.sequence = sequence;
        this.length = length;
    }

    protected Segment(int flags, int sequence, int ack, int length) {
        this.flags = flags;
        this.sequence = sequence;
        this.ack = ack;
        this.length = length;
    }

    public int flags() {
        return flags;
    }

    public int sequence() {
        return sequence;
    }

    public int ack() {
        if ((flags & RUDPConstants.PACKET_FLAG_ACK) == RUDPConstants.PACKET_FLAG_ACK) {
            return ack;
        }
        return -1;
    }

    public int length() {
        return length;
    }

    public int rtxCounter() {
        return rtxCounter;
    }

    public void rtxCounter(int rtxCounter) {
        this.rtxCounter = rtxCounter;
    }

    public RUDPConstants.SegmentType type() {
        return SegmentSerializer.segmentType(this);
    }

    void write(ByteBuf buffer) {
        buffer.writeByte(flags);
        buffer.writeByte(length);
        buffer.writeByte(sequence);
        buffer.writeByte(ack);
    }

    public String toString() {
        return getClass().getName() + " [" + " SEQ = " + sequence() + ", ACK = " + ((ack() >= 0) ? "" + ack() : "N/A") +
                ", LEN = " + length() + " ]";
    }

}
