package com.noctarius.tengi.serialization.impl;

public interface DefaultProtocolConstants {

    public static final String TYPE_MANIFEST_FILENAME = "META-INF/tengi/type.manifest";
    public static final String TYPE_DEFAULT_MANIFEST_FILENAME = "META-INF/tengi/type.default.manifest";

    public static final String PROTOCOL_MIME_TYPE = "application/tengi";

    public static final byte[] PROTOCOL_MAGIC_HEADER = {'T', 'e', 'N', 'g', 'I'};

    public static final short SERIALIZED_TYPE_PACKET = -101;
    public static final short SERIALIZED_TYPE_MARSHALLABLE = -102;

    public static final short SERIALIZED_TYPE_MESSAGE = -301;
    public static final short SERIALIZED_TYPE_IDENTIFIER = -302;

    public static final short SERIALIZED_TYPE_BYTE = -201;
    public static final short SERIALIZED_TYPE_SHORT = -202;
    public static final short SERIALIZED_TYPE_INTEGER = -203;
    public static final short SERIALIZED_TYPE_LONG = -204;
    public static final short SERIALIZED_TYPE_FLOAT = -205;
    public static final short SERIALIZED_TYPE_DOUBLE = -206;
    public static final short SERIALIZED_TYPE_STRING = -207;
    public static final short SERIALIZED_TYPE_BYTE_ARRAY = -208;

    public static final short TYPEID_PACKET = -1000;
    public static final short TYPEID_MESSAGE = -1001;

}
