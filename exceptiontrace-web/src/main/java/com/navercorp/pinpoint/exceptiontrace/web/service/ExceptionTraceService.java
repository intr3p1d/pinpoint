package com.navercorp.pinpoint.exceptiontrace.web.service;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.util.ExceptionTraceQueryParameter;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceService {
    List<SpanEventException> getCollectedSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter);

    SpanEventException getExactSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter);

    List<SpanEventException> getSpanEventExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter);
}
