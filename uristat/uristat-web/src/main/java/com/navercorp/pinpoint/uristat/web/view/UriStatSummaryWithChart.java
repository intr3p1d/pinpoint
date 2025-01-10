/*
 * Copyright 2025 NAVER Corp.
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
package com.navercorp.pinpoint.uristat.web.view;

import com.navercorp.pinpoint.uristat.web.model.UriStatSummary;

import java.util.List;

/**
 * @author intr3p1d
 */
public class UriStatSummaryWithChart {

    UriStatSummary summary;
    UriStatView chartView;

    public UriStatSummaryWithChart(UriStatSummary summary, UriStatView chartView) {
        this.summary = summary;
        this.chartView = chartView;
    }

    public List<UriStatSummaryWithChart> reduce(List<UriStatSummary> summaries) {
        // summaries 를 uri, version 기준으로 groupby 하여
        // 같은 group 내의 totalCount, failureCount, maxTimeMs, avgTimeMs, apdex 는 합산하고
        // chartValue 는 List 로 묶는다.
        // 그리고 그룹별로 UriStatSummaryWithChart 를 생성하여 List 로 반환한다.

//        List<List<UriStatSummary>> listOfSummaries = summaries.stream().

        return null;
    }

}
