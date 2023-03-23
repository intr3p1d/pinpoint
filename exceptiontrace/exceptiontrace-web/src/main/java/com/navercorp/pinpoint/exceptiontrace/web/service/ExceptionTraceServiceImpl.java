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

package com.navercorp.pinpoint.exceptiontrace.web.service;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import com.navercorp.pinpoint.metric.web.util.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author intr3p1d
 */
@Service
public class ExceptionTraceServiceImpl implements ExceptionTraceService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private ExceptionTraceDao exceptionTraceDao;

    public ExceptionTraceServiceImpl(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public List<SpanEventException> getTransactionExceptions(
            String applicationName,
            String agentId,
            String traceId,
            long spanId,
            long exceptionId
    ) {
        List<SpanEventException> spanEventExceptions = getTransactionExceptions(
                applicationName,
                agentId,
                traceId,
                spanId,
                exceptionId,
                this::getSpanEventExceptions
        );
        spanEventExceptions.sort(
                Comparator.comparing(SpanEventException::getExceptionDepth)
        );
        return spanEventExceptions;
    }

    @Override
    public List<SpanEventException> getExceptionsInRange(
            String applicationName,
            @Nullable String agentId,
            long from,
            long to
    ) {
        return getExceptionsInRange(
                applicationName,
                agentId,
                from,
                to,
                this::getSimpleSpanEventExceptions
        );
    }

    @Override
    public List<SpanEventException> getSimilarExceptions(
            String applicationName, String agentId, long from, long to,
            String traceId, long spanId, long exceptionId, int exceptionDepth
    ) {
        return getSimilarExceptions(
                applicationName, agentId, from, to,
                traceId, spanId, exceptionId, exceptionDepth,
                this::getSimpleSpanEventExceptions
        );
    }

    @Override
    public List<ExceptionTraceSummary> getSummaryInRange(
            String applicationName,
            @Nullable String agentId,
            long from,
            long to
    ) {
        return getExceptionsInRange(
                applicationName,
                agentId,
                from,
                to,
                this::getExceptionTraceSummaries
        );
    }

    @Override
    public List<ExceptionTraceSummary> getSummaryOfSimilarExceptions(
            String applicationName, String agentId, long from, long to,
            String traceId, long spanId, long exceptionId, int exceptionDepth
    ) {
        return getSimilarExceptions(
                applicationName, agentId, from, to,
                traceId, spanId, exceptionId, exceptionDepth,
                this::getExceptionTraceSummaries
        );
    }

    private <T> List<T> getTransactionExceptions(
            String applicationName,
            String agentId,
            String traceId,
            long spanId,
            long exceptionId,
            Function<ExceptionTraceQueryParameter, List<T>> queryFunction
    ) {
        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setTransactionId(traceId)
                .setSpanId(spanId)
                .setExceptionId(exceptionId);
        return queryFunction.apply(transactionBuilder.build());
    }

    private <T> List<T> getExceptionsInRange(
            String applicationName,
            @Nullable String agentId,
            long from,
            long to,
            Function<ExceptionTraceQueryParameter, List<T>> queryFunction
    ) {
        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setRange(Range.newRange(from, to));

        return queryFunction.apply(builder.build());
    }

    private <T> List<T> getSimilarExceptions(
            String applicationName, String agentId, long from, long to,
            String traceId, long spanId, long exceptionId, int exceptionDepth,
            Function<ExceptionTraceQueryParameter, List<T>> queryFunction
    ) {
        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setTransactionId(traceId)
                .setSpanId(spanId)
                .setExceptionId(exceptionId)
                .setExceptionDepth(exceptionDepth);

        final SpanEventException spanEventException = getTheExactException(
                transactionBuilder.build()
        );

        if (spanEventException == null) {
            return Collections.emptyList();
        }
        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setRange(Range.newRange(from, to))
                .setSpanEventException(spanEventException);

        return queryFunction.apply(
                builder.build()
        );
    }

    private List<SpanEventException> getSpanEventExceptions(ExceptionTraceQueryParameter queryParameter) {
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getExceptions(queryParameter);
        logger.info(spanEventExceptions.size());
        return spanEventExceptions;
    }

    private List<SpanEventException> getSimpleSpanEventExceptions(ExceptionTraceQueryParameter queryParameter) {
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getSimpleExceptions(queryParameter);
        logger.info(spanEventExceptions.size());
        return spanEventExceptions;
    }

    private SpanEventException getTheExactException(ExceptionTraceQueryParameter queryParameter) {
        return exceptionTraceDao.getException(queryParameter);
    }

    private List<ExceptionTraceSummary> getExceptionTraceSummaries(ExceptionTraceQueryParameter queryParameter) {
        List<ExceptionTraceSummary> spanEventExceptions = exceptionTraceDao.getSummaries(queryParameter);
        logger.info(spanEventExceptions.size());
        return spanEventExceptions;
    }

}
