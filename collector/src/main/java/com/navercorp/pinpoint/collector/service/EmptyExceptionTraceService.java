package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * @author intr3p1d
 */
@ConditionalOnMissingBean(value = ExceptionTraceService.class, ignored = EmptyExceptionTraceService.class)
@Service
public class EmptyExceptionTraceService implements ExceptionTraceService {
    @Override
    public void save(SpanEventExceptionBo spanEventExceptionBo) {
    }
}
