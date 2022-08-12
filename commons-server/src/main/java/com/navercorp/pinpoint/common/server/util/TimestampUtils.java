package com.navercorp.pinpoint.common.server.util;

public class TimestampUtils {

    private static final long TIMESLOT_SIZE = 3_600_000; // 1hour

    private static final TimeSlot timeSlot = new DefaultTimeSlot(TIMESLOT_SIZE);

    public static long reverseRoundedTimeMillis(long currentTimeMillis) {
        return Long.MAX_VALUE - timeSlot.getTimeSlot(currentTimeMillis);
    }

    public static long reverseRoundedCurrentTimeMillis() {
        return reverseRoundedTimeMillis(System.currentTimeMillis());
    }

    public static long getTimeslotSize() {
        return TIMESLOT_SIZE;
    }
}
