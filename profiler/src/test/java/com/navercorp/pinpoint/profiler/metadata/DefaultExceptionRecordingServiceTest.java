package com.navercorp.pinpoint.profiler.metadata;

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
        } catch(Exception e) {
            defaultExceptionRecordingService.recordException(e);
            throw new RuntimeException("Level 2 Error", e);
        }
    }

    public void methodC() {
        try {
            methodB();
        } catch(Exception e) {
            defaultExceptionRecordingService.recordException(e);
            throw new RuntimeException("Level 3 Error", e);
        }
    }

    @Test
    public void testGet(){
        try {
            methodC();
        } catch (Exception e) {
            defaultExceptionRecordingService.recordException(e);
        }
    }
}
