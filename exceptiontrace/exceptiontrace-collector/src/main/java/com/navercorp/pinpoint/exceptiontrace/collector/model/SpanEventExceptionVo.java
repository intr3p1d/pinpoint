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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author intr3p1d
 */
public class SpanEventExceptionVo {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SpanEventException spanEventException;
    private List<String> stackTrace;


    public SpanEventExceptionVo(
            SpanEventException spanEventException
    ) {
        this.spanEventException = spanEventException;
    }

    public static SpanEventExceptionVo valueOf(SpanEventException spanEventException) {
        return new SpanEventExceptionVo(
                spanEventException
        );
    }

    private static List<String> toJsonString(List<StackTraceElementWrapper> stackTrace) {
        List<String> strings = new ArrayList<>();
        stackTrace.forEach(
                (StackTraceElementWrapper s) -> {
                    try {
                        strings.add(OBJECT_MAPPER.writeValueAsString(s));
                    } catch (JsonProcessingException ignored) {
                        // do nothing
                    }
                }
        );
        return strings;
    }

    public long getTimestamp() {
        return this.spanEventException.getTimestamp();
    }

    public String getTransactionId() {
        return this.spanEventException.getTransactionId();
    }

    public long getSpanId() {
        return this.spanEventException.getSpanId();
    }

    public long getExceptionId() {
        return this.spanEventException.getExceptionId();
    }

    public String getApplicationServiceType() {
        return this.spanEventException.getApplicationServiceType();
    }

    public String getApplicationName() {
        return this.spanEventException.getApplicationName();
    }

    public String getAgentId() {
        return this.spanEventException.getAgentId();
    }

    public String getUriTemplate() {
        return this.spanEventException.getUriTemplate();
    }

    public String getErrorClassName() {
        return this.spanEventException.getErrorClassName();
    }

    public String getErrorMessage() {
        return this.spanEventException.getErrorMessage();
    }

    public int getExceptionDepth() {
        return this.spanEventException.getExceptionDepth();
    }

    public List<String> getStackTrace() {
        if (stackTrace == null) {
            stackTrace = toJsonString(this.spanEventException.getStackTrace());
        }
        return stackTrace;
    }

    public String getStackTraceHash() {
        return this.spanEventException.getStackTraceHash();
    }

    @Override
    public String toString() {
        return "SpanEventExceptionVo{" +
                "spanEventException=" + spanEventException +
                ", stackTrace=" + stackTrace +
                '}';
    }
}
