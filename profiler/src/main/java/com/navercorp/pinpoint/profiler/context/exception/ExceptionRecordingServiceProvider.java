package com.navercorp.pinpoint.profiler.context.exception;

import com.google.inject.Inject;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

import javax.inject.Provider;

/**
 * @author intr3p1d
 */
public class ExceptionRecordingServiceProvider implements Provider<ExceptionRecordingService> {

    private final ProfilerConfig profilerConfig;

    @Inject
    public ExceptionRecordingServiceProvider(ProfilerConfig profilerConfig) {
        this.profilerConfig = profilerConfig;
    }

    @Override
    public ExceptionRecordingService get() {
        return new DefaultExceptionRecordingService();
    }
}
