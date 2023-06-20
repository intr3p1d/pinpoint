package com.navercorp.pinpoint.profiler.context.module;

import com.google.inject.PrivateModule;
import com.google.inject.Scopes;
import com.navercorp.pinpoint.profiler.context.exception.ExceptionRecordingService;
import com.navercorp.pinpoint.profiler.context.exception.ExceptionRecordingServiceProvider;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionWrapperFactory;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionWrapperFactoryProvider;
import com.navercorp.pinpoint.profiler.context.exception.sampler.ExceptionTraceSampler;
import com.navercorp.pinpoint.profiler.context.exception.sampler.ExceptionTraceSamplerProvider;
import com.navercorp.pinpoint.profiler.context.monitor.config.ExceptionTraceConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ExceptionTraceModule extends PrivateModule {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final ExceptionTraceConfig exceptionTraceConfig;

    public ExceptionTraceModule(ExceptionTraceConfig exceptionTraceConfig) {
        this.exceptionTraceConfig = Objects.requireNonNull(exceptionTraceConfig, "exceptionTraceConfig");
    }

    @Override
    protected void configure() {
        logger.info("configure {}", this.getClass().getSimpleName());
        bind(ExceptionTraceConfig.class).toInstance(exceptionTraceConfig);

        bind(ExceptionTraceSampler.class).toProvider(ExceptionTraceSamplerProvider.class).in(Scopes.SINGLETON);
        bind(ExceptionWrapperFactory.class).toProvider(ExceptionWrapperFactoryProvider.class).in(Scopes.SINGLETON);

        bind(ExceptionRecordingService.class).toProvider(ExceptionRecordingServiceProvider.class).in(Scopes.SINGLETON);
        expose(ExceptionRecordingService.class);
    }
}
