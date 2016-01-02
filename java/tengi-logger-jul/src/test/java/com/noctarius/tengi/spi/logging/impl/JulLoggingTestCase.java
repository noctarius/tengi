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
package com.noctarius.tengi.spi.logging.impl;

import com.noctarius.tengi.spi.logging.Logger;
import org.junit.Test;

import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JulLoggingTestCase {

    @Test
    public void test_trace_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test"), Level.FINEST);
    }

    @Test
    public void test_trace_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s", 1), Level.FINEST);
    }

    @Test
    public void test_trace_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s, %s", 1, 2), Level.FINEST);
    }

    @Test
    public void test_trace_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s, %s, %s", 1, 2, 3), Level.FINEST);
    }

    @Test
    public void test_trace_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.FINEST);
    }

    @Test
    public void test_debug_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test"), Level.FINE);
    }

    @Test
    public void test_debug_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s", 1), Level.FINE);
    }

    @Test
    public void test_debug_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s, %s", 1, 2), Level.FINE);
    }

    @Test
    public void test_debug_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s, %s, %s", 1, 2, 3), Level.FINE);
    }

    @Test
    public void test_debug_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.FINE);
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

        practice((logger) -> logger.warning("test"), Level.WARNING);
    }

    @Test
    public void test_warning_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s", 1), Level.WARNING);
    }

    @Test
    public void test_warning_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s, %s", 1, 2), Level.WARNING);
    }

    @Test
    public void test_warning_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s, %s, %s", 1, 2, 3), Level.WARNING);
    }

    @Test
    public void test_warning_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.WARNING);
    }

    @Test
    public void test_fatal_no_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test"), Level.SEVERE);
    }

    @Test
    public void test_fatal_1_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s", 1), Level.SEVERE);
    }

    @Test
    public void test_fatal_2_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s, %s", 1, 2), Level.SEVERE);
    }

    @Test
    public void test_fatal_3_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s, %s, %s", 1, 2, 3), Level.SEVERE);
    }

    @Test
    public void test_fatal_5_param_no_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal("test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.SEVERE);
    }

    @Test
    public void test_trace_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test"), Level.FINEST);
    }

    @Test
    public void test_trace_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s", 1), Level.FINEST);
    }

    @Test
    public void test_trace_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s, %s", 1, 2), Level.FINEST);
    }

    @Test
    public void test_trace_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.FINEST);
    }

    @Test
    public void test_trace_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.trace(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.FINEST);
    }

    @Test
    public void test_debug_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test"), Level.FINE);
    }

    @Test
    public void test_debug_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s", 1), Level.FINE);
    }

    @Test
    public void test_debug_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s, %s", 1, 2), Level.FINE);
    }

    @Test
    public void test_debug_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.FINE);
    }

    @Test
    public void test_debug_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.debug(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.FINE);
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

        practice((logger) -> logger.warning(new NullPointerException(), "test"), Level.WARNING);
    }

    @Test
    public void test_warning_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s", 1), Level.WARNING);
    }

    @Test
    public void test_warning_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s, %s", 1, 2), Level.WARNING);
    }

    @Test
    public void test_warning_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.WARNING);
    }

    @Test
    public void test_warning_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.warning(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5),
                Level.WARNING);
    }

    @Test
    public void test_fatal_no_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test"), Level.SEVERE);
    }

    @Test
    public void test_fatal_1_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s", 1), Level.SEVERE);
    }

    @Test
    public void test_fatal_2_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s, %s", 1, 2), Level.SEVERE);
    }

    @Test
    public void test_fatal_3_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s, %s, %s", 1, 2, 3), Level.SEVERE);
    }

    @Test
    public void test_fatal_5_param_with_exception_logging()
            throws Exception {

        practice((logger) -> logger.fatal(new NullPointerException(), "test, %s, %s, %s, %s, %s", 1, 2, 3, 4, 5), Level.SEVERE);
    }

    private void practice(Consumer<Logger> test, Level level) {
        java.util.logging.Logger realLogger = java.util.logging.Logger.getLogger(JulLoggingTestCase.class.getName());

        Logger logger = new JULLogger(realLogger);
        VerifierHandler handler = new VerifierHandler();
        realLogger.addHandler(handler);

        activateLogLevel(level);

        test.accept(logger);
        realLogger.removeHandler(handler);

        assertEquals(level, handler.level);

        Throwable throwable = handler.throwable;
        if (throwable != null) {
            assertTrue(throwable instanceof NullPointerException);
        }
    }

    private static void activateLogLevel(Level level) {
        Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
        while (loggerNames.hasMoreElements()) {
            LogManager.getLogManager().getLogger(loggerNames.nextElement()).setLevel(level);
        }
    }

    private static class VerifierHandler
            extends Handler {

        private Level level;
        private Throwable throwable;

        @Override
        public void publish(LogRecord record) {
            if (this.level == null) {
                this.level = record.getLevel();
                this.throwable = record.getThrown();
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close()
                throws SecurityException {
        }
    }

}
