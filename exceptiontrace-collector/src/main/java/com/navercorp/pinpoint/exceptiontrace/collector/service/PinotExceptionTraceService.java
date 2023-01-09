package com.navercorp.pinpoint.exceptiontrace.collector.service;

import com.navercorp.pinpoint.collector.service.ExceptionTraceService;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.exceptiontrace.collector.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
@Profile("metric")
public class PinotExceptionTraceService implements ExceptionTraceService {
    private final ExceptionTraceDao exceptionTraceDao;

    public PinotExceptionTraceService(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public void save(List<SpanEventExceptionBo> spanEventExceptionBoList, ServiceType applicationServiceType, String applicationId, String agentId, TransactionId transactionId, long spanId) {
        List<SpanEventException> spanEventExceptions = spanEventExceptionBoList.stream().map(
                (SpanEventExceptionBo x) -> toSpanEventException(x, applicationServiceType, applicationId, agentId, transactionId, spanId)
        ).collect(Collectors.toList());

        exceptionTraceDao.insert(spanEventExceptions);
    }

    private static SpanEventException toSpanEventException(
            SpanEventExceptionBo spanEventExceptionBo,
            ServiceType applicationServiceType, String applicationId, String agentId,
            TransactionId transactionId, long spanId) {
        return new SpanEventException(
                spanEventExceptionBo.getStartTime(),
                transactionIdToString(transactionId),
                spanId,
                applicationServiceType.getName(),
                applicationId,
                agentId,
                spanEventExceptionBo.getExceptionClassName(),
                spanEventExceptionBo.getExceptionMessage(),
                spanEventExceptionBo.getStackTraceElements()
        );
    }

    private static String transactionIdToString(TransactionId transactionId) {
        return TransactionIdUtils.formatString(transactionId);
    }

}
