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

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
import com.navercorp.pinpoint.exceptiontrace.web.service.ExceptionTraceService;
import com.navercorp.pinpoint.metric.web.util.Range;
import com.navercorp.pinpoint.metric.web.util.TimeWindow;
import com.navercorp.pinpoint.metric.web.util.TimeWindowSampler;
import com.navercorp.pinpoint.metric.web.util.TimeWindowSlotCentricSampler;
import com.navercorp.pinpoint.exceptiontrace.web.view.ExceptionTraceView;
import com.navercorp.pinpoint.pinot.tenant.TenantProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            @RequestParam("agentId") String agentId,
            @RequestParam("traceId") String traceId,
            @RequestParam("spanId") long spanId,
            @RequestParam("exceptionId") long exceptionId
    ) {
        return exceptionTraceService.getTransactionExceptions(
                applicationName, agentId, traceId, spanId, exceptionId
        );
    }

    @GetMapping("/errorList")
    public List<SpanEventException> getListOfSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to,

            @RequestParam("traceId") Optional<String> traceId,
            @RequestParam("spanId") Optional<Long> spanId,
            @RequestParam("exceptionId") Optional<Long> exceptionId,
            @RequestParam("exceptionDepth") Optional<Integer> depth
    ) {
        if (optionalsAreGiven(traceId, spanId, exceptionId, depth)) {
            return exceptionTraceService.getSimilarExceptions(
                    applicationName, agentId, from, to,
                    traceId.get(), spanId.get(), exceptionId.get(), depth.get()
            );
        }
        return exceptionTraceService.getExceptionsInRange(
                applicationName, agentId, from, to
        );
    }

    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to,

            @RequestParam("traceId") Optional<String> traceId,
            @RequestParam("spanId") Optional<Long> spanId,
            @RequestParam("exceptionId") Optional<Long> exceptionId,
            @RequestParam("exceptionDepth") Optional<Integer> depth
    ) {

        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);
        List<ExceptionTraceSummary> exceptionTraceSummaries;
        if (optionalsAreGiven(traceId, spanId, exceptionId, depth)) {
            exceptionTraceSummaries = exceptionTraceService.getSummaryOfSimilarExceptions(
                    applicationName, agentId, from, to,
                    traceId.get(), spanId.get(), exceptionId.get(), depth.get()
            );
        } else {
            exceptionTraceSummaries = exceptionTraceService.getSummaryInRange(
                    applicationName, agentId, from, to
            );
        }
        return ExceptionTraceView.newViewFromSummaries("", timeWindow, exceptionTraceSummaries);
    }

    private static boolean optionalsAreGiven(Optional<?>... optionals) {
        for (Optional<?> o : optionals) {
            if (o.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
