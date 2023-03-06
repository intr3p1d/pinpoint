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

package com.navercorp.pinpoint.exceptiontrace.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.web.util.TimeWindow;
import org.apache.commons.lang3.StringUtils;
import com.navercorp.pinpoint.metric.web.view.TimeSeriesValueView;
import com.navercorp.pinpoint.metric.web.view.TimeseriesValueGroupView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class ExceptionTraceGroup implements TimeseriesValueGroupView {

    private final String exceptionClass;

    private final List<TimeSeriesValueView> values;

    public static final ExceptionTraceGroup EMPTY_EXCEPTION_TRACE_GROUP = new ExceptionTraceGroup();

    private ExceptionTraceGroup() {
        this.exceptionClass = StringUtils.EMPTY;
        this.values = Collections.emptyList();

    }

    private ExceptionTraceGroup(String exceptionClass, List<TimeSeriesValueView> values) {
        this.exceptionClass = exceptionClass;
        this.values = values;
    }

    public static ExceptionTraceGroup newGroupFromExceptions(String exceptionClass, TimeWindow timeWindow, List<SpanEventException> spanEventExceptions) {
        return new ExceptionTraceGroup(
                exceptionClass,
                ExceptionTraceValue.createValueList(timeWindow, spanEventExceptions)
        );
    }

    public static ExceptionTraceGroup newGroupFromSummaries(
            String exceptionClass,
            TimeWindow timeWindow,
            @Nullable SpanEventException spanEventException,
            List<ExceptionTraceSummary> exceptionTraceSummaries
    ) {
        if (spanEventException == null) {
            // TODO
        }
        return new ExceptionTraceGroup(
                exceptionClass,
                ExceptionTraceValue.createValueListFromSummary(timeWindow, spanEventException, exceptionTraceSummaries)
        );
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
            List<Integer> values = new ArrayList<>(Collections.nCopies((int) timeWindow.getWindowRangeCount(), 0));

            spanEventExceptions.stream().collect(
                    Collectors.groupingBy((SpanEventException x) -> timeWindow.getWindowIndex(x.getTimestamp()))
            ).forEach(
                    (i, e) -> values.set(i, e.size())
            );

            TimeSeriesValueView exceptionTraceValue = new ExceptionTraceValue(FIELD_NAME, values);
            return List.of(exceptionTraceValue);
        }

        public static List<TimeSeriesValueView> createValueListFromSummary(
                TimeWindow timeWindow,
                SpanEventException spanEventException,
                List<ExceptionTraceSummary> exceptionTraceSummaries
        ) {
            Objects.requireNonNull(exceptionTraceSummaries);

            Map<Similarity, int[]> fieldNameToListMap = new EnumMap<>(Similarity.class);
            for (Similarity similarity : Similarity.values()) {
                int[] values = new int[(int) timeWindow.getWindowRangeCount()];
                Arrays.fill(values, 0);
                fieldNameToListMap.put(similarity, values);
            }

            for (ExceptionTraceSummary e : exceptionTraceSummaries) {
                fieldNameToListMap.get(
                        similarityToFieldName(spanEventException, e)
                )[timeWindow.getWindowIndex(e.getTimestamp())] += e.getCount();
            }

            List<TimeSeriesValueView> timeSeriesValueViews = new ArrayList<>();
            for (Map.Entry<Similarity, int[]> e : fieldNameToListMap.entrySet()) {
                timeSeriesValueViews.add(
                        new ExceptionTraceValue(e.toString(), Arrays.stream(e.getValue()).boxed().collect(Collectors.toList()))
                );
            }

            return timeSeriesValueViews;
        }

        private static Similarity similarityToFieldName(SpanEventException base, ExceptionTraceSummary given) {
            return Similarity.valueOf(
                    Objects.equals(base.getErrorMessage(), given.getErrorMessage()),
                    Objects.equals(base.getStackTrace().toString(), given.getStackTrace())
            );
        }

        private enum Similarity {
            IDENTICAL,
            DIFFERENT_MESSAGE,
            DIFFERENT_STACKTRACE,
            DIFFERENT_MESSAGE_AND_STACKTRACE;

            public static Similarity valueOf(boolean messagesAreSame, boolean stacktraceAreSame) {
                if (messagesAreSame) {
                    if (stacktraceAreSame) {
                        return IDENTICAL;
                    }
                    return DIFFERENT_STACKTRACE;
                } else {
                    if (stacktraceAreSame) {
                        return DIFFERENT_MESSAGE;
                    }
                    return DIFFERENT_MESSAGE_AND_STACKTRACE;
                }
            }
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
