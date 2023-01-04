package com.navercorp.pinpoint.metric.web.dao;


import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.util.ExceptionTraceQueryParameter;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceDao {
    List<SpanEventException> getCollectedSpanEventExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter);
    List<SpanEventException> getSpanEventExceptionsFromTransaction(TransactionId transactionId);
}
