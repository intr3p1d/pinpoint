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
