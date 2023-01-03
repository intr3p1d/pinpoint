package com.navercorp.pinpoint.metric.web.service;

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.metric.web.util.ExceptionTraceQueryParameter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
public class ExceptionTraceServiceImpl implements ExceptionTraceService {

    private ExceptionTraceDao exceptionTraceDao;

    public ExceptionTraceServiceImpl(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public List<SpanEventException> getCollectedSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        return null;
    }

    @Override
    public SpanEventException getSpanEventExceptionFromTransaction(TransactionId transactionId) {
        return null;
    }
}
