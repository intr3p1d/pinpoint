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
import com.navercorp.pinpoint.grpc.trace.PDoubleValue;
import com.navercorp.pinpoint.grpc.trace.PDouleGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.grpc.trace.PIntGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PIntValue;
import com.navercorp.pinpoint.grpc.trace.PLongCountMetric;
import com.navercorp.pinpoint.grpc.trace.PLongGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PLongValue;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.CustomMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.IntCountMetricVo;
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
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "customMetricVos", target = "valuesList"),
    })
    PIntCountMetric createIntCountMetric(String metricName, CustomMetricVo[] customMetricVos);

    @Mappings({
            @Mapping(source = ".", target = "longCountMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PLongCountMetric pLongCountMetric);

    @Mappings({
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "customMetricVos", target = "valuesList", qualifiedByName = "ToPLongCountValues"),
    })
    PLongCountMetric createLongCountMetric(String metricName, CustomMetricVo[] customMetricVos);

    @Mappings({
            @Mapping(source = ".", target = "intGaugeMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PIntGaugeMetric pIntGaugeMetric);

    @Mappings({
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "customMetricVos", target = "valuesList", qualifiedByName = "ToPIntGaugeValues"),
    })
    PIntGaugeMetric createIntGaugeMetric(String metricName, CustomMetricVo[] customMetricVos);

    @Mappings({
            @Mapping(source = ".", target = "longGaugeMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PLongGaugeMetric pLongGaugeMetric);

    @Mappings({
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "customMetricVos", target = "valuesList", qualifiedByName = "ToPLongGaugeValues"),
    })
    PLongGaugeMetric createLongGaugeMetric(String metricName, CustomMetricVo[] customMetricVos);

    @Mappings({
            @Mapping(source = ".", target = "doubleGaugeMetric"),
            @Mapping(target = "unknownFields", ignore = true),
            @Mapping(target = "allFields", ignore = true),
    })
    PCustomMetric map(PDouleGaugeMetric pDouleGaugeMetric);

    @Mappings({
            @Mapping(source = "metricName", target = "name"),
            @Mapping(source = "customMetricVos", target = "valuesList", qualifiedByName = "ToPDoubleGaugeValues"),
    })
    PDouleGaugeMetric createDoubleGaugeMetric(String metricName, CustomMetricVo[] customMetricVos);


    class Holder<V> {
        V value;

        public Holder(V value) {
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

    default List<PIntValue> mapIntCountMetricVo(
            CustomMetricVo[] customMetricVos
    ) {
        List<PIntValue> intValues = new ArrayList<>();

        Holder<Integer> prevValue = new Holder<>(0);
        for (CustomMetricVo customMetricVo : customMetricVos) {
            intValues.add(toIntCountValue(customMetricVo, prevValue));
        }
        return intValues;
    }

    @Named("ToPLongCountValues")
    default List<PLongValue> mapLongCountMetricVo(
            CustomMetricVo[] customMetricVos
    ) {
        List<PLongValue> longValues = new ArrayList<>();

        Holder<Long> prevValue = new Holder<>((long) 0);
        for (CustomMetricVo customMetricVo : customMetricVos) {
            longValues.add(toLongCountValue(customMetricVo, prevValue));
        }
        return longValues;
    }

    @Named("ToPIntGaugeValues")
    default List<PIntValue> mapIntGaugeMetricVo(
            CustomMetricVo[] customMetricVos
    ) {
        List<PIntValue> intValues = new ArrayList<>();

        for (CustomMetricVo customMetricVo : customMetricVos) {
            intValues.add(toIntGaugeValue(customMetricVo));
        }
        return intValues;
    }

    @Named("ToPLongGaugeValues")
    default List<PLongValue> mapLongGaugeMetricVo(
            CustomMetricVo[] customMetricVos
    ) {
        List<PLongValue> longValues = new ArrayList<>();

        for (CustomMetricVo customMetricVo : customMetricVos) {
            longValues.add(toLongGaugeValue(customMetricVo));
        }
        return longValues;
    }

    @Named("ToPDoubleGaugeValues")
    default List<PDoubleValue> mapDoubleGaugeMetricVo(
            CustomMetricVo[] customMetricVos
    ) {
        List<PDoubleValue> doubleValues = new ArrayList<>();

        for (CustomMetricVo customMetricVo : customMetricVos) {
            doubleValues.add(toDoubleGaugeValue(customMetricVo));
        }
        return doubleValues;
    }

    default PIntValue toIntCountValue(CustomMetricVo customMetricVo, Holder<Integer> prev) {
        if (customMetricVo instanceof IntCountMetricVo) {
            int value = ((IntCountMetricVo) customMetricVo).getValue();
            PIntValue intValue = createIntValue(value - prev.getValue());
            prev.setValue(value);
            return intValue;
        }
        return createNotSetIntValue();
    }

    default PLongValue toLongCountValue(CustomMetricVo customMetricVo, Holder<Long> prev) {
        if (customMetricVo instanceof IntCountMetricVo) {
            long value = ((IntCountMetricVo) customMetricVo).getValue();
            PLongValue longValue = createLongValue(value - prev.getValue());
            prev.setValue(value);
            return longValue;
        }
        return createNotSetLongValue();
    }

    default PIntValue toIntGaugeValue(CustomMetricVo customMetricVo) {
        if (customMetricVo instanceof IntCountMetricVo) {
            int value = ((IntCountMetricVo) customMetricVo).getValue();
            return createIntValue(value);
        }
        return createNotSetIntValue();
    }

    default PLongValue toLongGaugeValue(CustomMetricVo customMetricVo) {
        if (customMetricVo instanceof IntCountMetricVo) {
            int value = ((IntCountMetricVo) customMetricVo).getValue();
            return createLongValue(value);
        }
        return createNotSetLongValue();
    }

    default PDoubleValue toDoubleGaugeValue(CustomMetricVo customMetricVo) {
        if (customMetricVo instanceof IntCountMetricVo) {
            int value = ((IntCountMetricVo) customMetricVo).getValue();
            return createDoubleValue(value);
        }
        return createNotSetDoubleValue();
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

    static PLongValue createLongValue(long value) {
        PLongValue.Builder builder = PLongValue.newBuilder();
        builder.setValue(value);
        return builder.build();
    }

    static PLongValue createNotSetLongValue() {
        PLongValue.Builder builder = PLongValue.newBuilder();
        builder.setIsNotSet(true);
        return builder.build();
    }

    static PDoubleValue createDoubleValue(double value) {
        PDoubleValue.Builder builder = PDoubleValue.newBuilder();
        builder.setValue(value);
        return builder.build();
    }

    static PDoubleValue createNotSetDoubleValue() {
        PDoubleValue.Builder builder = PDoubleValue.newBuilder();
        builder.setIsNotSet(true);
        return builder.build();
    }
}
