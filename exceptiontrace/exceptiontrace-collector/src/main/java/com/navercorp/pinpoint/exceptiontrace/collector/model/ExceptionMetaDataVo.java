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
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author intr3p1d
 */
public class ExceptionMetaDataVo {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ExceptionMetaData exceptionMetaData;
    private List<String> stackTrace;


    public ExceptionMetaDataVo(
            ExceptionMetaData exceptionMetaData
    ) {
        this.exceptionMetaData = exceptionMetaData;
    }

    public static ExceptionMetaDataVo valueOf(ExceptionMetaData exceptionMetaData) {
        return new ExceptionMetaDataVo(
                exceptionMetaData
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
        return this.exceptionMetaData.getTimestamp();
    }

    public String getTransactionId() {
        return this.exceptionMetaData.getTransactionId();
    }

    public long getSpanId() {
        return this.exceptionMetaData.getSpanId();
    }

    public long getExceptionId() {
        return this.exceptionMetaData.getExceptionId();
    }

    public String getApplicationServiceType() {
        return this.exceptionMetaData.getApplicationServiceType();
    }

    public String getApplicationName() {
        return this.exceptionMetaData.getApplicationName();
    }

    public String getAgentId() {
        return this.exceptionMetaData.getAgentId();
    }

    public String getUriTemplate() {
        return this.exceptionMetaData.getUriTemplate();
    }

    public String getErrorClassName() {
        return this.exceptionMetaData.getErrorClassName();
    }

    public String getErrorMessage() {
        return this.exceptionMetaData.getErrorMessage();
    }

    public int getExceptionDepth() {
        return this.exceptionMetaData.getExceptionDepth();
    }

    public List<String> getStackTrace() {
        if (stackTrace == null) {
            stackTrace = toJsonString(this.exceptionMetaData.getStackTrace());
        }
        return stackTrace;
    }

    public String getStackTraceHash() {
        return this.exceptionMetaData.getStackTraceHash();
    }

    @Override
    public String toString() {
        return "ExceptionMetaDataVo{" +
                "exceptionMetaData=" + exceptionMetaData +
                ", stackTrace=" + stackTrace +
                '}';
    }
}
