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
