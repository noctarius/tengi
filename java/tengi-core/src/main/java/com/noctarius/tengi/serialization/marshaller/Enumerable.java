package com.noctarius.tengi.serialization.marshaller;

public interface Enumerable<T extends Enum<T> & Enumerable<T>> {

    int flag();

    public static <E extends Enumerable> E value(Class<E> type, int flag) {
        for (E constant : type.getEnumConstants()) {
            if (flag == constant.flag()) {
                return constant;
            }
        }
        return null;
    }

}
