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
import com.navercorp.pinpoint.metric.web.view.TimeSeriesValueView;
import com.navercorp.pinpoint.metric.web.view.TimeseriesValueGroupView;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class ExceptionTraceGroup implements TimeseriesValueGroupView {

    private final String exceptionClass;
    private final List<TimeSeriesValueView> values;

    private ExceptionTraceGroup(String exceptionClass, List<TimeSeriesValueView> values) {
        this.exceptionClass = exceptionClass;
        this.values = values;
    }

    public static ExceptionTraceGroup newGroupFromSummaries(
            TimeWindow timeWindow,
            SpanEventException base,
            List<ExceptionTraceSummary> exceptionTraceSummaries
    ) {
        return new ExceptionTraceGroup(
                base.getErrorClassName(),
                ExceptionTraceValue.createValueListGroupedBySimilarity(timeWindow, base, exceptionTraceSummaries)
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

        private final String fieldName;
        private final List<Integer> values;

        private static List<TimeSeriesValueView> createValueListGroupedBySimilarity(
                TimeWindow timeWindow,
                SpanEventException spanEventException,
                List<ExceptionTraceSummary> exceptionTraceSummaries
        ) {
            int[] timeseriesvalues = new int[(int) timeWindow.getWindowRangeCount()];
            Arrays.fill(timeseriesvalues, 0);

            for (ExceptionTraceSummary e : exceptionTraceSummaries) {
                timeseriesvalues[timeWindow.getWindowIndex(e.getTimestamp())] += e.getCount();
            }

            return List.of(new ExceptionTraceValue(
                    "",
                    Arrays.stream(timeseriesvalues).boxed().collect(Collectors.toList())
            ));
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
