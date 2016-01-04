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
package com.noctarius.tengi.rudp.nio;

import com.noctarius.tengi.rudp.segments.DatSegment;
import com.noctarius.tengi.rudp.segments.FinSegment;
import com.noctarius.tengi.rudp.segments.RstSegment;
import com.noctarius.tengi.rudp.segments.Segment;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

import java.util.List;

public class NioReliableChannel
        extends AbstractReliableChannel {

    private static final int MAX_SEQUENCE_NUMBER = 255;

    private volatile ChannelState channelState;

    @Override
    protected boolean handleSegment(Segment segment, List<Object> buf) {
        if (segment instanceof DatSegment) {
            ByteBuf content = ((DatSegment) segment).content();
            buf.add(content);
            return true;

        } else if (segment instanceof FinSegment) {
            return true;

        } else if (segment instanceof RstSegment) {
            return true;

        }
        return false;
    }

    @Override
    protected DatagramPacket encodeSegment() {
        return null;
    }

    private int compareSequenceNumbers(int seqn1, int seqn2) {
        if (seqn1 == seqn2) {
            return 0;
        } else if (((seqn1 < seqn2) && ((seqn2 - seqn1) > MAX_SEQUENCE_NUMBER / 2)) //
                || ((seqn1 > seqn2) && ((seqn1 - seqn2) < MAX_SEQUENCE_NUMBER / 2))) {
            return 1;
        } else {
            return -1;
        }
    }

    private int nextSequenceNumber(int sequenceNumber) {
        return (sequenceNumber + 1) % MAX_SEQUENCE_NUMBER;
    }

    private static enum ChannelState {
        CLOSED,
        SYN_RCVD,
        SYN_SENT,
        ESTABLISHED,
        CLOSE_WAIT
    }

}
