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
package com.noctarius.tengi.core.serialization.marshaller;

/**
 * <p>The <tt>MarshallerFilter</tt> is used to decide if a
 * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} can be used
 * to serialize or de-serialize an object type.</p>
 * <p>The result of the {@link #accept(Object)} method might be cached
 * if {@link MarshallerFilter.Result#AcceptedAndCache}
 * is returned but never for other results.</p>
 */
public interface MarshallerFilter {

    /**
     * <p>This method tests the given object with logic rules to decide if
     * the associated {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} is
     * able to serialize and de-serialize this object.</p>
     * <p>If {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter.Result#Next} is
     * returned the filter does not assume this object to be serializable using the
     * corresponding {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}. If
     * {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter.Result#Accepted} is
     * returned, the corresponding {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * will be able to serialize or de-serialize the object but the result won't be
     * cached. If {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter.Result#AcceptedAndCache}
     * is returned the {@link Marshaller} is also able
     * to serialize the object and the result will be cached and no new acceptance test is
     * executed for the same type again.</p>
     *
     * @param object the object to be evaluated for serializability using the associated Marshaller
     * @return the test result
     */
    Result accept(Object object);

    /**
     * The <tt>Result</tt> enum is used as a result returned from a <tt>MarshallerFilter</tt>
     * to give evaluation information on the tested value. If a value can be serialized or
     * deserialized using the associated {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
     * {@link #Accepted} or {@link #AcceptedAndCache} is returned (depending on the result is cacheable or not)
     * otherwise {@link #Next} is returned to test using the next filter in the chain.
     */
    public static enum Result {
        /**
         * The {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter} does not assume
         * that this object will be serializable using the corresponding
         * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}.
         */
        Next,

        /**
         * The {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter} does assume
         * that this object will be serializable using the corresponding
         * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} <b>but</b> the result will
         * <b>never be cached</b>. This might be useful in case where the serializability
         * depends on some internal state.
         */
        Accepted,

        /**
         * The {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter} does assume
         * that this object will be serializable using the corresponding
         * {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller} <b>and</b> the result
         * <b>might be cached</b>. This is the typical response for annotated types or
         * types that are implementations of an interface or a subclass of a special type.
         */
        AcceptedAndCache
    }

}
