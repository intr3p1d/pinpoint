package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.metric.collector.MetricAppPropertySources;
import com.navercorp.pinpoint.metric.collector.dao.ExceptionTraceDao;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
@ComponentScan("com.navercorp.pinpoint.metric.collector.dao.pinot")
@ImportResource({"classpath:pinot-collector/applicationContext-collector-pinot.xml", "classpath:pinot-collector/servlet-context-collector-pinot.xml"})
@Import({MetricAppPropertySources.class})
public class PinotExceptionTraceService implements ExceptionTraceService {
    private final ExceptionTraceDao exceptionTraceDao;

    public PinotExceptionTraceService(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public void save(List<SpanEventExceptionBo> spanEventExceptionBoList, ServiceType applicationServiceType, String applicationId, String agentId, TransactionId transactionId, long spanId) {
        // TODO
    }

    @Override
    public void save(SpanEventExceptionBo spanEventExceptionBo) {
        exceptionTraceDao.insert(spanEventExceptionBo);
    }
}
