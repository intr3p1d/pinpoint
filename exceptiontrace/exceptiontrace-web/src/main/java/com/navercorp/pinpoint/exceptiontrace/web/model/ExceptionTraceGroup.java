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

import com.navercorp.pinpoint.metric.web.view.TimeSeriesValueView;
import com.navercorp.pinpoint.metric.web.view.TimeseriesValueGroupView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class ExceptionTraceGroup implements TimeseriesValueGroupView {

    private final String groupName;
    private final List<TimeSeriesValueView> values;

    private ExceptionTraceGroup(String groupName, List<TimeSeriesValueView> values) {
        this.groupName = groupName;
        this.values = values;
    }

    public static ExceptionTraceGroup newGroupFromValueViews(
            String groupName,
            List<ExceptionTraceValueView> exceptionTraceValueViews
    ) {
        List<TimeSeriesValueView> timeSeriesValueViews = exceptionTraceValueViews.stream().map(
                (ExceptionTraceValueView e) -> (TimeSeriesValueView) e
        ).collect(Collectors.toList());
        return new ExceptionTraceGroup(
                groupName,
                timeSeriesValueViews
        );
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public List<TimeSeriesValueView> getMetricValues() {
        return values;
    }

}
