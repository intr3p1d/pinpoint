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

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.profiler.context.Annotation;
import com.navercorp.pinpoint.profiler.context.annotation.Annotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingService implements ExceptionRecordingService {

    private static final Logger logger = LogManager.getLogger(DefaultExceptionRecordingService.class);

    private static final boolean IS_DEBUG = logger.isDebugEnabled();

    private final ExceptionIdGenerator exceptionIdGenerator;

    public DefaultExceptionRecordingService(ExceptionIdGenerator exceptionIdGenerator) {
        this.exceptionIdGenerator = exceptionIdGenerator;
    }

    @Override
    public SpanEventException recordException(ExceptionRecordingContext context, Throwable current, long startTime) {
        Objects.requireNonNull(context);

        ExceptionRecordingState state = ExceptionRecordingState.stateOf(context.getPrevious(), current);
        SpanEventException spanEventException = state.apply(context, current, startTime, exceptionIdGenerator);

        logException(spanEventException);

        return spanEventException;
    }

    private void logException(SpanEventException spanEventException) {
        if (IS_DEBUG && spanEventException != null) {
            logger.debug(spanEventException);
        }
    }

    @Override
    public Annotation<Long> recordExceptionLinkId(ExceptionRecordingContext context) {
        return Annotations.of(AnnotationKey.EXCEPTION_LINK_ID.getCode(), context.getExceptionId());
    }
}
