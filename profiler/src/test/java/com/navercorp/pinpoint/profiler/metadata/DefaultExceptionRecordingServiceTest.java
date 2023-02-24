package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.exception.ExceptionRecordingContext;
import com.navercorp.pinpoint.profiler.context.exception.DefaultExceptionRecordingService;
import com.navercorp.pinpoint.profiler.context.exception.SpanEventException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingServiceTest {

    DefaultExceptionRecordingService exceptionRecordingService = new DefaultExceptionRecordingService();

    ExceptionRecordingContext context;

    long START_TIME = 1;

    public void methodA() {
        throw new RuntimeException("Level 1 Error");
    }

    public void methodB() {
        try {
            methodA();
        } catch (Exception e) {
            exceptionRecordingService.recordException(context, e, START_TIME);
            throw new RuntimeException("Level 2 Error", e);
        }
    }

    public void methodC() {
        try {
            methodB();
        } catch (Exception e) {
            exceptionRecordingService.recordException(context, e, 0);
            throw new RuntimeException("Level 3 Error", e);
        }
    }

    public void resetContext() {
        context = ExceptionRecordingContext.newContext();
    }

    @Test
    public void testRecordNullException() {
        resetContext();
        SpanEventException actual = null;
        SpanEventException expected = null;
        try {
            methodC();
        } catch (Exception e) {
            actual = exceptionRecordingService.recordException(context, e, 0);
            expected = SpanEventException.newSpanEventException(e, START_TIME);
            Assertions.assertNull(actual);
        }
        actual = exceptionRecordingService.recordException(context, null, 0);
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void testRecordNothing() {
        resetContext();
        SpanEventException actual = null;
        SpanEventException expected = null;
        actual = exceptionRecordingService.recordException(context, null, 0);
        Assertions.assertEquals(actual, expected);
    }

}
