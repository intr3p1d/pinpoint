package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.exception.SpanEventException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private Throwable previous = null;
    private int depth = 0;

    public DefaultExceptionRecordingService() {
    }

    @Override
    public SpanEventException recordException(Throwable throwable) {
        SpanEventException flushedException = null;

        if (throwable != null) {
            depth += 1;
            logger.error(String.format("Stacking Exception... Current depth: %d", depth), throwable);
        }

        if (isTopLevelException(throwable)) {
            // Top level exception
            // Need to flush
            depth = 0;
            logger.error("Top level exception", previous);
            previous.printStackTrace();
            flushedException = toSpanException(previous);
        }

        holdCurrentException(throwable);
        return flushedException;
    }

    private boolean isTopLevelException(Throwable throwable) {
        return throwable == null && previous != null;
    }

    private void holdCurrentException(Throwable throwable) {
        this.previous = throwable;
    }

    private static SpanEventException toSpanException(Throwable throwable) {
        return new SpanEventException(throwable);
    }
}
