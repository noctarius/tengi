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
package com.noctarius.tengi.core.impl;

import com.noctarius.tengi.core.exception.SystemException;

public final class ExceptionUtil {

    public static RuntimeException rethrow(Exception e) {
        if (e instanceof SystemException) {
            throw (SystemException) e;
        }
        SystemException exception = new SystemException(e);
        StackTraceElement[] stackTrace = exception.getStackTrace();
        StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 1];
        System.arraycopy(stackTrace, 1, newStackTrace, 0, stackTrace.length - 1);
        exception.setStackTrace(newStackTrace);
        throw exception;
    }

    private ExceptionUtil() {
    }
}
