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

package com.navercorp.pinpoint.exceptiontrace.web.controller;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
import com.navercorp.pinpoint.common.server.tenant.TenantProvider;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.service.ExceptionTraceService;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import com.navercorp.pinpoint.metric.web.util.Range;
import com.navercorp.pinpoint.metric.web.util.TimeWindow;
import com.navercorp.pinpoint.metric.web.util.TimeWindowSampler;
import com.navercorp.pinpoint.metric.web.util.TimeWindowSlotCentricSampler;
import com.navercorp.pinpoint.exceptiontrace.web.view.ExceptionTraceView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@RestController
@RequestMapping(value = "/exceptionTrace")
public class ExceptionTraceController {
    private final ExceptionTraceService exceptionTraceService;

    private final TimeWindowSampler DEFAULT_TIME_WINDOW_SAMPLER = new TimeWindowSlotCentricSampler(30000L, 200);
    private final TenantProvider tenantProvider;

    public ExceptionTraceController(ExceptionTraceService exceptionTraceService, TenantProvider tenantProvider) {
        this.exceptionTraceService = Objects.requireNonNull(exceptionTraceService, "exceptionTraceService");
        this.tenantProvider = Objects.requireNonNull(tenantProvider, "tenantProvider");
    }

    @GetMapping("/transactionInfo")
    public SpanEventException getSpanEventExceptionFromTransactionId(
            @RequestParam("traceId") String traceId,
            @RequestParam(value = "traceTimestamp", required = false, defaultValue = "0") long timestamp
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);
        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder();
        transactionBuilder.setAgentId(transactionId.getAgentId());
        transactionBuilder.setTransactionId(transactionId);
        transactionBuilder.setSpanEventTimestamp(timestamp);
        return exceptionTraceService.getExactSpanEventException(transactionBuilder.build());
    }

    @GetMapping("/errorList")
    public List<SpanEventException> getListOfSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam(value = "traceId", required = false) String traceId,
            @RequestParam(value = "traceTimestamp", required = false, defaultValue = "0") long timestamp,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        if (traceIdIsGiven(traceId, timestamp)) {
            return getCollectedSpanEventExceptionListByTransactionId(
                    traceId, timestamp, applicationName, from, to
            );
        } else {
            return getAllCollectedSpanEventExceptionList(
                    applicationName, agentId, from, to
            );
        }
    }

    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam(value = "traceId", required = false) String traceId,
            @RequestParam(value = "traceTimestamp", required = false, defaultValue = "0") long timestamp,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);

        List<SpanEventException> spanEventExceptions;
        if (traceIdIsGiven(traceId, timestamp)) {
            spanEventExceptions = getCollectedSpanEventExceptionListByTransactionId(
                    traceId, timestamp, applicationName, from, to
            );
        } else {
            spanEventExceptions = getAllCollectedSpanEventExceptionList(
                    applicationName, agentId, from, to
            );
        }

        return new ExceptionTraceView("", timeWindow, spanEventExceptions);
    }

    private boolean traceIdIsGiven(String traceId, long timestamp) {
        return traceId != null && timestamp != 0;
    }

    private List<SpanEventException> getCollectedSpanEventExceptionListByTransactionId(
            String traceId,
            long timestamp,
            String applicationName,
            long from,
            long to
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);
        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder();
        transactionBuilder.setApplicationName(applicationName);
        transactionBuilder.setAgentId(transactionId.getAgentId());
        transactionBuilder.forFindingSpecificException(transactionId, timestamp, 0);
        final SpanEventException spanEventException = exceptionTraceService.getExactSpanEventException(transactionBuilder.build());

        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder();
        builder.setApplicationName(applicationName);
        builder.setAgentId(transactionId.getAgentId());
        builder.forFindingSameExceptions(spanEventException);
        builder.setRange(Range.newRange(from, to));

        return exceptionTraceService.getCollectedSpanEventException(builder.build());
    }

    private List<SpanEventException> getAllCollectedSpanEventExceptionList(
            String applicationName,
            String agentId,
            long from,
            long to
    ) {
        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder();
        builder.setApplicationName(applicationName);
        builder.setAgentId(agentId);
        builder.setRange(Range.newRange(from, to));

        return exceptionTraceService.getCollectedSpanEventException(builder.build());
    }
}
