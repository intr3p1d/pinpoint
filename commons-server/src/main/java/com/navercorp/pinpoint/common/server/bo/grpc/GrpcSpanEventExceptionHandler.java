package com.navercorp.pinpoint.common.server.bo.grpc;

import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.common.server.bo.exception.StackTraceElementWrapperBo;
import com.navercorp.pinpoint.grpc.trace.PSpanEventException;
import com.navercorp.pinpoint.grpc.trace.PStackTraceElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class GrpcSpanEventExceptionHandler {

    public GrpcSpanEventExceptionHandler() {
    }

    @Nullable
    public SpanEventExceptionBo bind(PSpanEventException pSpanEventException) {
        if (pSpanEventException == null) {
            return null;
        }
        SpanEventExceptionBo spanEventExceptionBo = new SpanEventExceptionBo();

        spanEventExceptionBo.setExceptionClassName(pSpanEventException.getExceptionClassName());
        spanEventExceptionBo.setExceptionMessage(pSpanEventException.getExceptionMessage());

        spanEventExceptionBo.setStackTraceElements(getStackTraceElements(pSpanEventException.getStackTraceElementList()));

        spanEventExceptionBo.setStartTime(pSpanEventException.getStartTime());
        spanEventExceptionBo.setElapsedTime(pSpanEventException.getEndElapsed());

        return spanEventExceptionBo;
    }

    private List<StackTraceElementWrapperBo> getStackTraceElements(List<PStackTraceElement> pStackTraceElementList) {
        return pStackTraceElementList.stream().map(
                pStackTraceElement -> {
                    StackTraceElementWrapperBo s = new StackTraceElementWrapperBo();
                    s.setClassName(pStackTraceElement.getClassName());
                    s.setFileName(pStackTraceElement.getFileName());
                    s.setLineNumber(pStackTraceElement.getLineNumber());
                    s.setMethodName(pStackTraceElement.getMethodName());

                    return s;
                }
        ).collect(Collectors.toList());
    }
}
