package com.navercorp.pinpoint.metric.web.service;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.util.ExceptionTraceQueryParameter;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceService {
    List<SpanEventException> getCollectedSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter);

    SpanEventException getSpanEventException(TransactionId transactionId, long timestamp);

    List<SpanEventException> getSpanEventExceptionFromTransaction(TransactionId transactionId);
}
