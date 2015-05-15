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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Supplier;

class SysOutLogger
        implements Logger {

    private final String prefix;

    public SysOutLogger(String prefix) {
        this.prefix = prefix;
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
            System.arraycopy(args, 0, params, 4, args.length);
            return String.format(message, params);
        });
    }

    private void log(Level level, Throwable throwable, Supplier<String> supplier) {
        switch (level) {
            case Trace: {
                String logMsg = supplier.get();
                System.out.println(fixMessage(level, logMsg, throwable));
                break;
            }

            case Debug: {
                String logMsg = supplier.get();
                System.out.println(fixMessage(level, logMsg, throwable));
                break;
            }

            case Warning: {
                String logMsg = supplier.get();
                System.err.println(fixMessage(level, logMsg, throwable));
                break;
            }

            case Fatal: {
                String logMsg = supplier.get();
                System.err.println(fixMessage(level, logMsg, throwable));
                break;
            }

            case Info:
            default: {
                String logMsg = supplier.get();
                System.out.println(fixMessage(level, logMsg, throwable));
                break;
            }
        }
    }

    private String fixMessage(Level level, String msg, Throwable throwable) {
        StringBuilder sb = new StringBuilder(level.name()).append(" [").append(prefix).append("]: ").append(msg);
        if (throwable == null) {
            return sb.toString();
        }

        StringWriter writer = new StringWriter();
        PrintWriter pipe = new PrintWriter(writer);
        throwable.printStackTrace(pipe);

        return sb.append("\n").append(writer.toString()).toString();
    }

}
