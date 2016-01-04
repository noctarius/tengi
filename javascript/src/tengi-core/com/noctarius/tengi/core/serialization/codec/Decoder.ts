module com.noctarius.core.serialization.codec {

    export interface Decoder {
        readBytes(bytes:Uint8Array): void;
        readBytes(bytes:Uint8Array, offset:number, length:number): void;
        readBoolean(): boolean;
        readBitSet(): Array<boolean>;
        readByte(): number;
        readUnsignedByte(): number;
        readShort(): number;
        readChar(): number;
        readInt32(): number;
        readCompressedInt32(): number;
        readInt64(): number;
        readCompressedInt64(): number;
        readFloat(): number;
        readDouble(): number;
        readString(): string;
        readObject(): any;
        readNullableObject(): any;
    }
}