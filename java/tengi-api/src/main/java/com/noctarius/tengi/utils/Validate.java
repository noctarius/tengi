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
package com.noctarius.tengi.utils;

/**
 * The <tt>Validate</tt> class supports convenience validations to be
 * applied. It is fully Java 8 lambda capable and heavily uses them.
 */
public final class Validate {

    private static final String MESSAGE_PARAM_NOT_EQUAL = "%s must be equal to %s";
    private static final String MESSAGE_PARAM_NOT_GREATER_THAN = "%s must be greater than %s";
    private static final String MESSAGE_PARAM_NOT_LOWER_THAN = "%s must be lower than %s";
    private static final String MESSAGE_PARAM_NOT_GREATER_EQUAL = "%s must be greater than or equal to %s";
    private static final String MESSAGE_PARAM_NOT_LOWER_EQUAL = "%s must be lower than or equal to %s";
    private static final String MESSAGE_PARAM_NOT_NULL = "%s must not be null";

    public static void validate(MessageBuilder messageBuilder, Validation validation) {
        if (!validation.validate()) {
            throw new ValidationException(messageBuilder.build());
        }
    }

    public static void equals(String paramName, int expected, int value) {
        validate(message(MESSAGE_PARAM_NOT_EQUAL, paramName, expected), () -> expected == value);
    }

    public static void greaterThan(String paramName, int minimum, int value) {
        validate(message(MESSAGE_PARAM_NOT_GREATER_THAN, paramName, minimum), () -> minimum < value);
    }

    public static void lowerThan(String paramName, int maximum, int value) {
        validate(message(MESSAGE_PARAM_NOT_LOWER_THAN, paramName, maximum), () -> maximum > value);
    }

    public static void greaterOrEqual(String paramName, int minimum, int value) {
        validate(message(MESSAGE_PARAM_NOT_GREATER_EQUAL, paramName, minimum), () -> minimum <= value);
    }

    public static void lowerOrEqual(String paramName, int maximum, int value) {
        validate(message(MESSAGE_PARAM_NOT_LOWER_EQUAL, paramName, maximum), () -> maximum >= value);
    }

    public static void notNull(String paramName, Object value) {
        validate(message(MESSAGE_PARAM_NOT_NULL, paramName), () -> value != null);
    }

    private Validate() {
    }

    private static MessageBuilder message(String message, Object param) {
        return () -> String.format(message, param);
    }

    private static MessageBuilder message(String message, Object param1, Object param2) {
        return () -> String.format(message, param1, param2);
    }

    /**
     * The <tt>Validation</tt> interface is used to implement internal and
     * external validations based on Java 8 lambdas.
     */
    public static interface Validation {

        /**
         * This method implements the validation logic and returns <tt>true</tt>
         * if the validation passed or <tt>false</tt> if not.
         *
         * @return true if validation passed, otherwise false
         */
        boolean validate();
    }

    /**
     * The <tt>MessageBuilder</tt> interface is used to delay creation of
     * exception messages up to the point where a validation really failed.
     * This prevents heavy string concatinations or other sort of costly
     * operations to be as lazy as possible and to only happen if really
     * necessary.
     */
    public static interface MessageBuilder {

        /**
         * Generates the content and builds the exception message.
         *
         * @return the generated exception message
         */
        String build();
    }

    /**
     * This exception class is thrown whenever a validation fails. It is a subclass
     * of {@link java.lang.IllegalArgumentException} since most validations happen
     * on parameters passed to any constructor or function.
     */
    public static class ValidationException extends IllegalArgumentException {

        /**
         * Creation of a ValidationException using an exception message string.
         *
         * @param s string to be used as exception message
         */
        protected ValidationException(String s) {
            super(s);
        }
    }

}
