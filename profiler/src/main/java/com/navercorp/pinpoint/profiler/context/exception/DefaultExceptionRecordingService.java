/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.context.exception;

import com.navercorp.pinpoint.profiler.context.Annotation;
import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionContext;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionWrapperFactory;
import com.navercorp.pinpoint.profiler.context.exception.sampler.ExceptionTraceSampler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {

    private final Logger logger = LogManager.getLogger(getClass());

    private final boolean IS_DEBUG = logger.isDebugEnabled();

    private final ExceptionTraceSampler exceptionTraceSampler;
    private final ExceptionWrapperFactory exceptionWrapperFactory;

    public DefaultExceptionRecordingService(ExceptionTraceSampler exceptionTraceSampler,
                                            ExceptionWrapperFactory exceptionWrapperFactory
    ) {
        this.exceptionTraceSampler = exceptionTraceSampler;
        this.exceptionWrapperFactory = exceptionWrapperFactory;
    }

    public void recordException(ExceptionContext context, Throwable current, long startTime) {
        Objects.requireNonNull(context);

        ExceptionRecordingState state = ExceptionRecordingState.stateOf(context.getPrevious(), current);
        ExceptionTraceSampler.SamplingState samplingState = getSamplingState(state, context);
        state.checkAndApply(context, current, startTime, samplingState, exceptionWrapperFactory);
    }

    private ExceptionTraceSampler.SamplingState getSamplingState(
            ExceptionRecordingState state,
            ExceptionContext context
    ) {
        if (state.needsNewExceptionId()) {
            return exceptionTraceSampler.isSampled();
        } else if (state.chainContinued()) {
            return exceptionTraceSampler.continuingSampled(context.getSamplingState());
        } else if (state.notNeedExceptionId()) {
            return ExceptionTraceSampler.DISABLED;
        }
        return ExceptionTraceSampler.DISABLED;
    }

    public void recordExceptionIdAnnotation(SpanEvent spanEvent, ExceptionContext context) {
        Annotation<Long> linkId = context.newExceptionLinkId();
        if (linkId != null) {
            spanEvent.addAnnotation(linkId);
        }
    }

    @Override
    public void recordException(
            ExceptionContext exceptionContext,
            SpanEvent spanEvent,
            Throwable throwable
    ) {
        this.recordException(
                exceptionContext,
                throwable,
                spanEvent.getStartTime()
        );
        this.recordExceptionIdAnnotation(
                spanEvent,
                exceptionContext
        );
    }
}
