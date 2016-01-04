/// <reference path='../../../core/serialization/codec/Encoder.ts'/>
/// <reference path='../../../core/serialization/codec/Decoder.ts'/>
module com.noctarius.spi.serialization.codec {
    export interface Codec extends core.serialization.codec.Encoder, core.serialization.codec.Decoder {
    }
}