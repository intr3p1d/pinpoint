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
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceValueView;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

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
            ExceptionTraceQueryParameter queryParameter
    ) {
        return getTransactionExceptions(
                queryParameter,
                this::getSpanEventExceptions
        );
    }

    @Override
    public List<SpanEventException> getExceptionsInRange(
            ExceptionTraceQueryParameter queryParameter
    ) {
        return getExceptionsInRange(
                queryParameter,
                this::getSimpleSpanEventExceptions
        );
    }

    @Override
    public List<ExceptionTraceSummary> getExceptionSummaries(ExceptionTraceQueryParameter queryParameter) {
        return null;
    }

    @Override
    public List<ExceptionTraceValueView> getValueViews(ExceptionTraceQueryParameter queryParameter) {
        return getExceptionsInRange(
                queryParameter,
                this::getExceptionTraceValueViews
        );
    }

    private <T> List<T> getTransactionExceptions(
            ExceptionTraceQueryParameter queryParameter,
            Function<ExceptionTraceQueryParameter, List<T>> queryFunction
    ) {
        return queryFunction.apply(queryParameter);
    }

    private <T> List<T> getExceptionsInRange(
            ExceptionTraceQueryParameter queryParameter,
            Function<ExceptionTraceQueryParameter, List<T>> queryFunction
    ) {
        return queryFunction.apply(queryParameter);
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

    private List<ExceptionTraceValueView> getExceptionTraceValueViews(ExceptionTraceQueryParameter queryParameter) {
        List<ExceptionTraceValueView> spanEventExceptions = exceptionTraceDao.getExceptionTraceValueViews(queryParameter);
        logger.info(spanEventExceptions.size());
        return spanEventExceptions;
    }
}
