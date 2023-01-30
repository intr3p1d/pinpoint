package com.navercorp.pinpoint.profiler.context.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {

    private static final Logger logger = LogManager.getLogger(DefaultExceptionRecordingService.class);

    private static final boolean isDebug = logger.isDebugEnabled();

    public DefaultExceptionRecordingService() {
    }

    @Override
    public SpanEventException recordException(ExceptionRecordingContext context, Throwable current, long startTime) {
        Objects.requireNonNull(context);

        ExceptionRecordingState state = ExceptionRecordingState.stateOf(context.getPrevious(), current);
        SpanEventException spanEventException = state.apply(context, current, startTime);

        logException(spanEventException);

        return spanEventException;
    }

    private void logException(SpanEventException spanEventException) {
        if (isDebug) {
            if (spanEventException != null) {
                logger.debug(spanEventException);
            }
        }
    }
}
