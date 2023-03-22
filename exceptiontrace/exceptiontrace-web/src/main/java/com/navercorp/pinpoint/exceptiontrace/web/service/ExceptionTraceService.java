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

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author intr3p1d
 */
public interface ExceptionTraceService {

    List<SpanEventException> getTransactionExceptions(String applicationName, String agentId, String traceId, long spanId, long exceptionId);

    List<SpanEventException> getExceptionsInRange(String applicationName, @Nullable String agentId, long from, long to);

    List<SpanEventException> getSimilarExceptions(String applicationName, String agentId, long from, long to, String traceId, long spanId, long exceptionId, int exceptionDepth);

    List<ExceptionTraceSummary> getSummaryInRange(String applicationName, @Nullable String agentId, long from, long to);

    List<ExceptionTraceSummary> getSummaryOfSimilarExceptions(String applicationName, String agentId, long from, long to, String traceId, long spanId, long exceptionId, int exceptionDepth);
}
