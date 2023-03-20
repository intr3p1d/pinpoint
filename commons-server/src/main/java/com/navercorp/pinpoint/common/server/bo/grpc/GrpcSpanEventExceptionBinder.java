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
package com.navercorp.pinpoint.common.server.bo.grpc;

import com.navercorp.pinpoint.common.server.bo.exception.ExceptionWrapperBo;
import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.server.bo.exception.StackTraceElementWrapperBo;
import com.navercorp.pinpoint.grpc.trace.PException;
import com.navercorp.pinpoint.grpc.trace.PSpanEventException;
import com.navercorp.pinpoint.grpc.trace.PStackTraceElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class GrpcSpanEventExceptionBinder {

    public GrpcSpanEventExceptionBinder() {
    }

    @Nullable
    public SpanEventExceptionBo bind(PSpanEventException pSpanEventException) {
        if (pSpanEventException == null) {
            return null;
        }
        SpanEventExceptionBo spanEventExceptionBo = new SpanEventExceptionBo();

        spanEventExceptionBo.setExceptionWrappers(getExceptions(pSpanEventException.getExceptionsList()));
        spanEventExceptionBo.setStartTime(pSpanEventException.getStartTime());
        spanEventExceptionBo.setExceptionId(pSpanEventException.getExceptionId());

        return spanEventExceptionBo;
    }

    private List<ExceptionWrapperBo> getExceptions(List<PException> pExceptions) {
        return pExceptions.stream().map(
                this::getException
        ).collect(Collectors.toList());
    }

    private ExceptionWrapperBo getException(PException pException) {
        return new ExceptionWrapperBo(
                pException.getExceptionClassName(),
                pException.getExceptionMessage(),
                getStackTraceElements(pException.getStackTraceElementList())
        );
    }

    private List<StackTraceElementWrapperBo> getStackTraceElements(List<PStackTraceElement> pStackTraceElementList) {
        return pStackTraceElementList.stream().map(
                this::getStackTraceElement
        ).collect(Collectors.toList());
    }

    private StackTraceElementWrapperBo getStackTraceElement(PStackTraceElement pStackTraceElement) {
        return new StackTraceElementWrapperBo(
                pStackTraceElement.getClassName(),
                pStackTraceElement.getFileName(),
                pStackTraceElement.getLineNumber(),
                pStackTraceElement.getMethodName()
        );
    }
}
