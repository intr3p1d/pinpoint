/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.grpc;

import com.google.protobuf.GeneratedMessageV3;
import com.navercorp.pinpoint.common.profiler.message.MessageConverter;
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
import com.navercorp.pinpoint.grpc.trace.PJvmGcType;
import com.navercorp.pinpoint.grpc.trace.PLoadedClass;
import com.navercorp.pinpoint.grpc.trace.PResponseTime;
import com.navercorp.pinpoint.grpc.trace.PThreadDump;
import com.navercorp.pinpoint.grpc.trace.PTotalThread;
import com.navercorp.pinpoint.grpc.trace.PTransaction;
import com.navercorp.pinpoint.profiler.context.active.ActiveTraceHistogram;
import com.navercorp.pinpoint.profiler.context.active.ActiveTraceHistogramUtils;
import com.navercorp.pinpoint.profiler.context.grpc.mapper.AgentStatMapper;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentStatMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentStatMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.JvmGcDetailedMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.JvmGcMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.MetricType;
import com.navercorp.pinpoint.profiler.monitor.metric.buffer.BufferMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.cpu.CpuLoadMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.datasource.DataSource;
import com.navercorp.pinpoint.profiler.monitor.metric.datasource.DataSourceMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.deadlock.DeadlockMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.deadlock.ThreadDumpMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.filedescriptor.FileDescriptorMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.loadedclass.LoadedClassMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.response.ResponseTimeValue;
import com.navercorp.pinpoint.profiler.monitor.metric.totalthread.TotalThreadMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.transaction.TransactionMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.uri.AgentUriStatData;

import java.util.List;

/**
 * @author jaehong.kim
 */
public class GrpcStatMessageConverter implements MessageConverter<MetricType, GeneratedMessageV3> {
    private final MessageConverter<MetricType, PCustomMetricMessage> customMetricMessageConverter = new GrpcCustomMetricMessageConverter();
    private final MessageConverter<MetricType, PAgentUriStat> uriStatMessageConverter = new GrpcUriStatMessageConverter();

    private final AgentStatMapper mapper = AgentStatMapper.INSTANCE;

    @Override
    public GeneratedMessageV3 toMessage(MetricType message) {
        if (message instanceof AgentStatMetricSnapshotBatch) {
            final AgentStatMetricSnapshotBatch agentStatMetricSnapshotBatch = (AgentStatMetricSnapshotBatch) message;
            return mapper.map(agentStatMetricSnapshotBatch);
        } else if (message instanceof AgentStatMetricSnapshot) {
            final AgentStatMetricSnapshot agentStatMetricSnapshot = (AgentStatMetricSnapshot) message;
            return mapper.map(agentStatMetricSnapshot);
        } else if (message instanceof AgentCustomMetricSnapshotBatch) {
            final AgentCustomMetricSnapshotBatch agentCustomMetricSnapshotBatch = (AgentCustomMetricSnapshotBatch) message;
            final PCustomMetricMessage pCustomMetricMessage = customMetricMessageConverter.toMessage(agentCustomMetricSnapshotBatch);
            return pCustomMetricMessage;
        } else if (message instanceof AgentUriStatData) {
            final AgentUriStatData agentUriStatData = (AgentUriStatData) message;
            final PAgentUriStat agentUriStat = uriStatMessageConverter.toMessage(agentUriStatData);
            return agentUriStat;
        }
        return null;
    }
}