/*
 * Copyright (c) 2015, Christoph Engelbert (aka noctarius) and
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
package com.noctarius.tengi.serialization.impl;

public interface DefaultProtocolConstants {

    public static final String TYPE_MANIFEST_FILENAME = "META-INF/tengi/type.manifest";
    public static final String TYPE_DEFAULT_MANIFEST_FILENAME = "META-INF/tengi/type.default.manifest";

    public static final String PROTOCOL_MIME_TYPE = "application/tengi";

    public static final byte[] PROTOCOL_MAGIC_HEADER = {'T', 'e', 'N', 'g', 'I'};

    public static final short SERIALIZED_TYPE_BYTE = -1;
    public static final short SERIALIZED_TYPE_SHORT = -2;
    public static final short SERIALIZED_TYPE_CHAR = -3;
    public static final short SERIALIZED_TYPE_INTEGER = -4;
    public static final short SERIALIZED_TYPE_FLOAT = -5;
    public static final short SERIALIZED_TYPE_LONG = -6;
    public static final short SERIALIZED_TYPE_DOUBLE = -7;
    public static final short SERIALIZED_TYPE_STRING = -8;
    public static final short SERIALIZED_TYPE_BYTE_ARRAY = -9;
    public static final short SERIALIZED_TYPE_ENUM = -10;
    public static final short SERIALIZED_TYPE_ENUMERABLE = -11;

    public static final short SERIALIZED_TYPE_IDENTIFIER = -101;
    public static final short SERIALIZED_TYPE_MESSAGE = -102;
    public static final short SERIALIZED_TYPE_PACKET = -103;
    public static final short SERIALIZED_TYPE_MARSHALLABLE = -104;

    public static final short TYPEID_PACKET = -1000;

    public static final short TYPEID_HANDSHAKE_REQUEST = -201;
    public static final short TYPEID_HANDSHAKE_RESPONSE = -202;
    public static final short TYPEID_LONG_POLLING_REQUEST = -203;
    public static final short TYPEID_LONG_POLLING_RESPONSE = -204;

}
