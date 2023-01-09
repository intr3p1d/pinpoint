package com.navercorp.pinpoint.metric.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.bo.exception.StackTraceElementWrapperBo;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class SpanEventException {
    private static final long EMPTY_NUMBER = 0L;
    private static final String EMPTY_STRING = "null";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final long timestamp;

    private final String transactionId;
    private final long spanId;

    private final String applicationServiceType;
    private final String applicationName;
    private final String agentId;

    private final String errorClassName;
    private final String errorMessage;
    private final String stackTrace;

    public SpanEventException(long timestamp, String transactionId, long spanId, String applicationServiceType, String applicationName, String agentId, String errorClassName, String errorMessage, String stackTrace) {
        this.timestamp = timestamp;
        this.transactionId = StringPrecondition.requireHasLength(transactionId, "transactionId");
        this.spanId = spanId;
        this.applicationServiceType = StringPrecondition.requireHasLength(applicationServiceType, "applicationServiceType");
        this.applicationName = StringPrecondition.requireHasLength(applicationName, "applicationName");
        this.agentId = StringPrecondition.requireHasLength(agentId, "agentId");
        this.errorClassName = StringPrecondition.requireHasLength(errorClassName, "errorClassName");
        this.errorMessage = StringPrecondition.requireHasLength(errorMessage, "errorMessage");
        this.stackTrace = stackTrace;
    }

    public SpanEventException(long timestamp, String transactionId, long spanId,
                              String applicationServiceType, String applicationName, String agentId,
                              String errorClassName, String errorMessage,
                              List<StackTraceElementWrapperBo> stackTraceElementWrapperBos) {
        this.timestamp = timestamp;
        this.transactionId = StringPrecondition.requireHasLength(transactionId, "transactionId");
        this.spanId = spanId;
        this.applicationServiceType = StringPrecondition.requireHasLength(applicationServiceType, "applicationServiceType");
        this.applicationName = StringPrecondition.requireHasLength(applicationName, "applicationName");
        this.agentId = StringPrecondition.requireHasLength(agentId, "agentId");
        this.errorClassName = StringPrecondition.requireHasLength(errorClassName, "errorClassName");
        this.errorMessage = StringPrecondition.requireHasLength(errorMessage, "errorMessage");
        this.stackTrace = toStackTrace(stackTraceElementWrapperBos);
    }

    public static String toStackTrace(List<StackTraceElementWrapperBo> stackTraceElementWrapperBos) {
        try {
            return OBJECT_MAPPER.writeValueAsString(stackTraceElementWrapperBos);
        } catch (JsonProcessingException jsonProcessingException) {
            return EMPTY_STRING;
        }
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

    public String getStackTrace() {
        return stackTrace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpanEventException that = (SpanEventException) o;
        return applicationName.equals(that.applicationName) && agentId.equals(that.agentId) && errorClassName.equals(that.errorClassName) && errorMessage.equals(that.errorMessage) && stackTrace.equals(that.stackTrace);
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
                ", stackTrace='" + stackTrace + '\'' +
                '}';
    }
}