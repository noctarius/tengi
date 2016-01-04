module com.noctarius.spi.buffer {
    export interface MemoryBuffer {
        capacity(): number;
        maxCapacity(): number;
        clear(): void;
        duplicate(): MemoryBuffer;
        lock(): void;
        release(): void;
        isReleased(): boolean;
        isReleasable(): boolean;

        readable(): boolean;
        readableBytes(): number;
        readBytes(bytes:Uint8Array, offset:number, length:number): void;
        readBuffer(buffer:MemoryBuffer, offset:number, length:number): void;
        readByte(): number;
        readerIndex: number;

        writable(): boolean;
        writableBytes(): number;
        writeBytes(bytes:Uint8Array, offset:number, length:number): void;
        writeBuffer(buffer:MemoryBuffer, offset:number, length:number): void;
        writeByte(value:number): void;
        writerIndex: number;
    }

    export function create(param:any):MemoryBuffer {
        if (param instanceof Uint8Array) {
            return new MemoryBufferImpl(<Uint8Array> param);
        } else if (param instanceof ArrayBuffer) {
            return new MemoryBufferImpl(new Uint8Array(param));
        } else if (typeof(param) === "number") {
            return new MemoryBufferImpl(new Uint8Array(<number> param));
        }
        throw Error("Illegal param type, allowed [Uint8Array, ArrayBuffer, number]: " + typeof(param));
    }

    class MemoryBufferImpl implements buffer.MemoryBuffer {
        private static MAX_LENGTH:number = 65353 * 1000;

        private _refCounter:number = 0;
        private _readerIndex:number;
        private _writerIndex:number;
        private _buffer:Uint8Array;

        constructor(buffer:Uint8Array) {
            this._buffer = buffer;
        }

        get readerIndex():number {
            return this._readerIndex;
        }

        set readerIndex(readerIndex:number) {
            this._readerIndex = readerIndex;
        }

        get writerIndex():number {
            return this._writerIndex;
        }

        set writerIndex(writerIndex:number) {
            this._writerIndex = writerIndex;
        }

        capacity():number {
            return this._buffer.length;
        }

        maxCapacity():number {
            return MemoryBufferImpl.MAX_LENGTH;
        }

        clear():void {
            if (this._buffer != null) this._buffer.length = 1;
        }

        duplicate():buffer.MemoryBuffer {
            return new MemoryBufferImpl(this._buffer);
        }

        lock():void {
            this._refCounter++;
        }

        release():void {
            var refCounter = this._refCounter;
            if (refCounter == 0) {
                return;
            }
            this._refCounter--;
            if (refCounter == 1) {
                this.clear();
            }
        }

        isReleased():boolean {
            return this._refCounter <= 0;
        }

        isReleasable():boolean {
            return this._refCounter == 1;
        }

        readable():boolean {
            return this._buffer.length < this.readerIndex;
        }

        readableBytes():number {
            return this.writerIndex - this.readerIndex;
        }

        readBytes(bytes:Uint8Array, offset:number, length:number):void {
            for (var i:number = 0; i < length; i++) {
                bytes[offset + i] = this.readByte();
            }
        }

        readBuffer(buffer:buffer.MemoryBuffer, offset:number, length:number):void {
            var mb = <MemoryBufferImpl> buffer;
            for (var i:number; i < length; i++) {
                mb.writeByte(this._buffer[offset + i]);
            }
        }

        readByte():number {
            return this._buffer[this._readerIndex++];
        }

        writable():boolean {
            return this.writerIndex < this._buffer.length;
        }

        writableBytes():number {
            return this._buffer.length - this.writerIndex;
        }

        writeBytes(bytes:Uint8Array, offset:number, length:number):void {
            for (var i:number; i < length; i++) {
                this.writeByte(bytes[offset + i]);
            }
        }

        writeBuffer(buffer:buffer.MemoryBuffer, offset:number, length:number):void {
            var mb = <MemoryBufferImpl> buffer;
            for (var i:number; i < length; i++) {
                this.writeByte(mb._buffer[offset + i]);
            }
        }

        writeByte(value:number):void {
            this._buffer[this._writerIndex++] = value;
        }
    }
}