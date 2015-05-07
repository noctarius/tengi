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
