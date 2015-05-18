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
package com.noctarius.tengi.core.exception;

/**
 * This exception class is the root exception of all specific tengi
 * exception classes and also acts as a generic base exception in case
 * of any kind of exception needs to be wrapped.
 */
public class SystemException
        extends RuntimeException {

    /**
     * Constructs a new instance with a given message.
     *
     * @param message the message of the exception
     */
    public SystemException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance with a given root cause.
     *
     * @param cause the root cause of the exception
     */
    public SystemException(Throwable cause) {
        super(cause);
    }

}
