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

package com.navercorp.pinpoint.exceptiontrace.collector.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author intr3p1d
 */
public class SpanEventExceptionVo {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final long timestamp;

    private final String transactionId;
    private final long spanId;

    private final String applicationServiceType;
    private final String applicationName;
    private final String agentId;

    private final String errorClassName;
    private final String errorMessage;
    private final int exceptionDepth;
    private final List<String> stackTrace;
    private final byte[] stackTraceHash;


    public SpanEventExceptionVo(long timestamp, String transactionId, long spanId, String applicationServiceType, String applicationName, String agentId, String errorClassName, String errorMessage, int exceptionDepth, List<String> stackTrace, byte[] stackTraceHash) {
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.spanId = spanId;
        this.applicationServiceType = applicationServiceType;
        this.applicationName = applicationName;
        this.agentId = agentId;
        this.errorClassName = errorClassName;
        this.errorMessage = errorMessage;
        this.exceptionDepth = exceptionDepth;
        this.stackTrace = stackTrace;
        this.stackTraceHash = stackTraceHash;
    }


    public static SpanEventExceptionVo valueOf(SpanEventException spanEventException) {
        return new SpanEventExceptionVo(
                spanEventException.getTimestamp(),
                spanEventException.getTransactionId(),
                spanEventException.getSpanId(),
                spanEventException.getApplicationServiceType(),
                spanEventException.getApplicationName(),
                spanEventException.getAgentId(),
                spanEventException.getErrorClassName(),
                spanEventException.getErrorMessage(),
                spanEventException.getExceptionDepth(),
                toJsonString(spanEventException.getStackTrace()),
                spanEventException.getStackTraceHash()
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
        return timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getSpanId() {
        return spanId;
    }

    public String getApplicationServiceType() {
        return applicationServiceType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getErrorClassName() {
        return errorClassName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getExceptionDepth() {
        return exceptionDepth;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public byte[] getStackTraceHash() {
        return stackTraceHash;
    }

    @Override
    public String toString() {
        return "SpanEventExceptionVo{" +
                "timestamp=" + timestamp +
                ", transactionId='" + transactionId + '\'' +
                ", spanId=" + spanId +
                ", applicationServiceType='" + applicationServiceType + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", agentId='" + agentId + '\'' +
                ", errorClassName='" + errorClassName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", exceptionDepth=" + exceptionDepth +
                ", stackTrace=" + stackTrace +
                ", stackTraceHash=" + Arrays.toString(stackTraceHash) +
                '}';
    }
}
