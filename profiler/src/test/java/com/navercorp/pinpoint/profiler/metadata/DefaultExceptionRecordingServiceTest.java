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
package com.navercorp.pinpoint.profiler.metadata;

import com.navercorp.pinpoint.profiler.context.DefaultReference;
import com.navercorp.pinpoint.profiler.context.Reference;
import com.navercorp.pinpoint.profiler.context.exception.AtomicExceptionIdGenerator;
import com.navercorp.pinpoint.profiler.context.exception.ExceptionIdGenerator;
import com.navercorp.pinpoint.profiler.context.exception.ExceptionRecordingContext;
import com.navercorp.pinpoint.profiler.context.exception.DefaultExceptionRecordingService;
import com.navercorp.pinpoint.profiler.context.exception.SpanEventException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

/**
 * @author intr3p1d
 */
public class DefaultExceptionRecordingServiceTest {

    private final static Logger logger = LogManager.getLogger(DefaultExceptionRecordingServiceTest.class);

    ExceptionIdGenerator exceptionIdGenerator = new AtomicExceptionIdGenerator();
    DefaultExceptionRecordingService exceptionRecordingService = new DefaultExceptionRecordingService(exceptionIdGenerator);

    ExceptionRecordingContext context;
    List<SpanEventException> flushedExceptions;

    long START_TIME = 1;

    String URI_TEMPLATE = "";

    private SpanEventException newSpanEventException(Throwable throwable, long exceptionId){
        return SpanEventException.newSpanEventException(throwable, START_TIME, exceptionId, URI_TEMPLATE);
    }

    public void resetContext() {
        context = ExceptionRecordingContext.newContext();
    }

    private Function<Throwable, Throwable> throwableInterceptor(Reference<Throwable> throwableReference) {
        return (Throwable throwable) -> {
            throwableReference.set(throwable);
            recordAndStore(throwable);
            logger.info(throwable);
            return throwable;
        };
    }

    private void recordAndStore(Throwable throwable) {
        SpanEventException spanEventException = exceptionRecordingService.recordException(context, throwable, START_TIME);
        if (spanEventException != null) {
            flushedExceptions.add(spanEventException);
        }
    }

    public void methodA(Function<Throwable, Throwable> interceptor) throws Throwable {
        throw interceptor.apply(new RuntimeException("Level 1 Error"));
    }

    public void methodB(Function<Throwable, Throwable> interceptor) throws Throwable {
        try {
            methodA(interceptor);
        } catch (Throwable e) {
            throw interceptor.apply(new RuntimeException("Level 2 Error", e));
        }
    }

    public void methodC(Function<Throwable, Throwable> interceptor) throws Throwable {
        try {
            methodB(interceptor);
        } catch (Throwable e) {
            throw interceptor.apply(new RuntimeException("Level 3 Error", e));
        }
    }

    @Test
    public void testRecordException() {
        resetContext();
        SpanEventException actual = null;
        SpanEventException expected = null;

        Reference<Throwable> reference = new DefaultReference<>();
        Function<Throwable, Throwable> throwableInterceptor = throwableInterceptor(reference);

        try {
            methodC(throwableInterceptor);
        } catch (Throwable e) {
            expected = newSpanEventException(e, context.getExceptionId());
            actual = exceptionRecordingService.recordException(context, e, START_TIME);
            expected = newSpanEventException(e, context.getExceptionId());
            Assertions.assertNull(actual);
        }
        actual = exceptionRecordingService.recordException(context, null, 0);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testRecordNothing() {
        resetContext();
        SpanEventException actual = null;
        SpanEventException expected = null;
        actual = exceptionRecordingService.recordException(context, null, 0);
        Assertions.assertEquals(expected, actual);
    }


    public void notChainedException(Function<Throwable, Throwable> interceptor) throws Throwable {
        try {
            methodC(interceptor);
        } catch (Throwable e) {
            throw interceptor.apply(new RuntimeException("Not Chained, Another New Exception"));
        }
    }

    @Test
    public void testRecordNotChainedException() {
        resetContext();
        SpanEventException actual = null;
        SpanEventException expected1 = null;
        SpanEventException expected2 = null;

        Reference<Throwable> reference = new DefaultReference<>();
        Function<Throwable, Throwable> throwableInterceptor = throwableInterceptor(reference);
        Throwable throwable = null;

        try {
            notChainedException(throwableInterceptor);
        } catch (Throwable e) {
            expected1 = newSpanEventException(reference.get(), context.getExceptionId());
            actual = exceptionRecordingService.recordException(context, e, START_TIME);
            throwable = e;
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(expected1, actual);
        }

        expected2 = newSpanEventException(throwable, context.getExceptionId());
        actual = exceptionRecordingService.recordException(context, null, 0);
        Assertions.assertEquals(expected2, actual);
    }

    public void rethrowGivenException(Function<Throwable, Throwable> interceptor) throws Throwable {
        try {
            methodC(interceptor);
        } catch (Exception e) {
            throw interceptor.apply(e);
        }
    }

    @Test
    public void testRecordRethrowGivenException() {
        resetContext();
        SpanEventException actual = null;
        SpanEventException expected = null;

        Reference<Throwable> reference = new DefaultReference<>();
        Function<Throwable, Throwable> throwableInterceptor = throwableInterceptor(reference);

        try {
            rethrowGivenException(throwableInterceptor);
        } catch (Throwable e) {
            expected = newSpanEventException(e, context.getExceptionId());
            actual = exceptionRecordingService.recordException(context, e, START_TIME);
            Assertions.assertNull(actual);
        }

        actual = exceptionRecordingService.recordException(context, null, 0);
        Assertions.assertEquals(expected, actual);
    }
}
