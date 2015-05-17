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

import com.noctarius.tengi.spi.logging.Logger;
import com.noctarius.tengi.spi.logging.LoggerFactory;

public class SLF4JLoggerFactory
        implements LoggerFactory {

    @Override
    public Logger create(Class<?> binding) {
        return new SLF4JLogger(org.slf4j.LoggerFactory.getLogger(binding));
    }

    @Override
    public Logger create(String binding) {
        return new SLF4JLogger(org.slf4j.LoggerFactory.getLogger(binding));
    }

    @Override
    public Class<?> loggerClass() {
        return org.slf4j.Logger.class;
    }
}