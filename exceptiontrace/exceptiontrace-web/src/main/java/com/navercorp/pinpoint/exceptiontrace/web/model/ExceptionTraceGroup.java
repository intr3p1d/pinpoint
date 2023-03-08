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
        if (base == null) {
            return new ExceptionTraceGroup(
                    "",
                    ExceptionTraceValue.createValueList(timeWindow, exceptionTraceSummaries)
            );
        }

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

        public static List<TimeSeriesValueView> createValueList(
                TimeWindow timeWindow,
                List<ExceptionTraceSummary> exceptionTraceSummaries
        ) {
            return createValueListGroupedBySimilarity(
                    timeWindow, null, Similarity.getNotCheckedSimilarities(), exceptionTraceSummaries
            );
        }

        public static List<TimeSeriesValueView> createValueListGroupedBySimilarity(
                TimeWindow timeWindow,
                @Nonnull SpanEventException base,
                List<ExceptionTraceSummary> exceptionTraceSummaries
        ) {
            return createValueListGroupedBySimilarity(
                    timeWindow, base, Similarity.getCheckedSimilarities(), exceptionTraceSummaries
            );
        }

        private static List<TimeSeriesValueView> createValueListGroupedBySimilarity(
                TimeWindow timeWindow,
                SpanEventException spanEventException,
                Similarity[] keyGroups,
                List<ExceptionTraceSummary> exceptionTraceSummaries
        ) {
            Map<Similarity, int[]> fieldNameToListMap = newSimilarityToValueListMap(timeWindow, keyGroups);

            for (ExceptionTraceSummary e : exceptionTraceSummaries) {
                fieldNameToListMap.get(
                        compareAndGetSimilarity(spanEventException, e)
                )[timeWindow.getWindowIndex(e.getTimestamp())] += e.getCount();
            }

            return newTimeSeriesValueView(
                    fieldNameToListMap.entrySet(),
                    (Map.Entry<Similarity, int[]> e) -> e.getKey().toString(),
                    (Map.Entry<Similarity, int[]> e) -> Arrays.stream(e.getValue()).boxed().collect(Collectors.toList())
            );
        }

        private static Map<Similarity, int[]> newSimilarityToValueListMap(
                TimeWindow timeWindow,
                Similarity[] similarities
        ) {
            Map<Similarity, int[]> fieldNameToListMap = new EnumMap<>(Similarity.class);
            for (Similarity similarity : similarities) {
                int[] values = new int[(int) timeWindow.getWindowRangeCount()];
                Arrays.fill(values, 0);
                fieldNameToListMap.put(similarity, values);
            }
            return fieldNameToListMap;
        }

        private static <T> List<TimeSeriesValueView> newTimeSeriesValueView(
                Collection<T> collections,
                Function<T, String> toString,
                Function<T, List<Integer>> toIntegerArray
        ) {
            List<TimeSeriesValueView> timeSeriesValueViews = new ArrayList<>();
            for (T t : collections) {
                timeSeriesValueViews.add(
                        new ExceptionTraceValue(
                                toString.apply(t),
                                toIntegerArray.apply(t)
                        )
                );
            }
            return timeSeriesValueViews;
        }

        private static Similarity compareAndGetSimilarity(
                SpanEventException base,
                ExceptionTraceSummary given
        ) {
            if (base == null || given == null) {
                return Similarity.NOT_CHECKED;
            }
            return Similarity.valueOf(
                    Objects.equals(base.getErrorMessage(), given.getErrorMessage()),
                    Objects.equals(base.getStackTraceHash(), given.getStackTraceHash())
            );
        }

        private enum Similarity {
            IDENTICAL,
            DIFFERENT_MESSAGE,
            DIFFERENT_STACKTRACE,
            DIFFERENT_MESSAGE_AND_STACKTRACE,

            NOT_CHECKED;

            private static final Similarity[] checkedSimilarities = new Similarity[]{Similarity.IDENTICAL, Similarity.DIFFERENT_MESSAGE, Similarity.DIFFERENT_STACKTRACE, Similarity.DIFFERENT_MESSAGE_AND_STACKTRACE};

            private static final Similarity[] notCheckedSimilarities = new Similarity[]{Similarity.NOT_CHECKED};

            public static Similarity[] getCheckedSimilarities() {
                return checkedSimilarities;
            }

            public static Similarity[] getNotCheckedSimilarities() {
                return notCheckedSimilarities;
            }

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
