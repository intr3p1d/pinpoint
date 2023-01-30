package com.navercorp.pinpoint.profiler.context.exception;

/**
 * @author intr3p1d
 */
public class BypassExceptionRecordingService implements ExceptionRecordingService {

    public BypassExceptionRecordingService() {
    }

    @Override
    public SpanEventException recordException(Throwable throwable) {
        return null;
    }

    @Override
    public SpanEventException flushHeldException() {
        return null;
    }

    @Override
    public void checkAndSetStartTime(long startTime) {
        // do nothing
    }
}
