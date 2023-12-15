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

import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.grpc.trace.PActiveTrace;
import com.navercorp.pinpoint.grpc.trace.PActiveTraceHistogram;
import com.navercorp.pinpoint.grpc.trace.PAgentStat;
import com.navercorp.pinpoint.grpc.trace.PAgentStatBatch;
import com.navercorp.pinpoint.grpc.trace.PAgentUriStat;
import com.navercorp.pinpoint.grpc.trace.PCpuLoad;
import com.navercorp.pinpoint.grpc.trace.PCustomMetricMessage;
import com.navercorp.pinpoint.grpc.trace.PDataSource;
import com.navercorp.pinpoint.grpc.trace.PDataSourceList;
import com.navercorp.pinpoint.grpc.trace.PDeadlock;
import com.navercorp.pinpoint.grpc.trace.PDirectBuffer;
import com.navercorp.pinpoint.grpc.trace.PFileDescriptor;
import com.navercorp.pinpoint.grpc.trace.PJvmGc;
import com.navercorp.pinpoint.grpc.trace.PJvmGcDetailed;
import com.navercorp.pinpoint.grpc.trace.PLoadedClass;
import com.navercorp.pinpoint.grpc.trace.PResponseTime;
import com.navercorp.pinpoint.grpc.trace.PTotalThread;
import com.navercorp.pinpoint.grpc.trace.PTransaction;
import com.navercorp.pinpoint.profiler.context.active.ActiveTraceHistogram;
import com.navercorp.pinpoint.profiler.context.active.ActiveTraceHistogramUtils;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentStatMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentStatMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.JvmGcDetailedMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.JvmGcMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.buffer.BufferMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.cpu.CpuLoadMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.datasource.DataSource;
import com.navercorp.pinpoint.profiler.monitor.metric.datasource.DataSourceMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.deadlock.DeadlockMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.filedescriptor.FileDescriptorMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.loadedclass.LoadedClassMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.response.ResponseTimeValue;
import com.navercorp.pinpoint.profiler.monitor.metric.totalthread.TotalThreadMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.transaction.TransactionMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.uri.AgentUriStatData;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author intr3p1d
 */
@Mapper(
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
                JvmGcTypeMapper.class,
                ThreadDumpMapper.class,
                UriStatMapper.class,
                CustomMetricMapper.class
        }
)
public interface AgentStatMapper {

    AgentStatMapper INSTANCE = Mappers.getMapper(AgentStatMapper.class);

    @Mappings({
            @Mapping(source = "agentStats", target = "agentStatList"),
    })
    PAgentStatBatch map(AgentStatMetricSnapshotBatch batch);

    @Mappings({
    })
    PAgentStat map(AgentStatMetricSnapshot snapshot);

    @Mappings({
            @Mapping(source = "type", target = "type", qualifiedBy = JvmGcTypeMapper.ToPJvmGcType.class),
    })
    PJvmGc map(JvmGcMetricSnapshot snapshot);

    PJvmGcDetailed map(JvmGcDetailedMetricSnapshot snapshot);

    @Mappings({
            @Mapping(source = "jvmCpuUsage", target = "jvmCpuLoad"),
            @Mapping(source = "systemCpuUsage", target = "systemCpuLoad"),
    })
    PCpuLoad map(CpuLoadMetricSnapshot snapshot);

    PTransaction map(TransactionMetricSnapshot snapshot);

    PActiveTrace map(ActiveTraceHistogram histogram);

    default PActiveTraceHistogram toPHistogram(ActiveTraceHistogram histogram) {
        if (histogram == null) {
            return null;
        }
        PActiveTraceHistogram.Builder pActiveTraceHistogram = PActiveTraceHistogram.newBuilder();
        if (histogram.getHistogramSchema() != null) {
            int typeCode = histogram.getHistogramSchema().getTypeCode();
            pActiveTraceHistogram.setHistogramSchemaType(typeCode);
        }
        final List<Integer> activeTraceCounts = toList(histogram);
        // To use addAll()
        pActiveTraceHistogram.addAllActiveTraceCount(activeTraceCounts);

        return pActiveTraceHistogram.build();
    }

    @Named("ToActiveTraceCount")
    default List<Integer> toList(ActiveTraceHistogram histogram) {
        return ActiveTraceHistogramUtils.asList(histogram);
    }

    PDataSourceList map(DataSourceMetricSnapshot snapshot);

    PDataSource map(DataSource dataSource);

    PResponseTime map(ResponseTimeValue value);

    @Mappings({
            @Mapping(source = "deadlockedThreadCount", target = "count")
    })
    PDeadlock map(DeadlockMetricSnapshot snapshot);

    PFileDescriptor map(FileDescriptorMetricSnapshot snapshot);

    PDirectBuffer map(BufferMetricSnapshot snapshot);

    PTotalThread map(TotalThreadMetricSnapshot snapshot);

    PLoadedClass map(LoadedClassMetricSnapshot snapshot);

    @Condition
    default boolean isNotZero(long value) {
        return value != 0;
    }
}
