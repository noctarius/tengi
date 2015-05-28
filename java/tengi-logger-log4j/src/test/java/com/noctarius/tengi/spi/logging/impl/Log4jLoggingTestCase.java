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

import com.noctarius.tengi.spi.logging.Logger;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Log4jLoggingTestCase {

    @Test
    public void test_trace_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test"), Level.TRACE);
    }

    @Test
    public void test_trace_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s", 1), Level.TRACE);
    }

    @Test
    public void test_trace_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s, %s", 1, 2), Level.TRACE);
    }

    @Test
    public void test_trace_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s, %s, %s", 1, 2, 3), Level.TRACE);
    }

    @Test
    public void test_trace_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.TRACE);
    }

    @Test
    public void test_debug_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test"), Level.DEBUG);
    }

    @Test
    public void test_debug_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s", 1), Level.DEBUG);
    }

    @Test
    public void test_debug_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s, %s", 1, 2), Level.DEBUG);
    }

    @Test
    public void test_debug_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s, %s, %s", 1, 2, 3), Level.DEBUG);
    }

    @Test
    public void test_debug_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.DEBUG);
    }

    @Test
    public void test_info_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.info("test"), Level.INFO);
    }

    @Test
    public void test_info_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.info("test, %s", 1), Level.INFO);
    }

    @Test
    public void test_info_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.info("test, %s, %s", 1, 2), Level.INFO);
    }

    @Test
    public void test_info_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.info("test, %s, %s, %s", 1, 2, 3), Level.INFO);
    }

    @Test
    public void test_info_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.info("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.INFO);
    }

    @Test
    public void test_warning_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test"), Level.WARN);
    }

    @Test
    public void test_warning_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s", 1), Level.WARN);
    }

    @Test
    public void test_warning_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s, %s", 1, 2), Level.WARN);
    }

    @Test
    public void test_warning_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s, %s, %s", 1, 2, 3), Level.WARN);
    }

    @Test
    public void test_warning_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.WARN);
    }

    @Test
    public void test_fatal_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test"), Level.FATAL);
    }

    @Test
    public void test_fatal_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s", 1), Level.FATAL);
    }

    @Test
    public void test_fatal_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s, %s", 1, 2), Level.FATAL);
    }

    @Test
    public void test_fatal_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s, %s, %s", 1, 2, 3), Level.FATAL);
    }

    @Test
    public void test_fatal_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.FATAL);
    }

    @Test
    public void test_trace_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test"), Level.TRACE);
    }

    @Test
    public void test_trace_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s", 1), Level.TRACE);
    }

    @Test
    public void test_trace_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s, %s", 1, 2), Level.TRACE);
    }

    @Test
    public void test_trace_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.TRACE);
    }

    @Test
    public void test_trace_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.TRACE);
    }

    @Test
    public void test_debug_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test"), Level.DEBUG);
    }

    @Test
    public void test_debug_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s", 1), Level.DEBUG);
    }

    @Test
    public void test_debug_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s, %s", 1, 2), Level.DEBUG);
    }

    @Test
    public void test_debug_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.DEBUG);
    }

    @Test
    public void test_debug_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.DEBUG);
    }

    @Test
    public void test_info_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.info(new NullPointerException(), "test"), Level.INFO);
    }

    @Test
    public void test_info_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.info(new NullPointerException(), "test, %s", 1), Level.INFO);
    }

    @Test
    public void test_info_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.info(new NullPointerException(), "test, %s, %s", 1, 2), Level.INFO);
    }

    @Test
    public void test_info_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.info(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.INFO);
    }

    @Test
    public void test_info_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.info(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.INFO);
    }

    @Test
    public void test_warning_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test"), Level.WARN);
    }

    @Test
    public void test_warning_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s", 1), Level.WARN);
    }

    @Test
    public void test_warning_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s, %s", 1, 2), Level.WARN);
    }

    @Test
    public void test_warning_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.WARN);
    }

    @Test
    public void test_warning_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.WARN);
    }

    @Test
    public void test_fatal_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test"), Level.FATAL);
    }

    @Test
    public void test_fatal_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s", 1), Level.FATAL);
    }

    @Test
    public void test_fatal_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s, %s", 1, 2), Level.FATAL);
    }

    @Test
    public void test_fatal_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.FATAL);
    }

    @Test
    public void test_fatal_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.FATAL);
    }

    private static void practice(Consumer<Logger> test, Level level) {
        org.apache.log4j.Logger realLogger = org.apache.log4j.Logger.getLogger(Log4jLoggingTestCase.class);

        Logger logger = new Log4jLogger(realLogger);
        VerifierAppender appender = new VerifierAppender();
        realLogger.addAppender(appender);

        activateLogLevel(realLogger, level);

        test.accept(logger);
        realLogger.removeAppender(appender);

        assertEquals(level, appender.level);

        Throwable throwable = appender.throwableInformation != null ? appender.throwableInformation.getThrowable() : null;
        if (throwable != null) {
            assertTrue(throwable instanceof NullPointerException);
        }
    }

    private static void activateLogLevel(org.apache.log4j.Logger logger, Level level) {
        logger.setLevel(level);
        org.apache.log4j.Logger.getRootLogger().setLevel(level);
    }

    private static class VerifierAppender
            extends AppenderSkeleton {

        private Level level;
        private ThrowableInformation throwableInformation;

        @Override
        protected void append(LoggingEvent event) {
            if (this.level == null) {
                this.level = event.getLevel();
                this.throwableInformation = event.getThrowableInformation();
            }
        }

        @Override
        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }

}
