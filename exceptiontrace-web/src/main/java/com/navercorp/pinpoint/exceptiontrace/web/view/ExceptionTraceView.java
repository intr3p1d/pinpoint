package com.navercorp.pinpoint.exceptiontrace.web.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceGroup;
import com.navercorp.pinpoint.metric.web.util.TimeWindow;
import com.navercorp.pinpoint.metric.web.view.TimeSeriesView;
import com.navercorp.pinpoint.metric.web.view.TimeseriesValueGroupView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class ExceptionTraceView implements TimeSeriesView {

    private static final String TITLE = "exceptionTrace";

    private final List<Long> timestampList;

    private final List<TimeseriesValueGroupView> exceptionTrace = new ArrayList<>();

    public ExceptionTraceView(String exceptionClass, TimeWindow timeWindow, List<SpanEventException> spanEventExceptions) {
        Objects.requireNonNull(timeWindow, "timeWindow");
        Objects.requireNonNull(spanEventExceptions, "spanEventException");
        this.timestampList = createTimeStampList(timeWindow);
        if (spanEventExceptions.isEmpty()) {
            this.exceptionTrace.add(ExceptionTraceGroup.EMPTY_EXCEPTION_TRACE_GROUP);
        } else {
            this.exceptionTrace.add(new ExceptionTraceGroup(exceptionClass, timeWindow, spanEventExceptions));
        }
    }

    private List<Long> createTimeStampList(TimeWindow timeWindow) {
        List<Long> timestampList = new ArrayList<>((int) timeWindow.getWindowRangeCount());

        for (Long timestamp : timeWindow) {
            timestampList.add(timestamp);
        }

        return timestampList;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getUnit() {
        return null;
    }

    @Override
    public List<Long> getTimestamp() {
        return timestampList;
    }

    @Override
    public List<TimeseriesValueGroupView> getMetricValueGroups() {
        return this.exceptionTrace;
    }
}
