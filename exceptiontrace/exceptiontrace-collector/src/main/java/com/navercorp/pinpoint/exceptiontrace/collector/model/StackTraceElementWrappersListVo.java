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
package com.navercorp.pinpoint.exceptiontrace.collector.model;

import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author intr3p1d
 */
public class StackTraceElementWrappersListVo {
    private List<StackTraceElementWrapper> stackTrace;


    public StackTraceElementWrappersListVo(List<StackTraceElementWrapper> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Iterable<String> getStackTraceClassName() {
        return () -> new MemberVariableIterator<>(
                stackTrace.iterator(),
                StackTraceElementWrapper::getClassName
        );
    }

    public Iterable<String> getStackTraceFileName() {
        return () -> new MemberVariableIterator<>(
                stackTrace.iterator(),
                StackTraceElementWrapper::getFileName
        );
    }

    public Iterable<Integer> getStackTraceLineNumber() {
        return () -> new MemberVariableIterator<>(
                stackTrace.iterator(),
                StackTraceElementWrapper::getLineNumber
        );
    }

    public Iterable<String> getStackTraceMethodName() {
        return () -> new MemberVariableIterator<>(
                stackTrace.iterator(),
                StackTraceElementWrapper::getMethodName
        );
    }


    private static class MemberVariableIterator<T, M> implements Iterator<M> {

        private final Iterator<T> parentIterator;
        private final Function<T, M> parentTFunction;

        public MemberVariableIterator(
                Iterator<T> parentIterator,
                Function<T, M> parentTFunction
        ) {
            this.parentIterator = parentIterator;
            this.parentTFunction = parentTFunction;
        }

        @Override
        public boolean hasNext() {
            return parentIterator.hasNext();
        }

        @Override
        public M next() {
            return parentTFunction.apply(parentIterator.next());
        }
    }

}
