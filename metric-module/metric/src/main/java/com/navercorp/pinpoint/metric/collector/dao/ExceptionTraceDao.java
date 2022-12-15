package com.navercorp.pinpoint.metric.collector.dao;

import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceDao {
    void insert(SpanEventExceptionBo spanEventExceptionBo);
}
