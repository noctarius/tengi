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
package com.noctarius.tengi.core.serialization;

/**
 * <p>The <tt>NonCacheable</tt> annotation class is used to mark an assignment
 * between a {@link com.noctarius.tengi.core.serialization.Marshallable}
 * implementing class and a {@link com.noctarius.tengi.core.serialization.marshaller.Marshaller}
 * based on the search using registered
 * {@link com.noctarius.tengi.core.serialization.marshaller.MarshallerFilter}s as non cacheable.</p>
 * <p>If a type might need different <tt>Marshaller</tt>s over the lifetime of a client or server
 * the <tt>MarshallerFilter</tt> result can not be cached but must be reevaluated on each
 * serialization or deserialization request. By default all found <tt>Marshaller</tt> matches
 * are cached to speed up further requests. If marked as non cacheable the result will still be
 * remembered and evaluated first, only if the evaluation fails a new <tt>Marshaller</tt> is searched.</p>
 */
public @interface NonCacheable {
}
