package com.navercorp.pinpoint.exceptiontrace.web.dao;


import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceDao {
    List<SpanEventException> getCollectedSpanEventExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter);
    List<SpanEventException> getExactSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter);
}
