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

public class SynSegment
        extends Segment {

    private int version;
    private int maxSequence;
    private int optionalFlags;
    private int maxSegmentSize;
    private int rtxTimeout;
    private int cumAckTimeout;
    private int nullSegmentTimeout;
    private int maxRtx;
    private int maxCumAck;
    private int maxOutOfSequence;
    private int maxAutoRst;

    SynSegment(int sequence, int maxSequence, int optionalFlags, int maxSegmentSize, int rtxTimeout, int cumAckTimeout,
               int nullSegmentTimeout, int maxRtx, int maxCumAck, int maxOutOfSequence, int maxAutoRst) {

        super(RUDPConstants.PACKET_FLAG_SYN, sequence, RUDPConstants.SYN_HEADER_LENGTH);
        this.version = RUDPConstants.RUDP_VERSION;
        this.maxSequence = maxSequence;
        this.optionalFlags = optionalFlags;
        this.maxSegmentSize = maxSegmentSize;
        this.rtxTimeout = rtxTimeout;
        this.cumAckTimeout = cumAckTimeout;
        this.nullSegmentTimeout = nullSegmentTimeout;
        this.maxRtx = maxRtx;
        this.maxCumAck = maxCumAck;
        this.maxOutOfSequence = maxOutOfSequence;
        this.maxAutoRst = maxAutoRst;
    }

    public int version() {
        return version;
    }

    public int maxSequence() {
        return maxSequence;
    }

    public int optionalFlags() {
        return optionalFlags;
    }

    public int maxSegmentSize() {
        return maxSegmentSize;
    }

    public int rtxTimeout() {
        return rtxTimeout;
    }

    public int cumAckTimeout() {
        return cumAckTimeout;
    }

    public int nullSegmentTimeout() {
        return nullSegmentTimeout;
    }

    public int maxRtx() {
        return maxRtx;
    }

    public int maxCumAck() {
        return maxCumAck;
    }

    public int maxOutOfSequence() {
        return maxOutOfSequence;
    }

    public int maxAutoRst() {
        return maxAutoRst;
    }

    @Override
    void write(ByteBuf buffer) {
        super.write(buffer);
        buffer.writeByte(version << 4);
        buffer.writeByte(maxSequence);
        buffer.writeByte(optionalFlags);
        buffer.writeByte(0);
        buffer.writeShort(maxSegmentSize);
        buffer.writeShort(rtxTimeout);
        buffer.writeShort(cumAckTimeout);
        buffer.writeShort(nullSegmentTimeout);
        buffer.writeByte(maxRtx);
        buffer.writeByte(maxCumAck);
        buffer.writeByte(maxOutOfSequence);
        buffer.writeByte(maxAutoRst);
    }

}
