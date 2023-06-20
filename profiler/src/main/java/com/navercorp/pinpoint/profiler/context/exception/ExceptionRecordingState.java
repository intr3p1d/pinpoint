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

import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionContext;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionWrapper;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionWrapperFactory;
import com.navercorp.pinpoint.profiler.context.exception.sampler.ExceptionTraceSampler;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public enum ExceptionRecordingState {
    CLEAN {
        @Override
        public List<ExceptionWrapper> getExceptions(
                ExceptionContext context,
                ExceptionWrapperFactory factory) {
            // do nothing
            return null;
        }

        @Override
        public void update(
                ExceptionContext context,
                Throwable current,
                long currentStartTime,
                ExceptionTraceSampler.SamplingState samplingState
        ) {
            // do nothing
        }
    },
    STARTED {
        @Override
        public List<ExceptionWrapper> getExceptions(
                ExceptionContext context,
                ExceptionWrapperFactory factory) {
            Objects.requireNonNull(context);
            return null;
        }

        @Override
        public void update(
                ExceptionContext context,
                Throwable current,
                long currentStartTime,
                ExceptionTraceSampler.SamplingState samplingState
        ) {
            Objects.requireNonNull(context);
            context.setPrevious(current);
            context.setStartTime(currentStartTime);
            context.setSamplingState(samplingState);
        }
    },
    STACKING {
        @Override
        public List<ExceptionWrapper> getExceptions(
                ExceptionContext context,
                ExceptionWrapperFactory factory) {
            Objects.requireNonNull(context);
            return null;
        }

        @Override
        public void update(
                ExceptionContext context,
                Throwable current,
                long currentStartTime,
                ExceptionTraceSampler.SamplingState samplingState
        ) {
            Objects.requireNonNull(context);
            context.setPrevious(current);
        }
    },
    FLUSH_AND_START {
        @Override
        public List<ExceptionWrapper> getExceptions(
                ExceptionContext context,
                ExceptionWrapperFactory factory) {
            Objects.requireNonNull(context);
            Objects.requireNonNull(factory);
            return newExceptionWrappers(
                    context, factory
            );
        }

        @Override
        public void update(
                ExceptionContext context,
                Throwable current,
                long currentStartTime,
                ExceptionTraceSampler.SamplingState samplingState
        ) {
            Objects.requireNonNull(context);
            context.setPrevious(current);
            context.setStartTime(currentStartTime);
            context.setSamplingState(samplingState);
        }
    },
    FLUSH {
        @Override
        public List<ExceptionWrapper> getExceptions(
                ExceptionContext context,
                ExceptionWrapperFactory factory) {
            Objects.requireNonNull(context);
            Objects.requireNonNull(factory);
            return newExceptionWrappers(
                    context, factory
            );
        }

        @Override
        public void update(
                ExceptionContext context,
                Throwable current,
                long currentStartTime,
                ExceptionTraceSampler.SamplingState samplingState
        ) {
            Objects.requireNonNull(context);
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

    public void checkAndApply(
            ExceptionContext context,
            Throwable current,
            long currentStartTime,
            ExceptionTraceSampler.SamplingState samplingState,
            ExceptionWrapperFactory factory
    ) {
        List<ExceptionWrapper> wrappers = null;
        if (samplingState.isSampling()) {
            wrappers = this.getExceptions(
                    context, factory
            );
            if (wrappers != null) {
                context.store(wrappers);
            }
        }
        this.update(
                context, current, currentStartTime, samplingState
        );
    }

    public abstract List<ExceptionWrapper> getExceptions(
            ExceptionContext context,
            ExceptionWrapperFactory factory
    );

    public abstract void update(
            ExceptionContext context,
            Throwable current,
            long currentStartTime,
            ExceptionTraceSampler.SamplingState samplingState
    );

    public boolean needsNewExceptionId() {
        return this == FLUSH_AND_START || this == STARTED;
    }

    public boolean chainContinued() {
        return this == STACKING || this == FLUSH;
    }

    public boolean notNeedExceptionId() {
        return this == STARTED;
    }

    private static List<ExceptionWrapper> newExceptionWrappers(
            ExceptionContext context,
            ExceptionWrapperFactory factory
    ) {
        return factory.newExceptionWrappers(
                context.getPrevious(), context.getStartTime(), context.getExceptionId()
        );
    }

}
