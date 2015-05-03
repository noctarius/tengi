package com.noctarius.tengi.utils;

import com.noctarius.tengi.SystemException;

public final class ExceptionUtil {

    public static RuntimeException rethrow(Exception e) {
        if (e instanceof SystemException) {
            throw (SystemException) e;
        }
        SystemException exception = new SystemException(e);
        StackTraceElement[] stackTrace = exception.getStackTrace();
        StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - 1];
        System.arraycopy(stackTrace, 1, newStackTrace, 0, stackTrace.length - 1);
        exception.setStackTrace(newStackTrace);
        throw exception;
    }

    private ExceptionUtil() {
    }
}
