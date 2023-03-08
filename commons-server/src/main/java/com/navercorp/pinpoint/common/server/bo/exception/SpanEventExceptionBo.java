package com.navercorp.pinpoint.common.server.bo.exception;

import java.util.List;

/**
 * @author intr3p1d
 */
public class SpanEventExceptionBo {

    private List<ExceptionWrapperBo> exceptionWrappers;
    private long startTime;

    public SpanEventExceptionBo() {
    }

    public List<ExceptionWrapperBo> getExceptionWrappers() {
        return exceptionWrappers;
    }

    public void setExceptionWrappers(List<ExceptionWrapperBo> exceptionWrappers) {
        this.exceptionWrappers = exceptionWrappers;
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
                "exceptionWrappers=" + exceptionWrappers +
                ", startTime=" + startTime +
                '}';
    }
}