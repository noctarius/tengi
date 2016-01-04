package com.noctarius.tengi.spi.buffer;

import haxe.io.Error;
import java.lang.Integer;
import haxe.Int32;

class MemoryBuffer {

    private var buffer:haxe.io.Bytes;

    private var released:Bool = false;
    private var refCount:Int32 = 1;

    public var readerIndex(default, default):Int32;
    public var writerIndex(default, default):Int32;

    public static function build(buffer:haxe.io.Bytes) {
        return new MemoryBuffer(buffer);
    }

    private function new(buffer:haxe.io.Bytes, writerIndex:Int32 = 0, readerIndex:Int32 = 0) {
        this.buffer = buffer;
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }

    public function capacity():Int32 {
        return buffer.length;
    }

    public function maxCapacity():Int32 {
        return Integer.MAX_VALUE;
    }

    public function clear():Void {
        buffer.fill(0, writerIndex, 0);
        writerIndex = 0;
        readerIndex = 0;
    }

    public function duplicate():MemoryBuffer {
        return new MemoryBuffer(buffer, writerIndex);
    }

    public function lock():Void {
        if (refCount == 0) {
// TODO
            throw new Error();
        }
        if (refCount == Integer.MAX_VALUE) {
            throw new Error();
        }
        refCount++;
    }

    public function release():Void {
        if (refCount == 0) {
// TODO
            throw new Error();
        }
        if (refCount == Integer.MAX_VALUE) {
            throw new Error();
        }
        refCount--;
        if (refCount == 1) {
            released = true;
        }
    }

    public function isReleased():Bool {
        return released;
    }

    public function isReleasable():Bool {
        refCount == 0;
    }

    public function readable():Bool {
        return writerIndex > readerIndex;
    }

    public function readableBytes():Int32 {
        return writerIndex - readerIndex;
    }

    public function readBytes(bytes:haxe.io.UInt8Array, offset:Int32 = 0, length:Int32 = -1):Void {
        length = length(length, bytes.length);
        for (i in offset...length) {
            bytes[i] = readByte();
        }
    }

    public function readBytesBuffer(buffer:haxe.io.BytesBuffer, offset:Int32 = 0, length:Int32 = -1):Void {
        length = length(length, buffer.length);
        for (i in offset...length) {
            buffer[i] = readByte();
        }
    }

    public function readMemoryBuffer(buffer:MemoryBuffer, offset:Int32 = 0, length:Int32 = -1):Void {
        length = length(length, buffer.length);
        var writerIndex:Int32 = buffer.writerIndex;
        for (i in offset...length) {
            buffer[i] = readByte();
        }
        buffer.writerIndex
    }

    public function readByte():Int {
        return this[readerIndex++];
    }

    @:arrayAccess public inline function read(index:Int):Int {
        return buffer[index];
    }

    public function writable():Bool {
        return capacity() > writerIndex;
    }

    public function writableBytes():Int32 {
        return capacity() - writerIndex;
    }

    public function writeBytes(bytes:haxe.io.UInt8Array, offset:Int32, length:Int32):Void {
        length = length(length, bytes.length);
    }

    public function writeBytesBuffer(buffer:haxe.io.BytesBuffer, offset:Int32, length:Int32):Void {
        length = length(length, bytes.length);
    }

    public function writeMemoryBuffer(buffer:MemoryBuffer, offset:Int32 = 0, length:Int32 = -1):Void {
        length = length(length, bytes.length);

    }

    public function writeByte(value:Int):Void {
        buffer.set(writerIndex++, value);
    }

    @:arrayAccess public inline function write(index:Int, value:Int):Void {
        buffer.set(index, value);
    }

    private function length(length:Int32, lengthFallback:Int32) {
        return length == -1 ? lengthFallback : length;
    }
}
