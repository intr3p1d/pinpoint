package com.navercorp.pinpoint.metric.web.controller;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.common.pinot.TenantProvider;
import com.navercorp.pinpoint.metric.web.service.ExceptionTraceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private final TenantProvider tenantProvider;

    public ExceptionTraceController(ExceptionTraceService exceptionTraceService, TenantProvider tenantProvider) {
        this.exceptionTraceService = Objects.requireNonNull(exceptionTraceService, "exceptionTraceService");
        this.tenantProvider = Objects.requireNonNull(tenantProvider, "tenantProvider");
    }

    @GetMapping()
    public List<SpanEventException> getSpanEventException() {
        return exceptionTraceService.getCollectedSpanEventExceptionApplication();
    }

    @GetMapping()
    public SpanEventException getSpanEventExceptionFromTransactionId() {
        return exceptionTraceService.getSpanEventExceptionFromTransaction();
    }
}
