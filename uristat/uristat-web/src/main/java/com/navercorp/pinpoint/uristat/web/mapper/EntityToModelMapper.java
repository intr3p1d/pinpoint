/*
 * Copyright 2024 NAVER Corp.
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
package com.navercorp.pinpoint.uristat.web.mapper;

import com.google.common.primitives.Doubles;
import com.navercorp.pinpoint.common.util.MathUtils;
import com.navercorp.pinpoint.uristat.web.entity.UriStatChartEntity;
import com.navercorp.pinpoint.uristat.web.entity.UriStatSummaryEntity;
import com.navercorp.pinpoint.uristat.web.model.UriStatChartValue;
import com.navercorp.pinpoint.uristat.web.model.UriStatSummary;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * @author intr3p1d
 */
@Mapper(
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface EntityToModelMapper {

    @Mapping(target = "apdex", source = "entity", qualifiedByName = "toApdex")
    @Mapping(target = "avgTimeMs", source = "entity", qualifiedByName = "toAvgTimeMs")
    UriStatSummary toModel(UriStatSummaryEntity entity);

    @Named("toApdex")
    default Double toApdex(UriStatSummaryEntity entity) {
        return MathUtils.average(entity.getApdexRaw(), entity.getTotalCount());
    }

    @Named("toAvgTimeMs")
    default Double toAvgTimeMs(UriStatSummaryEntity entity) {
        return MathUtils.average(entity.getTotalTimeMs(), entity.getTotalCount());
    }

    @Retention(RetentionPolicy.CLASS)
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "version", source = "version")
    public @interface ToChartValue {
    }

    @ToChartValue
    @Mapping(target = "chartType", constant = "bar")
    @Mapping(target = "unit", constant = "count")
    @Mapping(target = "values", source = "entity", qualifiedByName = "toTotalHistogram")
    UriStatChartValue toTotalChart(UriStatChartEntity entity);

    @ToChartValue
    @Mapping(target = "chartType", constant = "bar")
    @Mapping(target = "unit", constant = "count")
    @Mapping(target = "values", source = "entity", qualifiedByName = "toFailureHistogram")
    UriStatChartValue toFailureChart(UriStatChartEntity entity);

    @ToChartValue
    @Mapping(target = "chartType", constant = "line")
    @Mapping(target = "unit", constant = "ms")
    @Mapping(target = "values", source = "entity", qualifiedByName = "toLatency")
    UriStatChartValue toLatencyChart(UriStatChartEntity entity);


    @ToChartValue
    @Mapping(target = "chartType", constant = "line")
    @Mapping(target = "unit", constant = "")
    @Mapping(target = "values", source = "entity", qualifiedByName = "toApdexList")
    UriStatChartValue toApdexChart(UriStatChartEntity entity);

    @Named("toTotalHistogram")
    default List<Double> toTotalHistogram(UriStatChartEntity entity) {
        return toHistogram(
                entity.getTot0(), entity.getTot1(), entity.getTot2(), entity.getTot3(),
                entity.getTot4(), entity.getTot5(), entity.getTot6(), entity.getTot7()
        );
    }

    @Named("toFailureHistogram")
    default List<Double> toFailureHistogram(UriStatChartEntity entity) {
        return toHistogram(
                entity.getFail0(), entity.getFail1(), entity.getFail2(), entity.getFail3(),
                entity.getFail4(), entity.getFail5(), entity.getFail6(), entity.getFail7()
        );
    }

    default List<Double> toHistogram(
            Double hist0, Double hist1, Double hist2, Double hist3,
            Double hist4, Double hist5, Double hist6, Double hist7
    ) {
        return Doubles.asList(hist0, hist1, hist2, hist3, hist4, hist5, hist6, hist7);
    }

    @Named("toLatency")
    default List<Double> toLatency(UriStatChartEntity entity) {
        return Doubles.asList((entity.getCount() == 0) ? -1 : (entity.getTotalTimeMs() / entity.getCount()), entity.getMaxLatencyMs());
    }

    @Named("toApdexList")
    default List<Double> toApdexList(UriStatChartEntity entity) {
        return Doubles.asList((entity.getCount() == 0) ? -1 : (entity.getApdexRaw() / entity.getCount()));
    }
}
