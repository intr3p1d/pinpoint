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

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.util.QueryParameter;
import com.navercorp.pinpoint.metric.web.util.Range;
import com.navercorp.pinpoint.metric.web.util.TimePrecision;

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
    private final long spanEventTimestamp;
    private final int exceptionDepth;

    protected ExceptionTraceQueryParameter(Builder builder) {
        super(builder.getRange(), builder.getTimePrecision(), builder.getLimit());
        this.applicationName = builder.applicationName;
        this.agentId = builder.agentId;
        this.spanEventException = builder.spanEventException;

        if (builder.transactionId != null) {
            this.transactionId = TransactionIdUtils.formatString(builder.transactionId);
        } else {
            this.transactionId = null;
        }
        this.spanEventTimestamp = builder.spanEventTimestamp;
        this.exceptionDepth = builder.exceptionDepth;
    }

    public static class Builder extends QueryParameter.Builder {
        private final String applicationName;
        private String agentId = null;

        private SpanEventException spanEventException = null;

        private TransactionId transactionId = null;
        private long spanEventTimestamp = Long.MIN_VALUE;
        private int exceptionDepth = Integer.MIN_VALUE;

        public Builder(
                String applicationName,
                Range range
        ) {
            this.applicationName = applicationName;
            setRange(range);
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public void setSpanEventException(SpanEventException spanEventException) {
            this.spanEventException = Objects.requireNonNull(spanEventException, "spanEventException");
        }

        public void forFindingSpecificException(TransactionId transactionId, long spanEventTimestamp, int exceptionDepth) {
            this.transactionId = Objects.requireNonNull(transactionId, "transactionId");
            this.spanEventTimestamp = spanEventTimestamp;
            this.exceptionDepth = exceptionDepth;
        }

        public void setTransactionId(TransactionId transactionId) {
            this.transactionId = Objects.requireNonNull(transactionId, "transactionId");
        }

        public void setSpanEventTimestamp(long spanEventTimestamp) {
            this.spanEventTimestamp = spanEventTimestamp;
        }

        public void setExceptionDepth(int exceptionDepth) {
            this.exceptionDepth = exceptionDepth;
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
