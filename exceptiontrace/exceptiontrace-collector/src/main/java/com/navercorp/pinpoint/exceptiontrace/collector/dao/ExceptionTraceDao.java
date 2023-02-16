package com.navercorp.pinpoint.exceptiontrace.collector.dao;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceDao {
    void insert(List<SpanEventException> spanEventException);
}
