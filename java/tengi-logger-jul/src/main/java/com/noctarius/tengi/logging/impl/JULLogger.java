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
package com.noctarius.tengi.logging.impl;

import com.noctarius.tengi.logging.Level;
import com.noctarius.tengi.logging.Logger;

import java.util.function.Supplier;

class JULLogger
        implements Logger {

    private final java.util.logging.Logger logger;

    JULLogger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(Level level, Throwable throwable, String message) {
        log(level, throwable, () -> message);
    }

    @Override
    public void log(Level level, Throwable throwable, String message, Object arg) {
        log(level, throwable, () -> String.format(message, arg));
    }

    @Override
    public void log(Level level, Throwable throwable, String message, Object arg1, Object arg2) {
        log(level, throwable, () -> String.format(message, arg1, arg2));
    }

    @Override
    public void log(Level level, Throwable throwable, String message, Object arg1, Object arg2, Object arg3) {
        log(level, throwable, () -> String.format(message, arg1, arg2, arg3));
    }

    @Override
    public void log(Level level, Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(level, throwable, () -> {
            Object[] params = new Object[args.length + 3];
            params[0] = arg1;
            params[1] = arg2;
            params[2] = arg3;
            System.arraycopy(args, 0, params, 3, args.length);
            return String.format(message, params);
        });
    }

    private void log(Level level, Throwable throwable, Supplier<String> supplier) {
        switch (level) {
            case Trace:
                if (logger.isLoggable(java.util.logging.Level.FINEST)) {
                    String logMsg = supplier.get();
                    logger.log(java.util.logging.Level.FINEST, logMsg, throwable);
                }
                break;

            case Debug:
                if (logger.isLoggable(java.util.logging.Level.FINE)) {
                    String logMsg = supplier.get();
                    logger.log(java.util.logging.Level.FINE, logMsg, throwable);
                }
                break;

            case Warning:
                if (logger.isLoggable(java.util.logging.Level.WARNING)) {
                    String logMsg = supplier.get();
                    logger.log(java.util.logging.Level.WARNING, logMsg, throwable);
                }
                break;

            case Fatal:
                if (logger.isLoggable(java.util.logging.Level.SEVERE)) {
                    String logMsg = supplier.get();
                    logger.log(java.util.logging.Level.SEVERE, logMsg, throwable);
                }
                break;

            case Info:
            default:
                if (logger.isLoggable(java.util.logging.Level.INFO)) {
                    String logMsg = supplier.get();
                    logger.log(java.util.logging.Level.INFO, logMsg, throwable);
                }
        }
    }

}
