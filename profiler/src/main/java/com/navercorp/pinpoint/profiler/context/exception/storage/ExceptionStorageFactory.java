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
package com.navercorp.pinpoint.profiler.context.exception.storage;

import com.navercorp.pinpoint.common.profiler.message.DataSender;
import com.navercorp.pinpoint.profiler.context.SpanType;
import com.navercorp.pinpoint.profiler.context.exception.model.SpanEventExceptionFactory;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class ExceptionStorageFactory {

    private final DataSender<SpanType> dataSender;
    private final int bufferSize;

    public ExceptionStorageFactory(DataSender<SpanType> dataSender, int bufferSize) {
        this.dataSender = Objects.requireNonNull(dataSender, "dataSender");
        this.bufferSize = bufferSize;
    }

    public ExceptionStorage createStorage(SpanEventExceptionFactory factory) {
        return new ExceptionStorage(bufferSize, dataSender, factory);
    }
}
