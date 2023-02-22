package com.navercorp.pinpoint.profiler.context.exception;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class SpanEventException {

    private final ExceptionWrapper[] exceptionWrappers;

    private final long startTime;

    private SpanEventException(Throwable throwable, long startTime) {
        Objects.requireNonNull(throwable);
        this.exceptionWrappers = ExceptionWrapper.newExceptions(throwable);
        this.startTime = startTime;
    }

    public static SpanEventException newSpanEventException(Throwable throwable, long startTime) {
        return new SpanEventException(throwable, startTime);
    }

    public long getStartTime() {
        return startTime;
    }

    public ExceptionWrapper[] getExceptionWrappers() {
        return exceptionWrappers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpanEventException that = (SpanEventException) o;

        if (startTime != that.startTime) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(exceptionWrappers, that.exceptionWrappers);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(exceptionWrappers);
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SpanEventException{" +
                "exceptionWrappers=" + Arrays.toString(exceptionWrappers) +
                ", startTime=" + startTime +
                '}';
    }
}
