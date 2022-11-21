package com.navercorp.pinpoint.profiler.metadata;

/**
 * @author intr3p1d
 */
public interface ExceptionRecordingService {
    String recordException(Throwable throwable);
}
