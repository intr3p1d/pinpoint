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

import com.navercorp.pinpoint.common.server.bo.exception.StackTraceElementWrapperBo;
import com.navercorp.pinpoint.exceptiontrace.common.util.HashUtils;
import com.navercorp.pinpoint.exceptiontrace.common.util.StringPrecondition;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class SpanEventException {

    private final long timestamp;

    private final String transactionId;
    private final long spanId;
    private final long exceptionId;

    private final String applicationServiceType;
    private final String applicationName;
    private final String agentId;
    private final String uriTemplate;

    private final String errorClassName;
    private final String errorMessage;
    private final int exceptionDepth;
    private final List<StackTraceElementWrapper> stackTrace;

    private final String stackTraceHash;

    public SpanEventException(
            long timestamp,
            String transactionId,
            long spanId,
            long exceptionId,
            String applicationServiceType,
            String applicationName,
            String agentId,
            String uriTemplate,
            String errorClassName,
            String errorMessage,
            int exceptionDepth,
            List<StackTraceElementWrapper> stackTrace,
            String stackTraceHash
    ) {
        this.timestamp = timestamp;
        this.transactionId = StringPrecondition.requireHasLength(transactionId, "transactionId");
        this.spanId = spanId;
        this.exceptionId = exceptionId;
        this.applicationServiceType = StringPrecondition.requireHasLength(applicationServiceType, "applicationServiceType");
        this.applicationName = StringPrecondition.requireHasLength(applicationName, "applicationName");
        this.agentId = StringPrecondition.requireHasLength(agentId, "agentId");
        this.uriTemplate = StringPrecondition.requireHasLength(uriTemplate, "uriTemplate");
        this.errorClassName = StringPrecondition.requireHasLength(errorClassName, "errorClassName");
        this.errorMessage = StringPrecondition.requireHasLength(errorMessage, "errorMessage");
        this.exceptionDepth = exceptionDepth;
        this.stackTrace = stackTrace;
        this.stackTraceHash = stackTraceHash;
    }

    public SpanEventException(
            long timestamp,
            String transactionId,
            long spanId,
            long exceptionId,
            String applicationServiceType,
            String applicationName,
            String agentId,
            String uriTemplate,
            String errorClassName,
            String errorMessage,
            int exceptionDepth,
            String stackTraceHash
    ) {
        this.timestamp = timestamp;
        this.transactionId = StringPrecondition.requireHasLength(transactionId, "transactionId");
        this.spanId = spanId;
        this.exceptionId = exceptionId;
        this.applicationServiceType = StringPrecondition.requireHasLength(applicationServiceType, "applicationServiceType");
        this.applicationName = StringPrecondition.requireHasLength(applicationName, "applicationName");
        this.agentId = StringPrecondition.requireHasLength(agentId, "agentId");
        this.uriTemplate = StringPrecondition.requireHasLength(uriTemplate, "uriTemplate");
        this.errorClassName = StringPrecondition.requireHasLength(errorClassName, "errorClassName");
        this.errorMessage = StringPrecondition.requireHasLength(errorMessage, "errorMessage");
        this.exceptionDepth = exceptionDepth;
        this.stackTrace = Collections.emptyList();
        this.stackTraceHash = stackTraceHash;
    }

    public static SpanEventException valueOf(
            long timestamp, String transactionId, long spanId, long exceptionId,
            String applicationServiceType, String applicationName, String agentId,
            String uriTemplate,
            String errorClassName, String errorMessage, int exceptionDepth,
            List<StackTraceElementWrapperBo> stackTraceElementWrapperBos
    ) {
        List<StackTraceElementWrapper> wrappers = toStackTrace(stackTraceElementWrapperBos);

        return new SpanEventException(
                timestamp,
                transactionId,
                spanId,
                exceptionId,
                applicationServiceType,
                applicationName,
                agentId,
                uriTemplate,
                errorClassName,
                errorMessage,
                exceptionDepth,
                wrappers,
                toStackTraceHash(wrappers)
        );
    }

    public static List<StackTraceElementWrapper> toStackTrace(List<StackTraceElementWrapperBo> stackTraceElementWrapperBos) {
        return stackTraceElementWrapperBos.stream().map(
                (StackTraceElementWrapperBo s) -> new StackTraceElementWrapper(s.getClassName(), s.getFileName(), s.getLineNumber(), s.getMethodName())
        ).collect(Collectors.toList());
    }

    public static String toStackTraceHash(List<StackTraceElementWrapper> stackTraceElementWrappers) {
        return HashUtils.wrappersToHashString(stackTraceElementWrappers);
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

    public long getExceptionId() {
        return exceptionId;
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

    public String getUriTemplate() {
        return uriTemplate;
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

    public List<StackTraceElementWrapper> getStackTrace() {
        return stackTrace;
    }

    public String getStackTraceHash() {
        return stackTraceHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpanEventException)) return false;

        SpanEventException that = (SpanEventException) o;

        if (!Objects.equals(errorClassName, that.errorClassName))
            return false;
        if (!Objects.equals(errorMessage, that.errorMessage)) return false;
        return Objects.equals(stackTraceHash, that.stackTraceHash);
    }

    @Override
    public int hashCode() {
        int result = errorClassName != null ? errorClassName.hashCode() : 0;
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        result = 31 * result + (stackTraceHash != null ? stackTraceHash.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SpanEventException{" +
                "timestamp=" + timestamp +
                ", transactionId='" + transactionId + '\'' +
                ", spanId=" + spanId +
                ", exceptionId=" + exceptionId +
                ", applicationServiceType='" + applicationServiceType + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", agentId='" + agentId + '\'' +
                ", uriTemplate='" + uriTemplate + '\'' +
                ", errorClassName='" + errorClassName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", exceptionDepth=" + exceptionDepth +
                ", stackTrace=" + stackTrace +
                ", stackTraceHash='" + stackTraceHash + '\'' +
                '}';
    }
}
