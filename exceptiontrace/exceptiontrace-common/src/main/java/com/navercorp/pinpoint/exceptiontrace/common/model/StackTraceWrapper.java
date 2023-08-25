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
package com.navercorp.pinpoint.exceptiontrace.common.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author intr3p1d
 */
public class StackTraceWrapper {
    private List<StackTraceElementWrapper> stackTrace;

    public StackTraceWrapper(List<StackTraceElementWrapper> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public StackTraceWrapper(
            List<String> stackTraceClassName,
            List<String> stackTraceFileName,
            List<Integer> stackTraceLineNumber,
            List<String> stackTraceMethodName
    ) {
        List<StackTraceElementWrapper> wrappers = new ArrayList<>();
        for (int i = 0; i < stackTraceClassName.size(); i++) {
            wrappers.add(
                    new StackTraceElementWrapper(
                            stackTraceClassName.get(i),
                            stackTraceFileName.get(i),
                            stackTraceLineNumber.get(i),
                            stackTraceMethodName.get(i)
                    )
            );
        }
        this.stackTrace = wrappers;
    }

    public List<StackTraceElementWrapper> getStackTrace() {
        return stackTrace;
    }

    @Override
    public String toString() {
        return "StackTraceWrapper{" +
                "stackTrace=" + stackTrace +
                '}';
    }
}
