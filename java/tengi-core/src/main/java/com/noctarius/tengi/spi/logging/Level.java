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

/**
 * The <tt>Level</tt> enum defines the supported levels for the logging
 * implementation. These internal levels are matched to the external logging
 * framework's logging levels. Since no common rule exists how levels are
 * named or how they are defined please find the matching rules for the
 * different frameworks in the adapter implementations.
 */
public enum Level {

    /**
     * The <tt>Trace</tt> Level designates finer-grained
     * informational events than the <tt>Debug</tt> level.
     */
    Trace,

    /**
     * The <tt>Debug</tt> Level designates fine-grained
     * informational events that are most useful to debug an
     * application.
     */
    Debug,

    /**
     * The <tt>Info</tt> level designates informational messages
     * that highlight the progress of the application at coarse-grained
     * level.
     */
    Info,

    /**
     * The <tt>Warning</tt> level designates potentially harmful situations.
     */
    Warning,

    /**
     * The <tt>Fatal</tt> level designates a severe application error
     * event that will most probably lead the application to abort.
     */
    Fatal
}
