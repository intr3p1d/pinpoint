/*
 * Copyright 2020 NAVER Corp.
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

package com.navercorp.pinpoint.profiler.context.grpc;

import com.navercorp.pinpoint.common.profiler.message.MessageConverter;
import com.navercorp.pinpoint.grpc.trace.PCustomMetric;
import com.navercorp.pinpoint.grpc.trace.PCustomMetricMessage;
import com.navercorp.pinpoint.profiler.context.grpc.mapper.CustomMetricMapper;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.MetricType;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Taejin Koo
 */
public class GrpcCustomMetricMessageConverter implements MessageConverter<MetricType, PCustomMetricMessage> {

    private final CustomMetricMapper mapper = CustomMetricMapper.INSTANCE;

    @Override
    public PCustomMetricMessage toMessage(MetricType message) {
        Objects.requireNonNull(message, "message");

        if (message instanceof AgentCustomMetricSnapshotBatch) {
            AgentCustomMetricSnapshotBatch agentCustomMetricSnapshotBatch = (AgentCustomMetricSnapshotBatch) message;
            List<AgentCustomMetricSnapshot> agentCustomMetricSnapshotList = agentCustomMetricSnapshotBatch.getAgentCustomMetricSnapshotList();

            Set<String> metricNameSet = new HashSet<>();
            for (AgentCustomMetricSnapshot agentCustomMetricSnapshot : agentCustomMetricSnapshotList) {
                metricNameSet.addAll(agentCustomMetricSnapshot.getMetricNameSet());
            }

            PCustomMetricMessage.Builder builder = PCustomMetricMessage.newBuilder();
            for (AgentCustomMetricSnapshot agentCustomMetricSnapshot : agentCustomMetricSnapshotList) {
                builder.addTimestamp(agentCustomMetricSnapshot.getTimestamp());
                builder.addCollectInterval(agentCustomMetricSnapshot.getCollectInterval());
            }

            for (String metricName : metricNameSet) {
                PCustomMetric pCustomMetric = mapper.create(metricName, agentCustomMetricSnapshotList);
                if (pCustomMetric != null) {
                    builder.addCustomMetrics(pCustomMetric);
                }
            }

            return builder.build();
        } else {
            throw new IllegalArgumentException("Not supported Object. clazz:" + message.getClass());
        }
    }
}