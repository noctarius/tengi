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
package com.noctarius.tengi.core.exception;

/**
 * This exception class is thrown whenever a configured
 * {@link com.noctarius.tengi.core.connection.Transport} is not available to be
 * used on either client or serve side.
 */
public class IllegalTransportException
        extends SystemException {

    /**
     * Constructs a new instance with a given message.
     *
     * @param message the message of the exception
     */
    public IllegalTransportException(String message) {
        super(message);
    }

}
