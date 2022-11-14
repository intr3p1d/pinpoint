package com.navercorp.pinpoint.profiler.metadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public DefaultExceptionRecordingService() {
    }

    @Override
    public void recordException(Throwable throwable) {
        logger.warn(throwable.getCause());
        logger.warn(throwable.getMessage());
        logger.warn(throwable.toString());
        throwable.printStackTrace();
    }
}
