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

public enum SegmentSerializer {

    INSTANCE;

    public <S extends Segment> void writeSegment(S segment, ByteBuf buffer) {
        segment.write(buffer);
    }

    public <S extends Segment> S readSegment(ByteBuf buffer) {
        if (buffer.readableBytes() < RUDPConstants.RUDP_HEADER_LENGTH) {
            throw new IllegalArgumentException("Invalid segment");
        }

        int flags = buffer.getUnsignedByte(0);
        int length = buffer.getUnsignedByte(1);
        int sequence = buffer.getUnsignedByte(2);
        int ack = buffer.getUnsignedByte(3);

        RUDPConstants.SegmentType segmentType = segmentType(flags, length);
        switch (segmentType) {
            case SYN:
                return readSynSegment(buffer, sequence);

            case NUL:
                return readNulSegment(sequence);

            case EAK:
                return readEakSegment(buffer, length, sequence, ack);

            case RST:
                return readRstSegment(sequence);

            case FIN:
                return readFinSegment(sequence);

            case ACK:
                return readAckSegment(sequence, ack);

            case DAT:
                return readDatSegment(buffer, length, sequence, ack);

            default:
                throw new IllegalStateException("Illegal segment found");
        }
    }

    private <S extends Segment> S readSynSegment(ByteBuf buffer, int sequence) {
        short value = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH);
        int version = value >>> 4;
        if (version != RUDPConstants.RUDP_VERSION) {
            throw new IllegalStateException("Illegal RUDP version");
        }

        int maxSequence = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH + 1);
        int optionalFlags = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH + 2);
        int maxSegmentSize = buffer.getUnsignedShort(RUDPConstants.RUDP_HEADER_LENGTH + 4);
        int rtxTimeout = buffer.getUnsignedShort(RUDPConstants.RUDP_HEADER_LENGTH + 6);
        int cumAckTimeout = buffer.getUnsignedShort(RUDPConstants.RUDP_HEADER_LENGTH + 8);
        int nullSegmentTimeout = buffer.getUnsignedShort(RUDPConstants.RUDP_HEADER_LENGTH + 10);
        int maxRtx = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH + 12);
        int maxCumAck = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH + 13);
        int maxOutOfSequence = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH + 14);
        int maxAutoRst = buffer.getUnsignedByte(RUDPConstants.RUDP_HEADER_LENGTH + 15);

        return (S) new SynSegment(sequence, maxSequence, optionalFlags, maxSegmentSize, rtxTimeout, cumAckTimeout,
                nullSegmentTimeout, maxRtx, maxCumAck, maxOutOfSequence, maxAutoRst);
    }

    private <S extends Segment> S readEakSegment(ByteBuf buffer, int length, int sequence, int ack) {
        short[] acks = new short[RUDPConstants.RUDP_HEADER_LENGTH - length];
        for (int i = 0; i < acks.length; i++) {
            acks[i] = buffer.getUnsignedByte(i + RUDPConstants.RUDP_HEADER_LENGTH);
        }
        return (S) new EakSegment(sequence, ack, acks);
    }

    private <S extends Segment> S readDatSegment(ByteBuf buffer, int length, int sequence, int ack) {
        ByteBuf content = buffer.copy(RUDPConstants.RUDP_HEADER_LENGTH, length - RUDPConstants.RUDP_HEADER_LENGTH);
        return (S) new DatSegment(sequence, ack, content);
    }

    private <S extends Segment> S readNulSegment(int sequence) {
        return (S) new NulSegment(sequence);
    }

    private <S extends Segment> S readFinSegment(int sequence) {
        return (S) new FinSegment(sequence);
    }

    private <S extends Segment> S readRstSegment(int sequence) {
        return (S) new RstSegment(sequence);
    }

    private <S extends Segment> S readAckSegment(int sequence, int ack) {
        return (S) new AckSegment(sequence, ack);
    }

    static RUDPConstants.SegmentType segmentType(Segment segment) {
        return segmentType(segment.flags(), segment.length());
    }

    static RUDPConstants.SegmentType segmentType(int flags, int length) {
        if ((flags & RUDPConstants.PACKET_FLAG_SYN) != 0) {
            return RUDPConstants.SegmentType.SYN;

        } else if ((flags & RUDPConstants.PACKET_FLAG_NUL) != 0) {
            return RUDPConstants.SegmentType.NUL;

        } else if ((flags & RUDPConstants.PACKET_FLAG_EAK) != 0) {
            return RUDPConstants.SegmentType.EAK;

        } else if ((flags & RUDPConstants.PACKET_FLAG_RST) != 0) {
            return RUDPConstants.SegmentType.RST;

        } else if ((flags & RUDPConstants.PACKET_FLAG_FIN) != 0) {
            return RUDPConstants.SegmentType.FIN;

        } else if ((flags & RUDPConstants.PACKET_FLAG_ACK) != 0) {
            if (length == RUDPConstants.RUDP_HEADER_LENGTH) {
                return RUDPConstants.SegmentType.ACK;
            }
            return RUDPConstants.SegmentType.DAT;
        }
        throw new IllegalArgumentException("Illegal segment type");
    }

}
