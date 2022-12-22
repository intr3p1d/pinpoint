package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.exception.SpanEventException;

/**
 * @author intr3p1d
 */
public interface ExceptionRecordingService {
    SpanEventException recordException(Throwable throwable);

    SpanEventException flushHeldException();

    void checkAndSetStartTime(long startTime);

    void checkAndAddElapsedTime(long startTime);
}
