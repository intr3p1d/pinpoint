package com.navercorp.pinpoint.profiler.context.exception;

/**
 * @author intr3p1d
 */
public interface ExceptionRecordingService {
    public SpanEventException recordException(ExceptionRecordingContext context, Throwable current, long startTime);
}
