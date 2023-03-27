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

package com.navercorp.pinpoint.exceptiontrace.collector.service;

import com.navercorp.pinpoint.collector.service.ExceptionTraceService;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.profiler.util.TransactionIdUtils;
import com.navercorp.pinpoint.common.server.bo.exception.ExceptionWrapperBo;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.exceptiontrace.collector.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
@Profile("metric")
public class PinotExceptionTraceService implements ExceptionTraceService {
    private final ExceptionTraceDao exceptionTraceDao;

    public PinotExceptionTraceService(ExceptionTraceDao exceptionTraceDao) {
        this.exceptionTraceDao = Objects.requireNonNull(exceptionTraceDao, "exceptionTraceDao");
    }

    @Override
    public void save(List<SpanEventExceptionBo> spanEventExceptionBoList, ServiceType applicationServiceType, String applicationId, String agentId, TransactionId transactionId, long spanId) {
        List<SpanEventException> spanEventExceptions = new ArrayList<>();
        for (SpanEventExceptionBo spanEventExceptionBo : spanEventExceptionBoList) {
            spanEventExceptions.addAll(toSpanEventExceptions(spanEventExceptionBo, applicationServiceType, applicationId, agentId, transactionId, spanId));
        }
        exceptionTraceDao.insert(spanEventExceptions);
    }

    private static List<SpanEventException> toSpanEventExceptions(
            SpanEventExceptionBo spanEventExceptionBo,
            ServiceType applicationServiceType, String applicationId, String agentId,
            TransactionId transactionId, long spanId) {
        List<SpanEventException> spanEventExceptions = new ArrayList<>();
        List<ExceptionWrapperBo> exceptions = spanEventExceptionBo.getExceptionWrappers();
        for (int i = 0; i < exceptions.size(); i++) {
            spanEventExceptions.add(
                    toSpanEventException(
                            exceptions.get(i), i,
                            applicationServiceType, applicationId, agentId,
                            transactionId, spanId,
                            spanEventExceptionBo.getExceptionId(),
                            spanEventExceptionBo.getStartTime()
                    )
            );
        }
        return spanEventExceptions;
    }

    private static SpanEventException toSpanEventException(
            ExceptionWrapperBo exceptionWrapperBo,
            int exceptionDepth,
            ServiceType applicationServiceType, String applicationId, String agentId,
            TransactionId transactionId, long spanId,
            long exceptionId,
            long startTime) {
        return SpanEventException.valueOf(
                startTime,
                transactionIdToString(transactionId),
                spanId,
                exceptionId,
                applicationServiceType.getName(),
                applicationId,
                agentId,
                exceptionWrapperBo.getExceptionClassName(),
                exceptionWrapperBo.getExceptionMessage(),
                exceptionDepth,
                exceptionWrapperBo.getStackTraceElements()
        );
    }

    private static String transactionIdToString(TransactionId transactionId) {
        return TransactionIdUtils.formatString(transactionId);
    }

}
