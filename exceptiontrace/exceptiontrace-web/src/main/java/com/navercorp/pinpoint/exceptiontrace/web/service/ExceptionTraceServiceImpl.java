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
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import com.navercorp.pinpoint.metric.web.util.Range;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        String traceId,
        long timestamp
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);

        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder(
                applicationName,
                Range.newRange(timestamp - 1, timestamp + 1)
        );
        transactionBuilder.setAgentId(transactionId.getAgentId());
        transactionBuilder.setTransactionId(transactionId);
        transactionBuilder.setSpanEventTimestamp(timestamp);
        return getSpanEventExceptions(transactionBuilder.build());
    }

    @Override
    public List<SpanEventException> getExceptionsInRange(
            String applicationName,
            @Nullable String agentId,
            long from,
            long to
    ) {
        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder(
                applicationName,
                Range.newRange(from, to)
        );
        builder.setAgentId(agentId);

        return getSpanEventExceptions(builder.build());
    }

    @Override
    public List<SpanEventException> getSimilarExceptions(
            String agentId,
            String traceId,
            long traceTimestamp,
            int exceptionDepth,
            String applicationName,
            long from,
            long to
    ) {
        final TransactionId transactionId = TransactionIdUtils.parseTransactionId(traceId);
        ExceptionTraceQueryParameter.Builder transactionBuilder = new ExceptionTraceQueryParameter.Builder(
                applicationName,
                Range.newRange(traceTimestamp - 1, traceTimestamp + 1)
        );
        transactionBuilder.setAgentId(agentId);
        transactionBuilder.forFindingSpecificException(transactionId, traceTimestamp, exceptionDepth);
        transactionBuilder.setRange(Range.newRange(from, to));
        final SpanEventException spanEventException = getSpanEventException(transactionBuilder.build());

        if (spanEventException == null) {
            return Collections.emptyList();
        }
        logger.info(spanEventException.getErrorClassName());

        ExceptionTraceQueryParameter.Builder builder = new ExceptionTraceQueryParameter.Builder(
                applicationName,
                Range.newRange(from, to)
        );
        builder.setAgentId(transactionId.getAgentId());
        builder.setSpanEventException(spanEventException);

        return getSpanEventExceptions(builder.build());
    }

    public List<SpanEventException> getSpanEventExceptions(ExceptionTraceQueryParameter queryParameter) {
        // logger.info(queryParameter);
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getExceptions(queryParameter);
        logger.info(spanEventExceptions.size());
        if (spanEventExceptions.isEmpty()) {
            return Collections.emptyList();
        }
        return spanEventExceptions;
    }

    public SpanEventException getSpanEventException(ExceptionTraceQueryParameter queryParameter) {
        // logger.info(queryParameter);
        List<SpanEventException> spanEventExceptions = exceptionTraceDao.getExceptions(queryParameter);
        logger.info(spanEventExceptions.size());
        if (spanEventExceptions.isEmpty()) {
            return null;
        }
        return spanEventExceptions.get(0);
    }
}
