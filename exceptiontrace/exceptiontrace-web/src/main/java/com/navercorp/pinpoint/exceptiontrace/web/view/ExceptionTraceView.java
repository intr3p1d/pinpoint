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

package com.navercorp.pinpoint.exceptiontrace.web.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceGroup;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
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

    private ExceptionTraceView(List<Long> timestampList, List<TimeseriesValueGroupView> exceptionTraces) {
        this.timestampList = timestampList;
        this.exceptionTrace.addAll(exceptionTraces);
    }

    public static ExceptionTraceView newViewFromSummaries(
            String exceptionClass,
            TimeWindow timeWindow,
            List<ExceptionTraceSummary> exceptionTraceSummaries
    ) {
        Objects.requireNonNull(timeWindow, "timeWindow");
        Objects.requireNonNull(exceptionTraceSummaries, "exceptionTraceSummaries");

        List<Long> timestampList = createTimeStampList(timeWindow);
        List<TimeseriesValueGroupView> timeseriesValueGroupViews = new ArrayList<>();
        timeseriesValueGroupViews.add(
                ExceptionTraceGroup.newGroupFromSummaries(timeWindow, exceptionClass, exceptionTraceSummaries)
        );

        return new ExceptionTraceView(
                timestampList, timeseriesValueGroupViews
        );
    }

    private static List<Long> createTimeStampList(TimeWindow timeWindow) {
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
