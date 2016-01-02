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
package com.noctarius.tengi.spi.logging;

public interface Logger {

    /**
     * Logs a <tt>message</tt> at the given <tt>level</tt>. A non-null <tt>throwable</tt>
     * will also be logged.
     *
     * @param level     the <tt>Level</tt> to log to
     * @param throwable the <tt>Throwable</tt> to log or null if no exception should be logged
     * @param message   the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    void log(Level level, Throwable throwable, String message);

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at the given <tt>level</tt>. A non-null <tt>throwable</tt> will also be logged.
     *
     * @param level     the <tt>Level</tt> to log to
     * @param throwable the <tt>Throwable</tt> to log or null if no exception should be logged
     * @param format    the format to log
     * @param arg       the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>format</tt> is null
     */
    void log(Level level, Throwable throwable, String format, Object arg);

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at the given <tt>level</tt>. A non-null <tt>throwable</tt> will also be logged.
     *
     * @param level     the <tt>Level</tt> to log to
     * @param throwable the <tt>Throwable</tt> to log or null if no exception should be logged
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>format</tt> is null
     */
    void log(Level level, Throwable throwable, String format, Object arg1, Object arg2);

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at the given <tt>level</tt>. A non-null <tt>throwable</tt> will also be logged.
     *
     * @param level     the <tt>Level</tt> to log to
     * @param throwable the <tt>Throwable</tt> to log or null if no exception should be logged
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>format</tt> is null
     */
    void log(Level level, Throwable throwable, String format, Object arg1, Object arg2, Object arg3);

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at the given <tt>level</tt>. A non-null <tt>throwable</tt> will also be logged.
     *
     * @param level     the <tt>Level</tt> to log to
     * @param throwable the <tt>Throwable</tt> to log or null if no exception should be logged
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @param args      additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>format</tt> is null
     */
    void log(Level level, Throwable throwable, String format, Object arg1, Object arg2, Object arg3, Object... args);

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Trace}.
     *
     * @param message the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(String message) {
        log(Level.Trace, null, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}.
     *
     * @param format the format to log
     * @param arg    the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(String format, Object arg) {
        log(Level.Trace, null, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(String format, Object arg1, Object arg2) {
        log(Level.Trace, null, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Trace, null, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @param args   additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Trace, null, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Trace}. The
     * given throwable will also be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param message   the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(Throwable throwable, String message) {
        log(Level.Trace, throwable, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg       the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(Throwable throwable, String format, Object arg) {
        log(Level.Trace, throwable, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(Throwable throwable, String format, Object arg1, Object arg2) {
        log(Level.Trace, throwable, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(Throwable throwable, String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Trace, throwable, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Trace}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @param args      additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void trace(Throwable throwable, String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Trace, throwable, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Debug}.
     *
     * @param message the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(String message) {
        log(Level.Debug, null, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}.
     *
     * @param format the format to log
     * @param arg    the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(String format, Object arg) {
        log(Level.Debug, null, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(String format, Object arg1, Object arg2) {
        log(Level.Debug, null, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Debug, null, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @param args   additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Debug, null, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Debug}. The
     * given throwable will also be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param message   the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(Throwable throwable, String message) {
        log(Level.Debug, throwable, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg       the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(Throwable throwable, String format, Object arg) {
        log(Level.Debug, throwable, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(Throwable throwable, String format, Object arg1, Object arg2) {
        log(Level.Debug, throwable, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(Throwable throwable, String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Debug, throwable, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Debug}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @param args      additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void debug(Throwable throwable, String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Debug, throwable, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Info}.
     *
     * @param message the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(String message) {
        log(Level.Info, null, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}.
     *
     * @param format the format to log
     * @param arg    the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(String format, Object arg) {
        log(Level.Info, null, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(String format, Object arg1, Object arg2) {
        log(Level.Info, null, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Info, null, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @param args   additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Info, null, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Info}. The
     * given throwable will also be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param message   the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(Throwable throwable, String message) {
        log(Level.Info, throwable, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg       the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(Throwable throwable, String format, Object arg) {
        log(Level.Info, throwable, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(Throwable throwable, String format, Object arg1, Object arg2) {
        log(Level.Info, throwable, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(Throwable throwable, String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Info, throwable, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Info}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @param args      additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void info(Throwable throwable, String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Info, throwable, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Warning}.
     *
     * @param message the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(String message) {
        log(Level.Warning, null, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}.
     *
     * @param format the format to log
     * @param arg    the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(String format, Object arg) {
        log(Level.Warning, null, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(String format, Object arg1, Object arg2) {
        log(Level.Warning, null, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Warning, null, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @param args   additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Warning, null, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Warning}. The
     * given throwable will also be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param message   the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(Throwable throwable, String message) {
        log(Level.Warning, throwable, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg       the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(Throwable throwable, String format, Object arg) {
        log(Level.Warning, throwable, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(Throwable throwable, String format, Object arg1, Object arg2) {
        log(Level.Warning, throwable, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(Throwable throwable, String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Warning, throwable, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Warning}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @param args      additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void warning(Throwable throwable, String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Warning, throwable, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}.
     *
     * @param message the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(String message) {
        log(Level.Fatal, null, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}.
     *
     * @param format the format to log
     * @param arg    the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(String format, Object arg) {
        log(Level.Fatal, null, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(String format, Object arg1, Object arg2) {
        log(Level.Fatal, null, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Fatal, null, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}.
     *
     * @param format the format to log
     * @param arg1   the first argument to pass to the format
     * @param arg2   the second argument to pass to the format
     * @param arg3   the third argument to pass to the format
     * @param args   additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Fatal, null, format, arg1, arg2, arg3, args);
    }

    /**
     * Logs a <tt>message</tt> at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}. The
     * given throwable will also be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param message   the message to log
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(Throwable throwable, String message) {
        log(Level.Fatal, throwable, message);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed <tt>arg</tt>
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg       the argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(Throwable throwable, String format, Object arg) {
        log(Level.Fatal, throwable, format, arg);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(Throwable throwable, String format, Object arg1, Object arg2) {
        log(Level.Fatal, throwable, format, arg1, arg2);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(Throwable throwable, String format, Object arg1, Object arg2, Object arg3) {
        log(Level.Fatal, throwable, format, arg1, arg2, arg3);
    }

    /**
     * Logs a message based on the given <tt>format</tt> and enriched with the passed arguments
     * at level {@link com.noctarius.tengi.spi.logging.Level#Fatal}. The given throwable will also
     * be logged if configured but does not need to be set.
     *
     * @param throwable the <tt>Throwable</tt> to log
     * @param format    the format to log
     * @param arg1      the first argument to pass to the format
     * @param arg2      the second argument to pass to the format
     * @param arg3      the third argument to pass to the format
     * @param args      additional parameters to pass to the format
     * @throws java.lang.NullPointerException whenever <tt>level</tt> or <tt>message</tt> is null
     */
    default void fatal(Throwable throwable, String format, Object arg1, Object arg2, Object arg3, Object... args) {
        log(Level.Fatal, throwable, format, arg1, arg2, arg3, args);
    }

}
