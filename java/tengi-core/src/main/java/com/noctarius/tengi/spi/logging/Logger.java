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
package com.noctarius.tengi.spi.logging;

public interface Logger {

    void log(Level level, Throwable throwable, String message);

    void log(Level level, Throwable throwable, String message, Object arg);

    void log(Level level, Throwable throwable, String message, Object arg1, Object arg2);

    void log(Level level, Throwable throwable, String message, Object arg1, Object arg2, Object arg3);

    void log(Level level, Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args);

    default void trace(String message) {
        log(Level.Trace, null, message);
    }

    default void trace(String message, Object arg) {
        log(Level.Trace, null, message, arg);
    }

    default void trace(String message, Object arg1, Object arg2) {
        log(Level.Trace, null, message, arg1, arg2);
    }

    default void trace(String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Trace, null, message, arg1, arg2, arg3);
    }

    default void trace(String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Trace, null, message, arg1, arg2, arg3, args);
    }

    default void trace(Throwable throwable, String message) {
        log(Level.Trace, throwable, message);
    }

    default void trace(Throwable throwable, String message, Object arg) {
        log(Level.Trace, throwable, message, arg);
    }

    default void trace(Throwable throwable, String message, Object arg1, Object arg2) {
        log(Level.Trace, throwable, message, arg1, arg2);
    }

    default void trace(Throwable throwable, String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Trace, throwable, message, arg1, arg2, arg3);
    }

    default void trace(Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Trace, throwable, message, arg1, arg2, arg3, args);
    }

    default void debug(String message) {
        log(Level.Debug, null, message);
    }

    default void debug(String message, Object arg) {
        log(Level.Debug, null, message, arg);
    }

    default void debug(String message, Object arg1, Object arg2) {
        log(Level.Debug, null, message, arg1, arg2);
    }

    default void debug(String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Debug, null, message, arg1, arg2, arg3);
    }

    default void debug(String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Debug, null, message, arg1, arg2, arg3, args);
    }

    default void debug(Throwable throwable, String message) {
        log(Level.Debug, throwable, message);
    }

    default void debug(Throwable throwable, String message, Object arg) {
        log(Level.Debug, throwable, message, arg);
    }

    default void debug(Throwable throwable, String message, Object arg1, Object arg2) {
        log(Level.Debug, throwable, message, arg1, arg2);
    }

    default void debug(Throwable throwable, String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Debug, throwable, message, arg1, arg2, arg3);
    }

    default void debug(Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Debug, throwable, message, arg1, arg2, arg3, args);
    }

    default void info(String message) {
        log(Level.Info, null, message);
    }

    default void info(String message, Object arg) {
        log(Level.Info, null, message, arg);
    }

    default void info(String message, Object arg1, Object arg2) {
        log(Level.Info, null, message, arg1, arg2);
    }

    default void info(String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Info, null, message, arg1, arg2, arg3);
    }

    default void info(String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Info, null, message, arg1, arg2, arg3, args);
    }

    default void info(Throwable throwable, String message) {
        log(Level.Info, throwable, message);
    }

    default void info(Throwable throwable, String message, Object arg) {
        log(Level.Info, throwable, message, arg);
    }

    default void info(Throwable throwable, String message, Object arg1, Object arg2) {
        log(Level.Info, throwable, message, arg1, arg2);
    }

    default void info(Throwable throwable, String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Info, throwable, message, arg1, arg2, arg3);
    }

    default void info(Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Info, throwable, message, arg1, arg2, arg3, args);
    }

    default void warning(String message) {
        log(Level.Warning, null, message);
    }

    default void warning(String message, Object arg) {
        log(Level.Warning, null, message, arg);
    }

    default void warning(String message, Object arg1, Object arg2) {
        log(Level.Warning, null, message, arg1, arg2);
    }

    default void warning(String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Warning, null, message, arg1, arg2, arg3);
    }

    default void warning(String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Warning, null, message, arg1, arg2, arg3, args);
    }

    default void warning(Throwable throwable, String message) {
        log(Level.Warning, throwable, message);
    }

    default void warning(Throwable throwable, String message, Object arg) {
        log(Level.Warning, throwable, message, arg);
    }

    default void warning(Throwable throwable, String message, Object arg1, Object arg2) {
        log(Level.Warning, throwable, message, arg1, arg2);
    }

    default void warning(Throwable throwable, String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Warning, throwable, message, arg1, arg2, arg3);
    }

    default void warning(Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Warning, throwable, message, arg1, arg2, arg3, args);
    }

    default void fatal(String message) {
        log(Level.Fatal, null, message);
    }

    default void fatal(String message, Object arg) {
        log(Level.Fatal, null, message, arg);
    }

    default void fatal(String message, Object arg1, Object arg2) {
        log(Level.Fatal, null, message, arg1, arg2);
    }

    default void fatal(String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Fatal, null, message, arg1, arg2, arg3);
    }

    default void fatal(String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Fatal, null, message, arg1, arg2, arg3, args);
    }

    default void fatal(Throwable throwable, String message) {
        log(Level.Fatal, throwable, message);
    }

    default void fatal(Throwable throwable, String message, Object arg) {
        log(Level.Fatal, throwable, message, arg);
    }

    default void fatal(Throwable throwable, String message, Object arg1, Object arg2) {
        log(Level.Fatal, throwable, message, arg1, arg2);
    }

    default void fatal(Throwable throwable, String message, Object arg1, Object arg2, Object arg3) {
        log(Level.Fatal, throwable, message, arg1, arg2, arg3);
    }

    default void fatal(Throwable throwable, String message, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Fatal, throwable, message, arg1, arg2, arg3, args);
    }

}
