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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

class BuilderImpl<E extends Enum<E>>
        implements StateMachine.Builder<E> {

    private final List<Transition<E>> transitions = new ArrayList<>();

    @Override
    public StateMachine.Builder<E> addTransition(E oldState, E newState, StateMachine.Evaluator<E> evaluator) {
        transitions.add(new Transition<E>(oldState, newState, evaluator));
        return this;
    }

    @Override
    public StateMachine<E> build(E startState, boolean contextSupport) {
        return new StateMachineImpl<>(transitions, startState, contextSupport);
    }

    private static class Transition<E extends Enum<E>> {
        private final E oldState;
        private final E newState;
        private final StateMachine.Evaluator<E> evaluator;

        Transition(E oldState, E newState, StateMachine.Evaluator<E> evaluator) {
            this.oldState = oldState;
            this.newState = newState;
            this.evaluator = evaluator;
        }
    }

    private static class StateMachineImpl<E extends Enum<E>>
            implements StateMachine<E> {

        private static final AtomicReferenceFieldUpdater<StateMachineImpl, Enum> STATE_UPDATER = AtomicReferenceFieldUpdater
                .newUpdater(StateMachineImpl.class, Enum.class, "currentState");

        private final Context context;

        private final Map<E, List<Transition<E>>> transitions;

        private volatile E currentState;

        StateMachineImpl(List<Transition<E>> transitions, E startState, boolean contextSupport) {
            this.transitions = createTransitionLookupTable(transitions);
            this.context = contextSupport ? new ContextImpl() : null;
            this.currentState = startState;
        }

        @Override
        public E currentState() {
            return currentState;
        }

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public boolean transit(E newState) {
            List<Transition<E>> transitions = this.transitions.get(newState);
            if (transitions == null || transitions.size() == 0) {
                return false;
            }

            // Sanity system, max of 100 retries
            for (int i = 0; i < 100; i++) {
                E currentState = this.currentState;
                Transition<E> transition = findTransition(currentState, transitions);
                if (transition == null) {
                    return false;
                }

                if (transition.evaluator != null) {
                    if (!transition.evaluator.evaluate(currentState, newState, context)) {
                        return false;
                    }
                }

                if (STATE_UPDATER.compareAndSet(this, currentState, newState)) {
                    return true;
                }
            }
            return false;
        }

        private Transition<E> findTransition(E currentState, List<Transition<E>> transitions) {
            for (Transition<E> transition : transitions) {
                if (transition.oldState == currentState) {
                    return transition;
                }
            }
            return null;
        }

        private Map<E, List<Transition<E>>> createTransitionLookupTable(List<Transition<E>> transitions) {
            Map<E, List<Transition<E>>> mapping = new HashMap<>();
            for (Transition<E> transition : transitions) {
                List<Transition<E>> temp = mapping.get(transition.newState);
                if (temp == null) {
                    temp = new ArrayList<>();
                    mapping.put(transition.newState, temp);
                }
                temp.add(transition);
            }
            return mapping;
        }
    }

    private static class ContextImpl
            implements StateMachine.Context {

        private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

        @Override
        public <T> T getAttribute(String name) {
            return (T) attributes.get(name);
        }

        @Override
        public <T> T setAttribute(String name, T value) {
            return (T) attributes.put(name, value);
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Collections.unmodifiableMap(attributes);
        }
    }
}
