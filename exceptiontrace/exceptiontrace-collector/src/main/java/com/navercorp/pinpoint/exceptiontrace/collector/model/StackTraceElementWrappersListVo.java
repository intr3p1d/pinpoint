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
package com.navercorp.pinpoint.exceptiontrace.collector.model;

import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;

import java.util.List;

/**
 * @author intr3p1d
 */
public class StackTraceElementWrappersListVo {
    private final List<StackTraceElementWrapper> stackTrace;


    public StackTraceElementWrappersListVo(List<StackTraceElementWrapper> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Iterable<String> getStackTraceClassName() {
        return stackTrace.stream()
                .map(StackTraceElementWrapper::getClassName)::iterator;
    }

    public Iterable<String> getStackTraceFileName() {
        return stackTrace.stream()
                .map(StackTraceElementWrapper::getFileName)::iterator;
    }

    public Iterable<Integer> getStackTraceLineNumber() {
        return stackTrace.stream()
                .map(StackTraceElementWrapper::getLineNumber)::iterator;
    }

    public Iterable<String> getStackTraceMethodName() {
        return stackTrace.stream()
                .map(StackTraceElementWrapper::getMethodName)::iterator;
    }

}
