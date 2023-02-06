package com.navercorp.pinpoint.profiler.context.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class ExceptionWrapper {
    private final String exceptionClassName;
    private final String exceptionMessage;
    private final StackTraceElementWrapper[] stackTraceElements;

    private ExceptionWrapper(Throwable throwable){
        Objects.requireNonNull(throwable);
        this.exceptionClassName = throwable.getClass().getSimpleName();
        this.exceptionMessage = throwable.getMessage();
        this.stackTraceElements = StackTraceElementWrapper.valueOf(throwable.getStackTrace());
    }

    public static ExceptionWrapper[] newExceptions(Throwable throwable) {
        if (throwable == null) {
            return new ExceptionWrapper[0];
        }
        List<ExceptionWrapper> exceptionWrappers = new ArrayList<>();
        Throwable curr = throwable;
        while (curr.getCause() != null) {
            exceptionWrappers.add(new ExceptionWrapper(curr));
            curr = curr.getCause();
        }
        return exceptionWrappers.toArray(new ExceptionWrapper[0]);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionWrapper that = (ExceptionWrapper) o;

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
        return result;
    }

    @Override
    public String toString() {
        return "ExceptionWrapper{" +
                "exceptionClassName='" + exceptionClassName + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", stackTraceElements=" + Arrays.toString(stackTraceElements) +
                '}';
    }
}
