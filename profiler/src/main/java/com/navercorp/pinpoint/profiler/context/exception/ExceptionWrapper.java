/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        while (curr != null) {
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
        if (!(o instanceof ExceptionWrapper)) return false;

        ExceptionWrapper that = (ExceptionWrapper) o;

        if (!Objects.equals(exceptionClassName, that.exceptionClassName))
            return false;
        if (!Objects.equals(exceptionMessage, that.exceptionMessage))
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(stackTraceElements, that.stackTraceElements);
    }

    @Override
    public int hashCode() {
        int result = exceptionClassName != null ? exceptionClassName.hashCode() : 0;
        result = 31 * result + (exceptionMessage != null ? exceptionMessage.hashCode() : 0);
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
