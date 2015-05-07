package com.noctarius.tengi.utils;

public final class MathUtil {

    private MathUtil() {
    }

    public static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }

    public static boolean isPowerOfTwo(long x) {
        return (x & (x - 1)) == 0;
    }

    public static int nextPowerOfTwo(int value) {
        if (!isPowerOfTwo(value)) {
            value--;
            value |= value >> 1;
            value |= value >> 2;
            value |= value >> 4;
            value |= value >> 8;
            value |= value >> 16;
            value++;
        }
        return value;
    }

}
