package com.navercorp.pinpoint.metric.web.controller;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.common.pinot.TenantProvider;
import com.navercorp.pinpoint.metric.web.service.ExceptionTraceService;
import com.navercorp.pinpoint.metric.web.util.ExceptionTraceQueryParameter;
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
    public List<SpanEventException> getSpanEventExceptionFromTransactionId(
            @RequestParam("traceId") String traceId
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);
        return exceptionTraceService.getSpanEventExceptionFromTransaction(transactionId);
    }

    @GetMapping("/error-list")
    public List<SpanEventException> getListOfSpanEventException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        return getAllCollectedSpanEventExceptionList(
                applicationName, agentId, from, to
        );
    }

    @GetMapping("/error-list")
    public List<SpanEventException> getListOfSpanEventException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("traceId") String traceId,
            @RequestParam("traceTimestamp") long timestamp,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        return getCollectedSpanEventExceptionListByTransactionId(
                traceId, timestamp, applicationName, agentId, from, to
        );
    }

    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);

        List<SpanEventException> spanEventExceptions = getAllCollectedSpanEventExceptionList(
                applicationName, agentId, from, to
        );

        return new ExceptionTraceView("", timeWindow, spanEventExceptions);
    }

    @GetMapping("/chart")
    public ExceptionTraceView getCollectedSpanEventException(
            @RequestParam("applicationName") String applicationName,
            @RequestParam(value = "agentId", required = false) String agentId,
            @RequestParam("traceId") String traceId,
            @RequestParam("traceTimestamp") long timestamp,
            @RequestParam("from") long from,
            @RequestParam("to") long to
    ) {
        TimeWindow timeWindow = new TimeWindow(Range.newRange(from, to), DEFAULT_TIME_WINDOW_SAMPLER);

        List<SpanEventException> spanEventExceptions = getCollectedSpanEventExceptionListByTransactionId(
                traceId, timestamp, applicationName, agentId, from, to
        );

        return new ExceptionTraceView("", timeWindow, spanEventExceptions);
    }

    private List<SpanEventException> getCollectedSpanEventExceptionListByTransactionId(
            String traceId,
            long timestamp,
            String applicationName,
            String agentId,
            long from,
            long to
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);
        final SpanEventException spanEventException = exceptionTraceService.getSpanEventException(transactionId, timestamp);

        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder();
        builder.setApplicationName(applicationName);
        builder.setAgentId(agentId);
        builder.setSpanEventException(spanEventException);
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
        builder.setSpanEventException(null);
        builder.setRange(Range.newRange(from, to));

        return exceptionTraceService.getCollectedSpanEventException(builder.build());
    }
}
