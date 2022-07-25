package com.navercorp.pinpoint.common.server.util;

public class TimestampUtils {
    // 1 hour
    private static final long ROUNDING_CONSTANT = 3_600_000 * 1;

    public static long reverseRoundedTimeMillis(long currentTimeMillis){
        return Long.MAX_VALUE - (currentTimeMillis / ROUNDING_CONSTANT);
    }

    public static long reverseRoundedCurrentTimeMillis(){
        return reverseRoundedTimeMillis(System.currentTimeMillis());
    }
}
