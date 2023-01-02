package com.navercorp.pinpoint.metric.web.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.util.TimeWindow;
import org.apache.commons.lang3.StringUtils;
import com.navercorp.pinpoint.metric.web.view.TimeSeriesValueView;
import com.navercorp.pinpoint.metric.web.view.TimeseriesValueGroupView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class ExceptionTraceGroup implements TimeseriesValueGroupView {

    private final String exceptionClass;

    private final List<TimeSeriesValueView> values;

    public static final ExceptionTraceGroup EMPTY_EXCEPTION_TRACE_GROUP = new ExceptionTraceGroup();

    public ExceptionTraceGroup() {
        this.exceptionClass = StringUtils.EMPTY;
        this.values = Collections.emptyList();

    }

    public ExceptionTraceGroup(String exceptionClass, TimeWindow timeWindow, List<SpanEventException> spanEventExceptions) {
        this.exceptionClass = exceptionClass;
        this.values = ExceptionTraceValue.createValueList(timeWindow, spanEventExceptions);
    }

    @Override
    public String getGroupName() {
        return exceptionClass;
    }

    @Override
    public List<TimeSeriesValueView> getMetricValues() {
        return values;
    }

    public static class ExceptionTraceValue implements TimeSeriesValueView {

        private static final String FIELD_NAME = "trace-count";
        private final String fieldName;
        private final List<Integer> values;

        public static List<TimeSeriesValueView> createValueList(TimeWindow timeWindow, List<SpanEventException> spanEventExceptions) {
            Objects.requireNonNull(spanEventExceptions);

            // List<SpanEventException> to number of occasion
            List<TimeSeriesValueView> values = new ArrayList<>();

            List<Integer> exceptionOccasions = spanEventExceptions.stream().collect(
                    Collectors.groupingBy(x -> timeWindow.getWindowIndex(x.getTimestamp()))
            ).values().stream().map(
                    List::size
            ).collect(Collectors.toList());

            TimeSeriesValueView exceptionTraceValue = new ExceptionTraceValue(FIELD_NAME, exceptionOccasions);
            return List.of(exceptionTraceValue);
        }

        public ExceptionTraceValue(String fieldName, List<Integer> values) {
            this.fieldName = fieldName;
            this.values = values;
        }

        @Override
        public String getFieldName() {
            return this.fieldName;
        }

        @Override
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<String> getTags() {
            return null;
        }

        @Override
        public List<Integer> getValues() {
            return values;
        }
    }

}
