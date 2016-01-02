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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.Test;

import java.io.Serializable;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Log4jV2LoggingTestCase {

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
        org.apache.logging.log4j.Logger realLogger = LogManager.getLogger(Log4jV2LoggingTestCase.class);

        Logger logger = new Log4jV2Logger(realLogger);
        VerifierAppender appender = new VerifierAppender("verifier", null, null, false);
        appender.start();

        activateLogLevel(level, appender);

        test.accept(logger);

        assertEquals(level, appender.level);
        Throwable throwable = appender.throwable;
        if (throwable != null) {
            assertTrue(throwable instanceof NullPointerException);
        }
    }

    private static void activateLogLevel(Level level, Appender appender) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(ClassLoader.getSystemClassLoader(), false, null);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        loggerConfig.addAppender(appender, level, null);
        loggerContext.updateLoggers(configuration);
    }

    private static class VerifierAppender
            extends AbstractAppender {

        private Level level;
        private Throwable throwable;

        protected VerifierAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
            super(name, filter, layout, ignoreExceptions);
        }

        @Override
        public void append(LogEvent event) {
            if (this.level == null) {
                this.level = event.getLevel();
                this.throwable = event.getThrown();
            }
        }
    }

}
