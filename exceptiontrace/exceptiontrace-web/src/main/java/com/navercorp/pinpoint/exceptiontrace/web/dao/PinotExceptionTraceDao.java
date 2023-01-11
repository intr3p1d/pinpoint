package com.navercorp.pinpoint.exceptiontrace.web.dao;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final String NAMESPACE = PinotExceptionTraceDao.class.getName() + ".";

    private final SqlSessionTemplate sqlPinotSessionTemplate;

    public PinotExceptionTraceDao(@Qualifier("exceptionTracePinotSessionTemplate") SqlSessionTemplate sqlPinotSessionTemplate) {
        this.sqlPinotSessionTemplate = Objects.requireNonNull(sqlPinotSessionTemplate, "sqlPinotSessionTemplate");
    }

    @Override
    public List<SpanEventException> getCollectedSpanEventExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        return this.sqlPinotSessionTemplate.selectList(NAMESPACE + "selectSpanExceptionList", exceptionTraceQueryParameter);
    }

    @Override
    public List<SpanEventException> getExactSpanEventException(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        return this.sqlPinotSessionTemplate.selectList(NAMESPACE + "selectSpanExceptionList", exceptionTraceQueryParameter);
    }
}
