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
package com.noctarius.tengi.cbor;

import java.util.List;

public class ApiTest {

    public static void main(String[] args) {
        List<Item> items = Items.create()
                .push(1234)
                .push(-1234)
                .push(1.0f)
                .push(1.00001f)
                .push(1.00000000001f)
                .push("someText")
                .pushArray()
                    .push(4321)
                    .push("otherText")
                    .end()
                .build();

    }
}
