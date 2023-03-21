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

import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
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
            long timestamp
    ) {
        return getTransactionExceptions(
                applicationName,
                agentId,
                traceId,
                timestamp,
                this::getSpanEventExceptions
        );
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
                this::getSpanEventExceptions
        );
    }

    @Override
    public List<SpanEventException> getSimilarExceptions(String agentId, String traceId, long traceTimestamp, int exceptionDepth, String applicationName, long from, long to) {
        return getSimilarExceptions(
                agentId,
                traceId,
                traceTimestamp,
                exceptionDepth,
                applicationName,
                from,
                to,
                this::getSpanEventExceptions
        );
    }

    @Override
    public List<ExceptionTraceSummary> getSummaryInRange(String applicationName, @Nullable String agentId, long from, long to) {
        return getExceptionsInRange(
                applicationName,
                agentId,
                from,
                to,
                this::getExceptionTraceSummarys
        );
    }

    @Override
    public List<ExceptionTraceSummary> getSummaryOfSimilarExceptions(String agentId, String traceId, long traceTimestamp, int exceptionDepth, String applicationName, long from, long to) {
        return getSimilarExceptions(
                agentId,
                traceId,
                traceTimestamp,
                exceptionDepth,
                applicationName,
                from,
                to,
                this::getExceptionTraceSummarys
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
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);

        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .setTransactionId(transactionId)
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
                .setRange(Range.newRange(from, to))
                .setAgentId(agentId);

        return queryFunction.apply(builder.build());
    }

    private <T> List<T> getSimilarExceptions(
            String agentId,
            String traceId,
            long traceTimestamp,
            int exceptionDepth,
            String applicationName,
            long from,
            long to,
            Function<ExceptionTraceQueryParameter, List<T>> queryFunction
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);
        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setAgentId(agentId)
                .forFindingSpecificException(transactionId, traceTimestamp, exceptionDepth)
                .setRange(Range.newRange(from, to));

        final SpanEventException spanEventException = getSpanEventException(
                transactionBuilder.build()
        );

        if (spanEventException == null) {
            return Collections.emptyList();
        }
        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder()
                .setApplicationName(applicationName)
                .setRange(Range.newRange(from, to))
                .setAgentId(transactionId.getAgentId())
                .setSpanEventException(spanEventException);

        return queryFunction.apply(
                builder.build()
        );
    }

    private List<SpanEventException> getSpanEventExceptions(ExceptionTraceQueryParameter queryParameter) {
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getExceptions(queryParameter);
        logger.info(spanEventExceptions.size());
        if (spanEventExceptions.isEmpty()) {
            return Collections.emptyList();
        }
        return spanEventExceptions;
    }

    private SpanEventException getSpanEventException(ExceptionTraceQueryParameter queryParameter) {
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getExceptions(queryParameter);
        logger.info(spanEventExceptions.size());
        if (spanEventExceptions.isEmpty()) {
            return null;
        }
        return spanEventExceptions.get(0);
    }

    private List<ExceptionTraceSummary> getExceptionTraceSummarys(ExceptionTraceQueryParameter queryParameter) {
        List<ExceptionTraceSummary> spanEventExceptions = exceptionTraceDao.getSummaries(queryParameter);
        logger.info(spanEventExceptions.size());
        if (spanEventExceptions.isEmpty()) {
            return Collections.emptyList();
        }
        return spanEventExceptions;
    }

}
