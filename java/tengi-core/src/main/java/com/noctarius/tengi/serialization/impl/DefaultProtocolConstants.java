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
    public static final short SERIALIZED_TYPE_ENUM = -209;
    public static final short SERIALIZED_TYPE_ENUMERABLE = -209;

    public static final short TYPEID_PACKET = -1000;

}
