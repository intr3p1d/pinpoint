package com.navercorp.pinpoint.exceptiontrace.web.service;

import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.metric.web.util.ExceptionTraceQueryParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
public class ExceptionTraceServiceImpl implements ExceptionTraceService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private ExceptionTraceDao exceptionTraceDao;

    public ExceptionTraceServiceImpl(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public List<SpanEventException> getCollectedSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        return exceptionTraceDao.getCollectedSpanEventExceptions(exceptionTraceQueryParameter);
    }

    @Override
    public SpanEventException getExactSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getExactSpanEventException(exceptionTraceQueryParameter);
        if (spanEventExceptions.isEmpty()) {
            return null;
        }
        return spanEventExceptions.get(0);
    }

    @Override
    public List<SpanEventException> getSpanEventExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        return exceptionTraceDao.getCollectedSpanEventExceptions(exceptionTraceQueryParameter);
    }

}
