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
package com.noctarius.tengi.spi.logging.impl;

import com.noctarius.tengi.spi.logging.Level;
import com.noctarius.tengi.spi.logging.Logger;

import java.util.function.Supplier;

class Log4jV2Logger
        implements Logger {

    private final org.apache.logging.log4j.Logger logger;

    Log4jV2Logger(org.apache.logging.log4j.Logger logger) {
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
                if (logger.isTraceEnabled()) {
                    String logMsg = supplier.get();
                    logger.trace(logMsg, throwable);
                }
                break;

            case Debug:
                if (logger.isDebugEnabled()) {
                    String logMsg = supplier.get();
                    logger.debug(logMsg, throwable);
                }
                break;

            case Warning:
                if (logger.isWarnEnabled()) {
                    String logMsg = supplier.get();
                    logger.warn(logMsg, throwable);
                }
                break;

            case Fatal:
                if (logger.isErrorEnabled()) {
                    String logMsg = supplier.get();
                    logger.error(logMsg, throwable);
                }
                break;

            case Info:
            default:
                if (logger.isInfoEnabled()) {
                    String logMsg = supplier.get();
                    logger.info(logMsg, throwable);
                }
        }
    }

}
