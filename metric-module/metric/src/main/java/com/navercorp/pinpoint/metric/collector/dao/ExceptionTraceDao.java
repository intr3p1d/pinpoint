package com.navercorp.pinpoint.metric.collector.dao;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceDao {
    void insert(List<SpanEventException> spanEventException);
}
