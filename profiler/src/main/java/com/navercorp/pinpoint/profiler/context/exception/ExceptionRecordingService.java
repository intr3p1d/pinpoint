package com.navercorp.pinpoint.profiler.context.exception;

/**
 * @author intr3p1d
 */
public interface ExceptionRecordingService {
    SpanEventException recordException(Throwable throwable);

    SpanEventException flushHeldException();

    void checkAndSetStartTime(long startTime);
}
