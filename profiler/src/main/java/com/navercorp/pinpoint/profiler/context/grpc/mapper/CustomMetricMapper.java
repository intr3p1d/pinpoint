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
package com.navercorp.pinpoint.profiler.context.grpc.mapper;

import com.navercorp.pinpoint.grpc.trace.PCustomMetric;
import com.navercorp.pinpoint.grpc.trace.PCustomMetricMessage;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.grpc.trace.PIntValue;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.CustomMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.DoubleGaugeMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.IntCountMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.IntGaugeMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.LongCountMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.LongGaugeMetricVo;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * @author intr3p1d
 */
@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
        }
)
public interface CustomMetricMapper {


    PCustomMetricMessage map(AgentCustomMetricSnapshotBatch snapshotBatch);


    PCustomMetric map(String metricName, List<AgentCustomMetricSnapshot> snapshotList);


    @Mappings({
            @Mapping(source = "customMetricVos", target = "intCountMetric"),
            @Mapping(source = "customMetricVos", target = "longCountMetric"),
            @Mapping(source = "customMetricVos", target = "intGaugeMetric"),
            @Mapping(source = "customMetricVos", target = "longGaugeMetric"),
            @Mapping(source = "customMetricVos", target = "doubleGaugeMetric"),
    })
    PCustomMetric map(String metricName, CustomMetricVo representativeCustomMetricVo, CustomMetricVo[] customMetricVos);

    @Mappings(
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "snapshotList.value", target = "valuesList")
    })
    PIntCountMetric createFromIntCountMetric(String metricName, IntCountMetricVo[] snapshotList);

    PIntValue map


    PCustomMetric createFromLongCountMetric(String metricName, LongCountMetricVo[] snapshotList);

    PCustomMetric createFromIntGaugeMetric(String string, IntGaugeMetricVo[] snapshotList);

    PCustomMetric createFromLongGaugeMetric(String string, LongGaugeMetricVo[] snapshotList);

    PCustomMetric createFromDoubleGaugeMetric(String string, DoubleGaugeMetricVo[] snapshotList);
}
