package com.navercorp.pinpoint.exceptiontrace.collector.dao;

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

    private final long timestamp;

    private final String transactionId;
    private final long spanId;

    private final String applicationServiceType;
    private final String applicationName;
    private final String agentId;

    private final String errorClassName;
    private final String errorMessage;

    private final List<String> stackTrace;


    public SpanEventExceptionVo(long timestamp, String transactionId, long spanId, String applicationServiceType, String applicationName, String agentId, String errorClassName, String errorMessage, List<String> stackTrace) {
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.spanId = spanId;
        this.applicationServiceType = applicationServiceType;
        this.applicationName = applicationName;
        this.agentId = agentId;
        this.errorClassName = errorClassName;
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
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
                toJsonString(spanEventException.getStackTrace())
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

    public List<String> getStackTrace() {
        return stackTrace;
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
                ", stackTrace=" + stackTrace +
                '}';
    }
}
