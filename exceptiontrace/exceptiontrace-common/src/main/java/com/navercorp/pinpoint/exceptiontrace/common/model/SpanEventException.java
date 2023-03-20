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

    private final String applicationServiceType;
    private final String applicationName;
    private final String agentId;

    private final String errorClassName;
    private final String errorMessage;
    private final int exceptionDepth;
    private final List<StackTraceElementWrapper> stackTrace;

    private final String stackTraceHash;

    public SpanEventException(
            long timestamp,
            String transactionId,
            long spanId,
            String applicationServiceType,
            String applicationName,
            String agentId,
            String errorClassName,
            String errorMessage,
            int exceptionDepth,
            List<StackTraceElementWrapper> stackTrace,
            String stackTraceHash
    ) {
        this.timestamp = timestamp;
        this.transactionId = StringPrecondition.requireHasLength(transactionId, "transactionId");
        this.spanId = spanId;
        this.applicationServiceType = StringPrecondition.requireHasLength(applicationServiceType, "applicationServiceType");
        this.applicationName = StringPrecondition.requireHasLength(applicationName, "applicationName");
        this.agentId = StringPrecondition.requireHasLength(agentId, "agentId");
        this.errorClassName = StringPrecondition.requireHasLength(errorClassName, "errorClassName");
        this.errorMessage = StringPrecondition.requireHasLength(errorMessage, "errorMessage");
        this.exceptionDepth = exceptionDepth;
        this.stackTrace = stackTrace;
        this.stackTraceHash = stackTraceHash;
    }

    public static SpanEventException valueOf(long timestamp, String transactionId, long spanId,
                                             String applicationServiceType, String applicationName, String agentId,
                                             String errorClassName, String errorMessage, int exceptionDepth,
                                             List<StackTraceElementWrapperBo> stackTraceElementWrapperBos) {
        List<StackTraceElementWrapper> wrappers = toStackTrace(stackTraceElementWrapperBos);

        return new SpanEventException(
                timestamp,
                transactionId,
                spanId,
                applicationServiceType,
                applicationName,
                agentId,
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

        if (timestamp != that.timestamp) return false;
        if (spanId != that.spanId) return false;
        if (!transactionId.equals(that.transactionId)) return false;
        if (!applicationServiceType.equals(that.applicationServiceType)) return false;
        if (!applicationName.equals(that.applicationName)) return false;
        if (!agentId.equals(that.agentId)) return false;
        if (!errorClassName.equals(that.errorClassName)) return false;
        if (!errorMessage.equals(that.errorMessage)) return false;
        return stackTrace.equals(that.stackTrace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationName, agentId, errorClassName, errorMessage, stackTrace);
    }

    @Override
    public String toString() {
        return "SpanEventException{" +
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
                ", stackTraceHash='" + stackTraceHash + '\'' +
                '}';
    }
}
