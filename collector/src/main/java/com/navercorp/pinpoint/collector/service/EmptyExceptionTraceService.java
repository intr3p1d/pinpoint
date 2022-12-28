package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author intr3p1d
 */
@ConditionalOnMissingBean(value = ExceptionTraceService.class, ignored = EmptyExceptionTraceService.class)
@Service
public class EmptyExceptionTraceService implements ExceptionTraceService {
    @Override
    public void save(List<SpanEventExceptionBo> spanEventExceptionBoList, ServiceType applicationServiceType, String applicationId, String agentId, TransactionId transactionId, long spanId) {
        // do nothing
    }
}
