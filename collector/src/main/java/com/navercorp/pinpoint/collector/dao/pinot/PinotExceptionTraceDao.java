package com.navercorp.pinpoint.collector.dao.pinot;

import com.navercorp.pinpoint.collector.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public PinotExceptionTraceDao() {

    }

    @Override
    public void insert(SpanEventExceptionBo spanEventExceptionBo) {
        logger.debug(spanEventExceptionBo);
    }
}
