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
    private long startTime = 0;
    private int elapsedTime = 0;

    private int depth = 0;

    public DefaultExceptionRecordingService() {
    }

    @Nullable
    @Override
    public SpanEventException recordException(Throwable throwable) {
        SpanEventException flushedException = null;

        if (throwable != null) {
            depth += 1;
            logger.error(String.format("Stacking Exception... Current depth: %d", depth));
        }

        flushedException = flushIfTopLevelException(throwable);
        holdCurrentException(throwable);
        return flushedException;
    }

    public void checkAndSetStartTime(long startTime) {
        if (previous == null) {
            this.startTime = startTime;
        }
    }

    public void checkAndAddElapsedTime(int elapsedTime) {
        if (previous != null) {
            this.elapsedTime += elapsedTime;
        }
    }

    private SpanEventException flushIfTopLevelException(Throwable throwable) {
        SpanEventException spanEventException = null;
        if (isTopLevelException(throwable)) {
            depth = 0;
            logger.error("Top level exception", previous);
            spanEventException = toSpanException(previous, this.startTime, this.elapsedTime);
            cleanTime();
        }
        return spanEventException;
    }

    private boolean isTopLevelException(Throwable throwable) {
        return throwable == null && previous != null;
    }

    private void cleanTime() {
        this.startTime = 0;
        this.elapsedTime = 0;
    }

    private void holdCurrentException(Throwable throwable) {
        this.previous = throwable;
    }

    private static SpanEventException toSpanException(Throwable throwable, long startTime, int elapsedTime) {
        if (throwable == null) {
            return null;
        }
        SpanEventException spanEventException = new SpanEventException(throwable);
        spanEventException.setStartTime(startTime);
        spanEventException.setElapsedTime(elapsedTime);
        return new SpanEventException(throwable);
    }

    @Nullable
    @Override
    public SpanEventException flushHeldException() {
        return recordException(null);
    }

}
