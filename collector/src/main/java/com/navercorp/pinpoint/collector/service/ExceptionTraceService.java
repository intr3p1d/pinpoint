package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceService {
    void save(SpanEventExceptionBo spanEventExceptionBo);
}
