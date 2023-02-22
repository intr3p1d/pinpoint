package com.navercorp.pinpoint.profiler.context.exception;

import javax.annotation.Nullable;

/**
 * @author intr3p1d
 */
public class ExceptionRecordingContext {
    private Throwable previous = null;
    private long startTime = 0;

    public static ExceptionRecordingContext newContext() {
        return new ExceptionRecordingContext();
    }

    public void resetStartTime() {
        setStartTime(0);
    }

    public Throwable getPrevious() {
        return previous;
    }

    public void setPrevious(@Nullable Throwable previous) {
        this.previous = previous;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
