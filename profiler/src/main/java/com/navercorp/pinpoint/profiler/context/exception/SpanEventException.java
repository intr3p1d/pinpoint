package com.navercorp.pinpoint.profiler.context.exception;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class SpanEventException {

    private final String exceptionClassName;
    private final String exceptionMessage;
    private final List<StackTraceElementWrapper> stackTraceElements;

    private long startTime;
    private int elapsedTime;

    public SpanEventException(Throwable throwable) {
        Objects.requireNonNull(throwable);
        this.exceptionClassName = throwable.getClass().getSimpleName();
        this.exceptionMessage = throwable.getMessage();
        this.stackTraceElements = StackTraceElementWrapper.valueOf(throwable.getStackTrace());
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public List<StackTraceElementWrapper> getStackTraceElements() {
        return stackTraceElements;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
