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
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import com.navercorp.pinpoint.metric.web.util.TimePrecision;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceService {

    List<SpanEventException> getTransactionExceptions(ExceptionTraceQueryParameter queryParameter);

    List<SpanEventException> getExceptionsInRange(ExceptionTraceQueryParameter queryParameter);

    List<SpanEventException> getSimilarExceptions(ExceptionTraceQueryParameter targetQuery, ExceptionTraceQueryParameter.Builder queryBuilder);

    List<ExceptionTraceSummary> getSummaryInRange(ExceptionTraceQueryParameter queryParameter);

    List<ExceptionTraceSummary> getSummaryOfSimilarExceptions(ExceptionTraceQueryParameter targetQuery, ExceptionTraceQueryParameter.Builder queryBuilder);
}
