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
import com.navercorp.pinpoint.exceptiontrace.web.model.GroupByAttributes;
import com.navercorp.pinpoint.exceptiontrace.web.service.ExceptionTraceService;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import com.navercorp.pinpoint.metric.web.util.Range;
import com.navercorp.pinpoint.metric.web.util.TimePrecision;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
@RestController
@RequestMapping(value = "/errors")
public class ExceptionTraceController {

    private static final TimePrecision DETAILED_TIME_PRECISION = TimePrecision.newTimePrecision(TimeUnit.MILLISECONDS, 1);

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
        ExceptionTraceQueryParameter queryParameter = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setTransactionId(traceId)
                .setSpanId(spanId)
                .setExceptionId(exceptionId)
                .setTimePrecision(DETAILED_TIME_PRECISION)
                .build();
        return exceptionTraceService.getTransactionExceptions(
                queryParameter
        );
    }

    @GetMapping("/errorList")
    public List<SpanEventException> getListOfSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        ExceptionTraceQueryParameter queryParameter = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setRange(Range.newRange(from, to))
                .setTimePrecision(DETAILED_TIME_PRECISION)
                .build();
        return exceptionTraceService.getExceptionsInRange(
                queryParameter
        );
    }

    @GetMapping("/errorList/groupBy")
    public List<ExceptionTraceSummary> getListOfSpanEventExceptionWithDynamicGroupBy(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to,

            @RequestParam("groupBy") List<String> groupByList
    ) {
        List<GroupByAttributes> groupByAttributes = groupByList.stream().map(
                GroupByAttributes::valueOf
        ).distinct().collect(Collectors.toList());

        logger.info(groupByAttributes);

        ExceptionTraceQueryParameter queryParameter = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setRange(Range.newRange(from, to))
                .setTimePrecision(DETAILED_TIME_PRECISION)
                .addAllGroupBies(groupByAttributes)
                .build();

        return exceptionTraceService.getSummaryWithGroups(
                queryParameter
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
        ExceptionTraceQueryParameter queryParameter = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setRange(Range.newRange(from, to))
                .setTimePrecision(TimePrecision.newTimePrecision(TimeUnit.MILLISECONDS, (int) timeWindow.getWindowSlotSize()))
                .build();
        List<ExceptionTraceSummary> exceptionTraceSummaries = exceptionTraceService.getSummaryInRange(
                queryParameter
        );
        return ExceptionTraceView.newViewFromSummaries("", timeWindow, exceptionTraceSummaries);
    }

    @GetMapping("/chart/groupBy")
    public ExceptionTraceView getCollectedSpanEventExceptionByGivenRange(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to,

            @RequestParam("groupBy") List<String> groupByList
    ) {
        List<GroupByAttributes> groupByAttributes = groupByList.stream().map(
                GroupByAttributes::valueOf
        ).distinct().collect(Collectors.toList());
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);
        ExceptionTraceQueryParameter queryParameter = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setRange(Range.newRange(from, to))
                .setTimePrecision(TimePrecision.newTimePrecision(TimeUnit.MILLISECONDS, (int) timeWindow.getWindowSlotSize()))
                .addAllGroupBies(groupByAttributes)
                .build();
        List<ExceptionTraceSummary> exceptionTraceSummaries = exceptionTraceService.getSummaryWithGroups(
                queryParameter
        );
        return ExceptionTraceView.newViewFromSummaries("", timeWindow, exceptionTraceSummaries);
    }

}
