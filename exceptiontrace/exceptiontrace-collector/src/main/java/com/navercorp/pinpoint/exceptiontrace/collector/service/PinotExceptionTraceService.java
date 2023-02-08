package com.navercorp.pinpoint.exceptiontrace.collector.service;

import com.navercorp.pinpoint.collector.service.ExceptionTraceService;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
import com.navercorp.pinpoint.common.server.bo.exception.ExceptionWrapperBo;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.exceptiontrace.collector.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
@Service
@Profile("metric")
public class PinotExceptionTraceService implements ExceptionTraceService {
    private final ExceptionTraceDao exceptionTraceDao;

    public PinotExceptionTraceService(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public void save(List<SpanEventExceptionBo> spanEventExceptionBoList, ServiceType applicationServiceType, String applicationId, String agentId, TransactionId transactionId, long spanId) {
        List<SpanEventException> spanEventExceptions = new ArrayList<>();
        for (SpanEventExceptionBo spanEventExceptionBo: spanEventExceptionBoList) {
            spanEventExceptions.addAll(toSpanEventExceptions(spanEventExceptionBo, applicationServiceType, applicationId, agentId, transactionId, spanId));
        }
        exceptionTraceDao.insert(spanEventExceptions);
    }

    private static List<SpanEventException> toSpanEventExceptions(
            SpanEventExceptionBo spanEventExceptionBo,
            ServiceType applicationServiceType, String applicationId, String agentId,
            TransactionId transactionId, long spanId) {
        return spanEventExceptionBo.getExceptionWrappers().stream().map(
                (ExceptionWrapperBo e) ->
                toSpanEventException(
                        e,
                        applicationServiceType, applicationId, agentId,
                        transactionId, spanId,
                        spanEventExceptionBo.getStartTime()
                )
        ).collect(Collectors.toList());
    }

    private static SpanEventException toSpanEventException(
            ExceptionWrapperBo exceptionWrapperBo,
            ServiceType applicationServiceType, String applicationId, String agentId,
            TransactionId transactionId, long spanId,
            long startTime) {
        return SpanEventException.valueOf(
                startTime,
                transactionIdToString(transactionId),
                spanId,
                applicationServiceType.getName(),
                applicationId,
                agentId,
                exceptionWrapperBo.getExceptionClassName(),
                exceptionWrapperBo.getExceptionMessage(),
                exceptionWrapperBo.getStackTraceElements()
        );
    }

    private static String transactionIdToString(TransactionId transactionId) {
        return TransactionIdUtils.formatString(transactionId);
    }

}
