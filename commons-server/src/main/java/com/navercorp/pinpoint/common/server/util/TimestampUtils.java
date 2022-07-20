package com.navercorp.pinpoint.common.server.util;

public class TimestampUtils {
    public static long reverseRoundedTimeMillis(long currentTimeMillis){
        return Long.MAX_VALUE - (currentTimeMillis / 3_600_000 * 6);
    }

    public static long reverseRoundedCurrentTimeMillis(){
        return reverseRoundedTimeMillis(System.currentTimeMillis());
    }
}
