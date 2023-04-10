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

package com.navercorp.pinpoint.exceptiontrace.web.util;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.model.SummaryGroupBy;
import com.navercorp.pinpoint.metric.web.util.QueryParameter;
import com.navercorp.pinpoint.metric.web.util.TimePrecision;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author intr3p1d
 */
public class ExceptionTraceQueryParameter extends QueryParameter {
    private final String applicationName;
    private final String agentId;
    private final SpanEventException spanEventException;

    private final String transactionId;
    private final long spanId;
    private final long exceptionId;
    private final int exceptionDepth;

    private final List<SummaryGroupBy> groupByList;

    private boolean groupByAgentId;
    private boolean groupByUriTemplate;
    private boolean groupByErrorClassName;
    private boolean groupByErrorMessage;
    private boolean groupByStackTrace;


    protected ExceptionTraceQueryParameter(Builder builder) {
        super(builder.getRange(), builder.getTimePrecision(), builder.getLimit());
        this.applicationName = builder.applicationName;
        this.agentId = builder.agentId;
        this.spanEventException = builder.spanEventException;
        this.transactionId = builder.transactionId;
        this.spanId = builder.spanId;
        this.exceptionId = builder.exceptionId;
        this.exceptionDepth = builder.exceptionDepth;
        this.groupByList = builder.groupBIES;
    }

    public static class Builder extends QueryParameter.Builder<Builder> {
        private static final int LIMIT = 65536;
        private String applicationName;
        private String agentId = null;

        private SpanEventException spanEventException = null;

        private String transactionId = null;
        private long spanId = Long.MIN_VALUE;
        private long exceptionId = Long.MIN_VALUE;
        private int exceptionDepth = Integer.MIN_VALUE;

        private List<SummaryGroupBy> groupBIES = Collections.emptyList();

        @Override
        protected Builder self() {
            return this;
        }

        public Builder setApplicationName(String applicationName) {
            this.applicationName = applicationName;
            return self();
        }

        public Builder setExceptionDepth(int exceptionDepth) {
            this.exceptionDepth = exceptionDepth;
            return self();
        }

        public Builder setAgentId(String agentId) {
            this.agentId = agentId;
            return self();
        }

        public Builder setSpanEventException(SpanEventException spanEventException) {
            this.spanEventException = Objects.requireNonNull(spanEventException, "spanEventException");
            return self();
        }

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return self();
        }

        public Builder setSpanId(long spanId) {
            this.spanId = spanId;
            return self();
        }

        public Builder setExceptionId(long exceptionId) {
            this.exceptionId = exceptionId;
            return self();
        }

        public Builder addGroupBy(SummaryGroupBy summaryGroupBy) {
            this.groupBIES.add(summaryGroupBy);
            return self();
        }

        public Builder addAllGroupBies(Collection<SummaryGroupBy> summaryGroupBIES) {
            this.groupBIES.addAll(summaryGroupBIES);
            return self();
        }

        @Override
        public ExceptionTraceQueryParameter build() {
            if (timePrecision == null) {
                this.timePrecision = TimePrecision.newTimePrecision(TimeUnit.MILLISECONDS, 30000);
            }
            if (this.range != null) {
                this.limit = estimateLimit();
            } else {
                this.limit = LIMIT;
            }
            return new ExceptionTraceQueryParameter(this);
        }
    }

    @Override
    public String toString() {
        return "ExceptionTraceQueryParameter{" +
                "applicationName='" + applicationName + '\'' +
                ", agentId='" + agentId + '\'' +
                ", spanEventException=" + spanEventException +
                ", transactionId='" + transactionId + '\'' +
                ", spanId=" + spanId +
                ", exceptionId=" + exceptionId +
                ", exceptionDepth=" + exceptionDepth +
                '}';
    }
}
