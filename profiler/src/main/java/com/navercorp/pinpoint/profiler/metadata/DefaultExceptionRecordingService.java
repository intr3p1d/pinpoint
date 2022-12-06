package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.exception.SpanEventException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private Throwable previous = null;
    private int depth = 0;

    public DefaultExceptionRecordingService() {
    }

    @Nullable
    @Override
    public SpanEventException recordException(Throwable throwable) {
        /*
        return toSpanException(throwable);8
         */
        SpanEventException flushedException = null;

        if (throwable != null) {
            depth += 1;
            logger.error(String.format("Stacking Exception... Current depth: %d", depth));
        }

        if (isTopLevelException(throwable)) {
            // Top level exception
            // Need to flush
            depth = 0;
            logger.error("Top level exception", previous);
            flushedException = toSpanException(previous);
        }

        holdCurrentException(throwable);
        return flushedException;
    }

    @Nullable
    @Override
    public SpanEventException flushHeldException() {
        return recordException(null);
    }

    private boolean isTopLevelException(Throwable throwable) {
        return throwable == null && previous != null;
    }

    private void holdCurrentException(Throwable throwable) {
        this.previous = throwable;
    }

    private static SpanEventException toSpanException(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        return new SpanEventException(throwable);
    }
}
