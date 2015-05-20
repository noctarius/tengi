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
package com.noctarius.tengi.core.serialization;

/**
 * <p>The <tt>Enumerable</tt> interface is used to mark highly optimized but
 * evolutionary {@link java.lang.Enum} types. For support of evolution of enums
 * the bundled serialization framework serializes an <tt>Enum</tt> using
 * the name of the constant since ordinals tend to change based on reordering or
 * adding new constants.</p>
 * <p><tt>Enumerable</tt>s therefore have an additional value called <tt>flag</tt>
 * which acts as a serialization constant type id, even if order or name of the
 * constant changes or new constants are added the flag value can be preserved.</p>
 * <p>Whenever a <tt>Enumerable</tt> is serialized the <tt>flag</tt> value will be
 * written to the stream to optimize traffic and serialization speed.</p>
 *
 * @param <T> the type of the Enumerable which needs to be an <tt>Enum</tt> and an <tt>Enumerable</tt>
 */
public interface Enumerable<T extends Enum<T> & Enumerable<T>> {

    /**
     * Returns the <tt>flag</tt> value which acts as a serialization constant type id.
     *
     * @return the flag value
     */
    int flag();

    /**
     * This method supports to search for a <tt>Enumerable</tt> constant based on the given
     * flag value. If no such constant can be found it will return <tt>null</tt>.
     *
     * @param type the <tt>Enumerable</tt> type to search in
     * @param flag the flag value (constant id)
     * @param <E>  the type generic type of the <tt>Enumerable</tt> type
     * @return the constant according to the given <tt>flag</tt> if found, otherwise null
     */
    public static <E extends Enumerable> E value(Class<E> type, int flag) {
        for (E constant : type.getEnumConstants()) {
            if (flag == constant.flag()) {
                return constant;
            }
        }
        return null;
    }

}
