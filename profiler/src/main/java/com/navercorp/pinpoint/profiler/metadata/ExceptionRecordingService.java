package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.SpanException;

/**
 * @author intr3p1d
 */
public interface ExceptionRecordingService {
    SpanException recordException(Throwable throwable);
}
