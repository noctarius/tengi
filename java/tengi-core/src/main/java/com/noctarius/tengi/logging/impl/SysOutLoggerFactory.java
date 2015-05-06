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
