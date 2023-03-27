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

/**
 * @author intr3p1d
 */
public enum ExceptionRecordingState {
    CLEAN {
        @Override
        public SpanEventException apply(
                ExceptionRecordingContext exceptionRecordingContext,
                Throwable current,
                long currentStartTime,
                ExceptionIdGenerator idGenerator) {
            // do nothing
            return null;
        }
    },
    STARTED {
        @Override
        public SpanEventException apply(
                ExceptionRecordingContext exceptionRecordingContext,
                Throwable current,
                long currentStartTime,
                ExceptionIdGenerator idGenerator) {
            exceptionRecordingContext.setPrevious(current);
            exceptionRecordingContext.setStartTime(currentStartTime);
            exceptionRecordingContext.setExceptionId(idGenerator.nextExceptionId());
            return null;
        }
    },
    STACKING {
        @Override
        public SpanEventException apply(
                ExceptionRecordingContext exceptionRecordingContext,
                Throwable current,
                long currentStartTime,
                ExceptionIdGenerator idGenerator) {
            if (!isContinued(exceptionRecordingContext.getPrevious(), current)) {
                SpanEventException spanEventException = newSpanEventException(
                        exceptionRecordingContext
                );
                exceptionRecordingContext.setPrevious(current);
                exceptionRecordingContext.setStartTime(currentStartTime);
                exceptionRecordingContext.setExceptionId(idGenerator.nextExceptionId());
                return spanEventException;
            }
            else {
                exceptionRecordingContext.setPrevious(current);
                return null;
            }
        }
    },
    FLUSH {
        @Override
        public SpanEventException apply(
                ExceptionRecordingContext exceptionRecordingContext,
                Throwable current,
                long currentStartTime,
                ExceptionIdGenerator idGenerator) {
            SpanEventException spanEventException = newSpanEventException(
                    exceptionRecordingContext
            );
            exceptionRecordingContext.resetPrevious();
            exceptionRecordingContext.resetStartTime();
            exceptionRecordingContext.resetExceptionId();
            return spanEventException;
        }
    };

    public static ExceptionRecordingState stateOf(Object previous, Object current) {
        if (previous == null) {
            if (current == null) {
                return CLEAN;
            }
            return STARTED;
        } else {
            if (current == null) {
                return FLUSH;
            }
            return STACKING;
        }
    }

    public static SpanEventException newSpanEventException(ExceptionRecordingContext context) {
        return SpanEventException.newSpanEventException(
                context.getPrevious(), context.getStartTime(), context.getExceptionId()
        );
    }

    public static boolean isContinued(Throwable previous, Throwable current) {
        Throwable throwable = current;
        while (throwable != null) {
            if (throwable == previous) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }

    public abstract SpanEventException apply(
            ExceptionRecordingContext exceptionRecordingContext,
            Throwable current,
            long currentStartTime,
            ExceptionIdGenerator idGenerator
    );
}