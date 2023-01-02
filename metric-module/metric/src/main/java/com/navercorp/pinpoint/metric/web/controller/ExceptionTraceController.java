package com.navercorp.pinpoint.metric.web.controller;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.common.pinot.TenantProvider;
import com.navercorp.pinpoint.metric.web.service.ExceptionTraceService;
import com.navercorp.pinpoint.metric.web.util.Range;
import com.navercorp.pinpoint.metric.web.util.TimeWindow;
import com.navercorp.pinpoint.metric.web.util.TimeWindowSampler;
import com.navercorp.pinpoint.metric.web.util.TimeWindowSlotCentricSampler;
import com.navercorp.pinpoint.metric.web.view.ExceptionTraceView;
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
@RequestMapping(value = "/exception-trace")
public class ExceptionTraceController {
    private final ExceptionTraceService exceptionTraceService;

    private final TimeWindowSampler DEFAULT_TIME_WINDOW_SAMPLER = new TimeWindowSlotCentricSampler(30000L, 200);
    private final TenantProvider tenantProvider;

    public ExceptionTraceController(ExceptionTraceService exceptionTraceService, TenantProvider tenantProvider) {
        this.exceptionTraceService = Objects.requireNonNull(exceptionTraceService, "exceptionTraceService");
        this.tenantProvider = Objects.requireNonNull(tenantProvider, "tenantProvider");
    }

    @GetMapping("/transaction-info")
    public SpanEventException getSpanEventExceptionFromTransactionId() {
        return exceptionTraceService.getSpanEventExceptionFromTransaction();
    }

    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam(value = "exceptionTrace", required = false) String exception,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);


        return null;
    }

    @GetMapping("/error-list")
    public ExceptionTraceView getListOfSpanEventException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam(value = "exceptionTrace", required = false) String exception,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);


        return null;
    }
}
