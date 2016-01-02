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
package com.noctarius.tengi.spi.statemachine;

import java.util.Map;

public interface StateMachine<E extends Enum<E>> {

    E currentState();

    boolean transit(E newState);

    Context getContext();

    public static <E extends Enum<E>> Builder<E> newBuilder() {
        return new BuilderImpl();
    }

    interface Builder<E extends Enum<E>> {

        default Builder<E> addTransition(E oldState, E newState) {
            return addTransition(oldState, newState, null);
        }

        Builder<E> addTransition(E oldState, E newState, Evaluator<E> evaluator);

        StateMachine<E> build(E startState, boolean contextSupport);
    }

    interface Evaluator<E extends Enum<E>> {
        boolean evaluate(E oldState, E newState, Context context);
    }

    interface Context {
        <T> T getAttribute(String name);

        <T> T setAttribute(String name, T value);

        Map<String, Object> getAttributes();
    }
}
