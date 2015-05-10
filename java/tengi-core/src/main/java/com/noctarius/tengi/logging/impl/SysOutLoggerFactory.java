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
import com.noctarius.tengi.logging.LoggerFactory;

public class SysOutLoggerFactory
        implements LoggerFactory {

    @Override
    public Logger create(Class<?> binding) {
        String[] tokens = binding.getName().split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (i == tokens.length - 1) {
                sb.append(tokens[i]);
            } else {
                sb.append(tokens[i].charAt(0)).append('.');
            }
        }
        return new SysOutLogger(sb.toString());
    }

    @Override
    public Logger create(String binding) {
        return new SysOutLogger(binding);
    }

    @Override
    public Class<?> loggerClass() {
        return SysOutLogger.class;
    }
}
