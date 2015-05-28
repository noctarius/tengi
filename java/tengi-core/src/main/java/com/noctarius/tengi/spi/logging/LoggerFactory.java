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

/**
 * <p>The <tt>LoggerFactory</tt> interface describes an entry point for
 * external logging framework integrations. It is registered using the
 * Java ServiceLoader API and automatically recognized on startup.</p>
 * <p>Such a factory is used to create the underlying logger implementations
 * and delegates to the external logging framework.</p>
 */
public interface LoggerFactory {

    /**
     * Creates a new <tt>Logger</tt> bound to the given <tt>binding</tt> type.
     *
     * @param binding the <tt>java.lang.Class</tt> to bind to
     * @return the <b>new</b> <tt>Logger</tt> instance
     */
    Logger create(Class<?> binding);

    /**
     * Creates a new <tt>Logger</tt> bound to the given <tt>binding</tt> string.
     *
     * @param binding the <tt>java.lang.String</tt> to bind to
     * @return the <b>new</b> <tt>Logger</tt> instance
     */
    Logger create(String binding);

    /**
     * Returns the type of the <tt>Logger</tt> that is created by this factory
     * instance.
     *
     * @return the type of the created <tt>Logger</tt>s
     */
    Class<?> loggerClass();

}
