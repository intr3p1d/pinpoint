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
import com.navercorp.pinpoint.grpc.trace.PDouleGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.grpc.trace.PIntGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PIntValue;
import com.navercorp.pinpoint.grpc.trace.PLongCountMetric;
import com.navercorp.pinpoint.grpc.trace.PLongGaugeMetric;
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
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * @author intr3p1d
 */
@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
        }
)
public interface CustomMetricMapper {

    CustomMetricMapper INSTANCE = Mappers.getMapper(CustomMetricMapper.class);

    PCustomMetricMessage map(AgentCustomMetricSnapshotBatch snapshotBatch);

    default PCustomMetric create(
            String metricName,
            List<AgentCustomMetricSnapshot> agentCustomMetricSnapshotList
    ) {
        int size = agentCustomMetricSnapshotList.size();

        CustomMetricVo representativeCustomMetricVo = null;
        CustomMetricVo[] customMetricVos = new CustomMetricVo[size];
        for (int i = 0; i < size; i++) {
            AgentCustomMetricSnapshot agentCustomMetricSnapshot = agentCustomMetricSnapshotList.get(i);
            CustomMetricVo customMetricVo = agentCustomMetricSnapshot.get(metricName);
            customMetricVos[i] = customMetricVo;
            if (customMetricVo == null) {
                continue;
            }
            if (representativeCustomMetricVo == null) {
                representativeCustomMetricVo = customMetricVo;
            }
        }

        return map(metricName, representativeCustomMetricVo, customMetricVos);
    }


    default PCustomMetric map(
            String metricName,
            CustomMetricVo representativeCustomMetricVo,
            CustomMetricVo[] customMetricVos
    ) {
        if (representativeCustomMetricVo instanceof IntCountMetricVo) {
            return map(
                    createIntCountMetric(metricName, customMetricVos)
            );
        }
        /*
        if (representativeCustomMetricVo instanceof LongCountMetricVo) {
            return createLongCountMetric(metricName, customMetricVos);
        }
        if (representativeCustomMetricVo instanceof IntGaugeMetricVo) {
            return createIntGaugeMetric(metricName, customMetricVos);
        }
        if (representativeCustomMetricVo instanceof LongGaugeMetricVo) {
            return createLongGaugeMetric(metricName, customMetricVos);
        }
        if (representativeCustomMetricVo instanceof DoubleGaugeMetricVo) {
            return createDoubleGaugeMetric(metricName, customMetricVos);
        }
         */
        return null;
    }


    @Mappings({
            @Mapping(source = ".", target = "intCountMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PIntCountMetric intCountMetric);

    @Mappings({
            @Mapping(source = ".", target = "longCountMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PLongCountMetric pLongCountMetric);

    @Mappings({
            @Mapping(source = ".", target = "intGaugeMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PIntGaugeMetric pIntGaugeMetric);

    @Mappings({
            @Mapping(source = ".", target = "longGaugeMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PLongGaugeMetric pLongGaugeMetric);

    @Mappings({
            @Mapping(source = ".", target = "doubleGaugeMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PDouleGaugeMetric pDouleGaugeMetric);

    @Mappings({
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "customMetricVos", target = "valuesList", qualifiedByName = "ToPIntValues"),
    })
    PIntCountMetric createIntCountMetric(String metricName, CustomMetricVo[] customMetricVos);

    @Named("ToPIntValues")
    default List<PIntValue> createCountMetric(CustomMetricVo[] customMetricVos) {
        return createCountMetrics(
                customMetricVos,
                IntCountMetricVo.class::isInstance,
                (CustomMetricVo customMetricVo) -> ((IntCountMetricVo) customMetricVo).getValue(),
                CustomMetricMapper::createIntValue,
                CustomMetricMapper::createNotSetIntValue
        );
    }
    default <CountMetric, PValue, Type extends Number> List<PValue> mapAbstractCountMetric(
            CustomMetricVo[] customMetricVos,
            Predicate<CustomMetricVo> isInstanceOf,
            Function<CountMetric, Type> extractor,
            Function<Type, PValue> createValue,
            Supplier<PValue> createNotSetValue
    ) {
        List<PValue> intValues = new ArrayList<>();

        Type prevValue = 0;
        for (CustomMetricVo customMetricVo : customMetricVos) {
            if (isInstanceOf.test(customMetricVo)) {
                Type value = extractor.apply((CountMetric) customMetricVo);
                intValues.add(createValue.apply(value - prevValue));
                prevValue = value;
            } else {
                intValues.add(createNotSetValue.get());
            }
        }

        return intValues;
    }


    default <CountMetric, PValue> List<PValue> createCountMetrics(
            CustomMetricVo[] customMetricVos,
            Predicate<CustomMetricVo> isInstanceOf,
            ToIntFunction<CountMetric> extractor,
            IntFunction<PValue> createValue,
            Supplier<PValue> createNotSetValue
    ) {
        List<PValue> intValues = new ArrayList<>();

        int prevValue = 0;
        for (CustomMetricVo customMetricVo : customMetricVos) {
            if (isInstanceOf.test(customMetricVo)) {
                int value = extractor.applyAsInt((CountMetric) customMetricVo);
                intValues.add(createValue.apply(value - prevValue));
                prevValue = value;
            } else {
                intValues.add(createNotSetValue.get());
            }
        }

        return intValues;
    }

    @Named("ToPIntValues")
    default List<PIntValue> map(CustomMetricVo[] customMetricVos) {
        List<PIntValue> intValues = new ArrayList<>();

        int prevValue = 0;
        for (CustomMetricVo customMetricVo : customMetricVos) {
            if (customMetricVo instanceof IntCountMetricVo) {
                int value = ((IntCountMetricVo) customMetricVo).getValue();
                intValues.add(createIntValue(value - prevValue));
            } else {
                intValues.add(createNotSetIntValue());
            }
        }

        return intValues;
    }

    static PIntValue createIntValue(int value) {
        PIntValue.Builder builder = PIntValue.newBuilder();
        builder.setValue(value);
        return builder.build();
    }

    static PIntValue createNotSetIntValue() {
        PIntValue.Builder builder = PIntValue.newBuilder();
        builder.setIsNotSet(true);
        return builder.build();
    }

    PCustomMetric createFromLongCountMetric(String metricName, LongCountMetricVo[] snapshotList);

    PCustomMetric createFromIntGaugeMetric(String string, IntGaugeMetricVo[] snapshotList);

    PCustomMetric createFromLongGaugeMetric(String string, LongGaugeMetricVo[] snapshotList);

    PCustomMetric createFromDoubleGaugeMetric(String string, DoubleGaugeMetricVo[] snapshotList);
}
