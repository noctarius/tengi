module com.noctarius.core.serialization.codec {
    export interface Encoder {
        writeBytes(bytes:Uint8Array): void;
        writeBytes(bytes:Uint8Array, offset:number, length:number): void;
        writeBoolean(value:boolean): void;
        writeBitSet(values:Array<boolean>): void;
        writeByte(value:number): void;
        writeUnsignedByte(value:number): void;
        writeShort(value:number): void;
        writeChar(value:number): void;
        writeInt32(value:number): void;
        writeCompressedInt32(value:number): void;
        writeInt64(value:number): void;
        writeCompressedInt64(value:number): void;
        writeFloat(value:number): void;
        writeDouble(value:number): void;
        writeString(value:string): void;
        writeObject(value:any): void;
        writeNullableObject(value:any): void;
    }
}