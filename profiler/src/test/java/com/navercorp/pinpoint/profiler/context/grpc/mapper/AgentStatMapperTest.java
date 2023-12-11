package com.navercorp.pinpoint.profiler.context.grpc.mapper;


import com.navercorp.pinpoint.grpc.trace.PAgentStat;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentStatMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.JvmGcMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.gc.JvmGcType;

import java.util.Collection;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author intr3p1d
 */
class AgentStatMapperTest {

    AgentStatMapper mapper = AgentStatMapper.INSTANCE;
    void testAgentStat() {
        AgentStatMetricSnapshot agentStatMetricSnapshot = new AgentStatMetricSnapshot();
        AgentStatMetricSnapshot agentStat = new AgentStatMetricSnapshot();
        agentStat.setAgentId("agentId");
        agentStat.setStartTimestamp(new Random().nextInt());
        //agentStat.setGc(jvmGcMetricCollector.collect());
        //agentStat.setCpuLoad(cpuLoadMetricCollector.collect());
        //agentStat.setTransaction(transactionMetricCollector.collect());
        //agentStat.setActiveTrace(activeTraceMetricCollector.collect());
        //agentStat.setDataSourceList(dataSourceMetricCollector.collect());
        //agentStat.setResponseTime(responseTimeMetricCollector.collect());
        //agentStat.setDeadlock(deadlockMetricCollector.collect());
        //agentStat.setFileDescriptor(fileDescriptorMetricCollector.collect());
        //agentStat.setDirectBuffer(bufferMetricCollector.collect());
        //agentStat.setTotalThread(totalThreadMetricCollector.collect());
        //agentStat.setLoadedClassCount(loadedClassMetricCollector.collect());

        PAgentStat pAgentStat = mapper.map(agentStat);
    }


    void testJvmGcMetric() {
        JvmGcMetricSnapshot snapshot = new JvmGcMetricSnapshot();
        snapshot.setType(JvmGcType.G1);
        snapshot.set
    }


}