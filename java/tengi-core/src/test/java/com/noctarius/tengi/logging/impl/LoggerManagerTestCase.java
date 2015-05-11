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

import com.noctarius.tengi.SystemException;
import com.noctarius.tengi.testing.FilteringClassLoader;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import static java.util.Collections.emptyList;

public class LoggerManagerTestCase {

    @Test(expected = SystemException.class)
    public void test_multiple_logger_implementations_string_binding()
            throws Throwable {

        ClassLoader classLoader = new ServicesAdaptingClassLoader(emptyList(), "com.noctarius.tengi.logging.LoggerManager");
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            Class<?> loggerManagerClass = classLoader.loadClass("com.noctarius.tengi.logging.LoggerManager");
            Method getLogger = loggerManagerClass.getMethod("getLogger", String.class);
            getLogger.invoke(loggerManagerClass, "test");
        } catch (ReflectiveOperationException e) {
            throw e.getCause();
        }
    }

    @Test(expected = SystemException.class)
    public void test_multiple_logger_implementations_class_binding()
            throws Throwable {

        ClassLoader classLoader = new ServicesAdaptingClassLoader(emptyList(), "com.noctarius.tengi.logging.LoggerManager");
        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            Class<?> loggerManagerClass = classLoader.loadClass("com.noctarius.tengi.logging.LoggerManager");
            Method getLogger = loggerManagerClass.getMethod("getLogger", Class.class);
            getLogger.invoke(loggerManagerClass, LoggerManagerTestCase.class);
        } catch (ReflectiveOperationException e) {
            throw e.getCause();
        }
    }

    @Test
    public void test_multiple_logger_implementations_select_specific_string_binding()
            throws Throwable {

        ClassLoader classLoader = new ServicesAdaptingClassLoader(emptyList(), "com.noctarius.tengi.logging.LoggerManager");

        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            Class<?> loggerManagerClass = classLoader.loadClass("com.noctarius.tengi.logging.LoggerManager");
            Method getLogger = loggerManagerClass.getMethod("getLogger", String.class, Class.class);
            getLogger.invoke(loggerManagerClass, "test", SysOutLogger.class);
        } catch (ReflectiveOperationException e) {
            throw e.getCause();
        }
    }

    @Test
    public void test_multiple_logger_implementations_select_specific_class_binding()
            throws Throwable {

        ClassLoader classLoader = new ServicesAdaptingClassLoader(emptyList(), "com.noctarius.tengi.logging.LoggerManager");

        Thread.currentThread().setContextClassLoader(classLoader);

        try {
            Class<?> loggerManagerClass = classLoader.loadClass("com.noctarius.tengi.logging.LoggerManager");
            Method getLogger = loggerManagerClass.getMethod("getLogger", Class.class, Class.class);
            getLogger.invoke(loggerManagerClass, LoggerManagerTestCase.class, SysOutLogger.class);
        } catch (ReflectiveOperationException e) {
            throw e.getCause();
        }
    }

    private static class ServicesAdaptingClassLoader
            extends FilteringClassLoader {

        private ServicesAdaptingClassLoader(List<String> excludePackages, String enforcedSelfLoadingPackage) {
            super(excludePackages, enforcedSelfLoadingPackage);
        }

        @Override
        public Enumeration<URL> getResources(String name)
                throws IOException {

            if ("META-INF/services/com.noctarius.tengi.logging.LoggerFactory".equals(name)) {
                URL url = LoggerManagerTestCase.class.getResource("LoggerManagerTestCase");
                Vector<URL> resources = new Vector<>();
                resources.add(url);
                return resources.elements();
            }

            return super.getResources(name);
        }
    }

}
