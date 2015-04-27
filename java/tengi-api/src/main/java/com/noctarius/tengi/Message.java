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
package com.noctarius.tengi;

import com.noctarius.tengi.serialization.Marshallable;

/**
 * The <tt>Message</tt> interface defines a message which consists of an
 * unique identifier and a message body which is any kind of object that
 * have a registered {@link com.noctarius.tengi.serialization.marshaller.Marshaller}
 * or is an internally handled type.
 */
public interface Message extends Marshallable {

    /**
     * Returns the unique message Id.
     *
     * @return the message id
     */
    Identifier getMessageId();

    /**
     * Returns the messages body object. This object is the real value of
     * the message and can be of any kind of object that
     * have a registered {@link com.noctarius.tengi.serialization.marshaller.Marshaller}
     * or is an internally handled type.
     *
     * @param <O> the type of the messages object body
     * @return the message body
     */
    <O> O getBody();

    public static Message create(Object body) {
        Identifier identifier = Identifier.randomIdentifier();
        return null;
    }

}
