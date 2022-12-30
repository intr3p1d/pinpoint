package com.navercorp.pinpoint.metric.web.dao.pinot;

import com.navercorp.pinpoint.metric.web.dao.ExceptionTraceDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final String NAMESPACE = PinotExceptionTraceDao.class.getName() + ".";

    private final SqlSessionTemplate sqlPinotSessionTemplate;

    public PinotExceptionTraceDao(SqlSessionTemplate sqlPinotSessionTemplate) {
        this.sqlPinotSessionTemplate = Objects.requireNonNull(sqlPinotSessionTemplate, "sqlPinotSessionTemplate");
    }
}
