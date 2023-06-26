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
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.profiler.context.exception.model.ExceptionWrapper;
import com.navercorp.pinpoint.profiler.context.exception.model.SpanEventException;
import com.navercorp.pinpoint.profiler.context.exception.model.SpanEventExceptionFactory;
import com.navercorp.pinpoint.profiler.metadata.MetaDataType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class BufferedExceptionStorage implements ExceptionStorage {

    private static final Logger logger = LogManager.getLogger(BufferedExceptionStorage.class);
    private static final boolean isDebug = logger.isDebugEnabled();

    private final int bufferSize;
    private List<ExceptionWrapper> storage;
    private final DataSender<MetaDataType> dataSender;
    private final SpanEventExceptionFactory factory;

    public BufferedExceptionStorage(int bufferSize, DataSender<MetaDataType> dataSender, SpanEventExceptionFactory factory) {
        this.bufferSize = bufferSize;
        this.dataSender = Objects.requireNonNull(dataSender, "dataSender");
        this.storage = allocateBuffer();
        this.factory = Objects.requireNonNull(factory, "spanEventExceptionFactory");
    }

    @Override
    public void store(List<ExceptionWrapper> wrappers) {
        final List<ExceptionWrapper> storage = getBuffer();
        storage.addAll(wrappers);

        if (overflow(storage)) {
            final List<ExceptionWrapper> flushData = clearBuffer();
            sendSpanEventException(flushData);
        }
    }

    @Override
    public void flush() {
        final List<ExceptionWrapper> copy = clearBuffer();
        if (CollectionUtils.hasLength(copy)) {
            sendSpanEventException(copy);
        }
    }

    @Override
    public void close() {
    }

    private boolean overflow(List<ExceptionWrapper> storage) {
        return storage.size() >= bufferSize;
    }

    private List<ExceptionWrapper> allocateBuffer() {
        return new ArrayList<>(this.bufferSize);
    }

    private List<ExceptionWrapper> getBuffer() {
        List<ExceptionWrapper> copy = this.storage;
        if (copy == null) {
            copy = allocateBuffer();
            this.storage = copy;
        }
        return copy;
    }

    private List<ExceptionWrapper> clearBuffer() {
        final List<ExceptionWrapper> copy = this.storage;
        this.storage = null;
        return copy;
    }

    private void sendSpanEventException(List<ExceptionWrapper> exceptionWrappers) {
        final SpanEventException spanEventException = this.factory.newSpanEventException(exceptionWrappers);

        if (isDebug) {
            logger.debug("Flush {}", spanEventException);
        }
        final boolean success = this.dataSender.send(spanEventException);
        if (!success) {
            // Do not call spanEventException.toString()
            logger.debug("send fail");
        }
    }

    @Override
    public String toString() {
        return "ExceptionTraceStorage{" +
                "bufferSize=" + bufferSize +
                ", dataSender=" + dataSender +
                '}';
    }
}
