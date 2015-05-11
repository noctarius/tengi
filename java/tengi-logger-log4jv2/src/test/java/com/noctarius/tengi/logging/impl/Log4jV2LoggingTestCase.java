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

import com.noctarius.tengi.logging.Logger;
import com.noctarius.tengi.logging.LoggerManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.Test;

import java.util.function.Consumer;

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
        Logger logger = LoggerManager.getLogger(Log4jV2LoggingTestCase.class);
        assertTrue(Log4jV2Logger.class.isAssignableFrom(logger.getClass()));

        activateLogLevel(level);
        test.accept(logger);
    }

    private static void activateLogLevel(Level level) {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(ClassLoader.getSystemClassLoader(), false, null);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig loggerConfig = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        loggerContext.updateLoggers(configuration);
    }

}
