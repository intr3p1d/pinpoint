package com.navercorp.pinpoint.profiler.context.exception;

import javax.annotation.Nullable;

import static java.lang.Long.max;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {

    private Throwable previous = null;
    private long startTime = 0;

    public DefaultExceptionRecordingService() {
    }

    @Nullable
    @Override
    public SpanEventException recordException(Throwable throwable) {
        SpanEventException flushedException = flushIfTopLevelException(throwable);
        holdGivenException(throwable);
        return flushedException;
    }

    private SpanEventException flushIfTopLevelException(Throwable throwable) {
        SpanEventException spanEventException = null;
        if (isTopLevelException(throwable)) {
            spanEventException = toSpanException(previous, this.startTime);
            resetStartTime();
        }
        return spanEventException;
    }

    private boolean isTopLevelException(Throwable throwable) {
        return throwable == null && previous != null;
    }

    @Nullable
    public static SpanEventException toSpanException(Throwable throwable, long startTime) {
        if (throwable == null) {
            return null;
        }
        return new SpanEventException(throwable, startTime);
    }

    private void resetStartTime() {
        this.startTime = 0;
    }

    private void holdGivenException(Throwable throwable) {
        this.previous = throwable;
    }

    @Nullable
    @Override
    public SpanEventException flushHeldException() {
        return recordException(null);
    }

    @Override
    public void checkAndSetStartTime(long startTime) {
        if (previous == null) {
            this.startTime = max(startTime, this.startTime);
        }
    }
}
