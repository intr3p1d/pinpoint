package com.navercorp.pinpoint.common.server.bo.exception;

import java.util.List;

/**
 * @author intr3p1d
 */
public class SpanEventExceptionBo {
    private String exceptionClassName;
    private String exceptionMessage;
    private List<StackTraceElementWrapperBo> stackTraceElements;
    private long startTime;

    public SpanEventExceptionBo() {
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public List<StackTraceElementWrapperBo> getStackTraceElements() {
        return stackTraceElements;
    }

    public void setStackTraceElements(List<StackTraceElementWrapperBo> stackTraceElements) {
        this.stackTraceElements = stackTraceElements;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "SpanEventExceptionBo{" +
                "exceptionClassName='" + exceptionClassName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", stackTraceElements=" + stackTraceElements +
                ", startTime=" + startTime +
                '}';
    }
}