package com.navercorp.pinpoint.common.server.bo.exception;

import java.util.List;

/**
 * @author intr3p1d
 */
public class ExceptionWrapperBo {
    private String exceptionClassName;
    private String exceptionMessage;
    private List<StackTraceElementWrapperBo> stackTraceElements;

    public ExceptionWrapperBo (
            String exceptionClassName,
            String exceptionMessage,
            List<StackTraceElementWrapperBo> stackTraceElements
    ) {
        this.exceptionClassName = exceptionClassName;
        this.exceptionMessage = exceptionMessage;
        this.stackTraceElements = stackTraceElements;
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

    @Override
    public String toString() {
        return "ExceptionWrapperBo{" +
                "exceptionClassName='" + exceptionClassName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", stackTraceElements=" + stackTraceElements +
                '}';
    }
}
