package com.navercorp.pinpoint.metric.web.util;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;

import java.util.concurrent.TimeUnit;

/**
 * @author intr3p1d
 */
public class ExceptionTraceQueryParameter extends QueryParameter {
    private final String applicationName;
    private final String agentId;
    private final SpanEventException spanEventException;

    protected ExceptionTraceQueryParameter(Builder builder) {
        super(builder.range, builder.timePrecision, builder.limit);
        this.applicationName = builder.applicationName;
        this.agentId = builder.agentId;
        this.spanEventException = builder.spanEventException;
    }

    public static class Builder extends QueryParameter.Builder {
        private String applicationName;
        private String agentId;
        private SpanEventException spanEventException;

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public void setSpanEventException(SpanEventException spanEventException) {
            this.spanEventException = spanEventException;
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
