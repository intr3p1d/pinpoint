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

import java.util.Objects;

/**
 * @author intr3p1d
 */
public enum ExceptionRecordingState {
    CLEAN {
        @Override
        public SpanEventException getException(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            // do nothing
            return null;
        }

        @Override
        public void update(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            // do nothing
        }
    },
    STARTED {
        @Override
        public SpanEventException getException(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            return null;
        }

        @Override
        public void update(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            context.setPrevious(current);
            context.setStartTime(currentStartTime);
            context.setExceptionId(exceptionId);
        }
    },
    STACKING {
        @Override
        public SpanEventException getException(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            return null;
        }

        @Override
        public void update(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            context.setPrevious(current);
        }
    },
    FLUSH_AND_START {
        @Override
        public SpanEventException getException(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            return newSpanEventException(
                    context
            );
        }

        @Override
        public void update(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            context.setPrevious(current);
            context.setStartTime(currentStartTime);
            context.setExceptionId(exceptionId);
        }
    },
    FLUSH {
        @Override
        public SpanEventException getException(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            Objects.requireNonNull(context);
            return newSpanEventException(
                    context
            );
        }

        @Override
        public void update(
                ExceptionRecordingContext context,
                Throwable current,
                long currentStartTime,
                long exceptionId
        ) {
            context.resetPrevious();
            context.resetStartTime();
            context.resetExceptionId();
        }
    };

    public static ExceptionRecordingState stateOf(Throwable previous, Throwable current) {
        if (previous == null) {
            if (current == null) {
                return CLEAN;
            }
            return STARTED;
        } else {
            if (current == null) {
                return FLUSH;
            } else if (isExceptionChainContinuing(previous, current)) {
                return STACKING;
            }
            return FLUSH_AND_START;
        }
    }

    private static boolean isExceptionChainContinuing(Throwable previous, Throwable current) {
        Throwable throwable = current;
        while (throwable != null) {
            if (throwable == previous) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }

    public SpanEventException checkAndApply(
            ExceptionRecordingContext context,
            Throwable current,
            long currentStartTime,
            ExceptionTraceSampler.SamplingState samplingState
    ) {
        SpanEventException spanEventException = null;
        if (samplingState.isSampling()) {
            spanEventException = this.getException(
                    context, current, currentStartTime, samplingState.currentId()
            );
        }
        this.update(
                context, current, currentStartTime, samplingState.currentId()
        );
        return spanEventException;
    }

    public abstract SpanEventException getException(
            ExceptionRecordingContext context,
            Throwable current,
            long currentStartTime,
            long exceptionId
    );

    public abstract void update(
            ExceptionRecordingContext context,
            Throwable current,
            long currentStartTime,
            long exceptionId
    );

    public boolean needsNewExceptionId() {
        return this == FLUSH_AND_START || this == STARTED;
    }

    private static SpanEventException newSpanEventException(ExceptionRecordingContext context) {
        return SpanEventException.newSpanEventException(
                context.getPrevious(), context.getStartTime(), context.getExceptionId()
        );
    }

}
