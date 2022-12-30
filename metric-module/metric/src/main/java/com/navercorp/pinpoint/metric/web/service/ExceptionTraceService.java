package com.navercorp.pinpoint.metric.web.service;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceService {
    List<SpanEventException> getCollectedSpanEventExceptionApplication();

    SpanEventException getSpanEventExceptionFromTransaction();
}
