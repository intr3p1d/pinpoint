package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.exception.DefaultExceptionRecordingService;
import com.navercorp.pinpoint.profiler.context.exception.SpanEventException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingServiceTest {

    DefaultExceptionRecordingService defaultExceptionRecordingService = new DefaultExceptionRecordingService();

    public void methodA() {
        throw new RuntimeException("Level 1 Error");
    }

    public void methodB() {
        try {
            methodA();
        } catch (Exception e) {
            defaultExceptionRecordingService.recordException(e);
            throw new RuntimeException("Level 2 Error", e);
        }
    }

    public void methodC() {
        try {
            methodB();
        } catch (Exception e) {
            defaultExceptionRecordingService.recordException(e);
            throw new RuntimeException("Level 3 Error", e);
        }
    }

    @Test
    public void testFlushHeldException() {
        SpanEventException actual = null;
        SpanEventException expected = null;
        try {
            methodC();
        } catch (Exception e) {
            actual = defaultExceptionRecordingService.recordException(e);
            expected = DefaultExceptionRecordingService.toSpanException(e, 0);
            Assertions.assertNull(actual);
        }
        actual = defaultExceptionRecordingService.flushHeldException();
        Assertions.assertEquals(actual, expected);
    }

    @Test
    public void testRecordNullException() {
        SpanEventException actual = null;
        SpanEventException expected = null;
        try {
            methodC();
        } catch (Exception e) {
            actual = defaultExceptionRecordingService.recordException(e);
            expected = DefaultExceptionRecordingService.toSpanException(e, 0);
            Assertions.assertNull(actual);
        }
        actual = defaultExceptionRecordingService.recordException(null);
        Assertions.assertEquals(actual, expected);
    }
}
