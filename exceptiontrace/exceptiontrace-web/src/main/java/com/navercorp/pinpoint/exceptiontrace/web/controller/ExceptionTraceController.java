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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final TimeWindowSampler DEFAULT_TIME_WINDOW_SAMPLER = new TimeWindowSlotCentricSampler(30000L, 200);
    private final TenantProvider tenantProvider;

    public ExceptionTraceController(ExceptionTraceService exceptionTraceService, TenantProvider tenantProvider) {
        this.exceptionTraceService = Objects.requireNonNull(exceptionTraceService, "exceptionTraceService");
        this.tenantProvider = Objects.requireNonNull(tenantProvider, "tenantProvider");
    }

    @GetMapping("/transactionInfo")
    public List<SpanEventException> getSpanEventExceptionFromTransactionId(
            @RequestParam("applicationName") String applicationName,
            @RequestParam("traceId") String traceId,
            @RequestParam("traceTimestamp") long timestamp
    ) {
        return exceptionTraceService.getTransactionExceptions(
                applicationName, traceId, timestamp
        );
    }

    @GetMapping("/errorList")
    public List<SpanEventException> getListOfSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        return exceptionTraceService.getExceptionsInRange(
                applicationName, agentId, from, to
        );
    }

    @GetMapping(value = "/errorList", params = {"traceId", "traceTimestamp", "traceDepth"})
    public List<SpanEventException> getListOfSpanEventExceptionForSpecificException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("traceId") String traceId,
            @RequestParam("traceTimestamp") long timestamp,
            @RequestParam("traceDepth") int depth,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        return exceptionTraceService.getSimilarExceptions(
                agentId, traceId, timestamp, depth, applicationName, from, to
        );
    }


    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);
        List<SpanEventException> spanEventExceptions= exceptionTraceService.getExceptionsInRange(
                applicationName, agentId, from, to
        );
        return new ExceptionTraceView("", timeWindow, spanEventExceptions);
    }

    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventExceptionForSpecificException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("traceId") String traceId,
            @RequestParam("traceTimestamp") long timestamp,
            @RequestParam("traceDepth") int depth,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);
        List<SpanEventException> spanEventExceptions = exceptionTraceService.getSimilarExceptions(
                agentId, traceId, timestamp, depth, applicationName, from, to
        );
        return new ExceptionTraceView("", timeWindow, spanEventExceptions);
    }

    private List<SpanEventException> getSpanEventExceptions(
            String applicationName,
            String agentId,
            long from,
            long to
    ) {
        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder(
                applicationName,
                Range.newRange(from, to)
        );
        builder.setAgentId(agentId);

        return exceptionTraceService.getSpanEventExceptions(builder.build());
    }
}
