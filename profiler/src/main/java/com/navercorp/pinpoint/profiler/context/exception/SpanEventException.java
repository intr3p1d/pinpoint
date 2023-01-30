package com.navercorp.pinpoint.profiler.context.exception;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class SpanEventException {

    private final String exceptionClassName;
    private final String exceptionMessage;
    private final StackTraceElementWrapper[] stackTraceElements;

    private final long startTime;

    public SpanEventException(Throwable throwable, long startTime) {
        Objects.requireNonNull(throwable);
        this.exceptionClassName = throwable.getClass().getSimpleName();
        this.exceptionMessage = throwable.getMessage();
        this.stackTraceElements = StackTraceElementWrapper.valueOf(throwable.getStackTrace());
        this.startTime = startTime;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public StackTraceElementWrapper[] getStackTraceElements() {
        return stackTraceElements;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpanEventException that = (SpanEventException) o;

        if (startTime != that.startTime) return false;
        if (!exceptionClassName.equals(that.exceptionClassName)) return false;
        if (!exceptionMessage.equals(that.exceptionMessage)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(stackTraceElements, that.stackTraceElements);
    }

    @Override
    public int hashCode() {
        int result = exceptionClassName.hashCode();
        result = 31 * result + exceptionMessage.hashCode();
        result = 31 * result + Arrays.hashCode(stackTraceElements);
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SpanEventException{" +
                "exceptionClassName='" + exceptionClassName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", stackTraceElements=" + Arrays.toString(stackTraceElements) +
                ", startTime=" + startTime +
                '}';
    }
}
