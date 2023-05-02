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

import com.google.common.util.concurrent.RateLimiter;

import java.util.Objects;


/**
 * @author intr3p1d
 */
public class ExceptionTraceSampler {

    final RateLimiter rateLimiter;
    
    final ExceptionIdGenerator idGenerator;
    
    private final static SamplingState DISABLED = new SamplingState() {
        @Override
        public boolean isSampling() {
            return false;
        }

        @Override
        public long currentId() {
            return Long.MIN_VALUE;
        }
    };

    public ExceptionTraceSampler(
            final int newMaxNewThroughput,
            ExceptionIdGenerator exceptionIdGenerator
    ) {
        this.rateLimiter = RateLimiter.create(newMaxNewThroughput);
        this.idGenerator = Objects.requireNonNull(exceptionIdGenerator, "exceptionIdGenerator");
    }

    public SamplingState isSampled() {
        if (rateLimiter.tryAcquire()) {
            long currentId = idGenerator.nextExceptionId();
            return new SamplingState() {
                @Override
                public boolean isSampling() {
                    return true;
                }

                @Override
                public long currentId() {
                    return currentId;
                }
            };
        }
        return DISABLED;
    }

    public interface SamplingState {
        boolean isSampling();

        long currentId();
    }
}
