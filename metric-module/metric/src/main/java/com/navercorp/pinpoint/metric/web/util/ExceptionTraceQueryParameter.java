package com.navercorp.pinpoint.metric.web.util;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;

import java.util.concurrent.TimeUnit;

/**
 * @author intr3p1d
 */
public class ExceptionTraceQueryParameter extends QueryParameter {
    private final String applicationName;
    private final String agentId;
    private final SpanEventException spanEventException;

    private final TransactionId transactionId;

    private final long spanEventTimestamp;

    protected ExceptionTraceQueryParameter(Builder builder) {
        super(builder.range, builder.timePrecision, builder.limit);
        this.applicationName = builder.applicationName;
        this.agentId = builder.agentId;
        this.spanEventException = builder.spanEventException;
        this.transactionId = builder.transactionId;
        this.spanEventTimestamp = builder.spanEventTimestamp;
    }

    public static class Builder extends QueryParameter.Builder {
        private String applicationName = null;
        private String agentId = null;
        private SpanEventException spanEventException = null;

        private TransactionId transactionId = null;

        private long spanEventTimestamp = 0;

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public void setSpanEventException(SpanEventException spanEventException) {
            this.spanEventException = spanEventException;
        }

        public void setTransactionId(TransactionId transactionId) {
            this.transactionId = transactionId;
        }

        public void setSpanEventTimestamp(long spanEventTimestamp) {
            this.spanEventTimestamp = spanEventTimestamp;
        }

        @Override
        public ExceptionTraceQueryParameter build() {
            if (timePrecision == null) {
                this.timePrecision = TimePrecision.newTimePrecision(TimeUnit.MILLISECONDS, 30000);
            }
            this.limit = estimateLimit();
            return new ExceptionTraceQueryParameter(this);
        }
    }
}