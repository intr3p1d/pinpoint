package com.navercorp.pinpoint.profiler.context.grpc.mapper;


import com.navercorp.pinpoint.bootstrap.context.ServerMetaData;
import com.navercorp.pinpoint.bootstrap.context.ServiceInfo;
import com.navercorp.pinpoint.common.Version;
import com.navercorp.pinpoint.grpc.trace.PAgentInfo;
import com.navercorp.pinpoint.grpc.trace.PJvmInfo;
import com.navercorp.pinpoint.grpc.trace.PServerMetaData;
import com.navercorp.pinpoint.grpc.trace.PServiceInfo;
import com.navercorp.pinpoint.profiler.AgentInformation;
import com.navercorp.pinpoint.profiler.JvmInformation;
import com.navercorp.pinpoint.profiler.context.DefaultServerMetaData;
import com.navercorp.pinpoint.profiler.context.DefaultServiceInfo;
import com.navercorp.pinpoint.profiler.context.TestAgentInformation;
import com.navercorp.pinpoint.profiler.metadata.AgentInfo;
import com.navercorp.pinpoint.profiler.monitor.metric.gc.JvmGcType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author intr3p1d
 */
class AgentInfoMapperTest {

    AgentInfoMapper mapper = AgentInfoMapper.INSTANCE;

    @Test
    void testMapAgentInfo() {
        AgentInfo agentInfo = new AgentInfo(
                new TestAgentInformation(),
                new DefaultServerMetaData(
                        "serverInfo",
                        randomStringList(),
                        randomIntegerStringMap(),
                        randomServiceInfoList()
                ),
                new JvmInformation("1.0", JvmGcType.G1)
        );
        PAgentInfo pAgentInfo = mapper.map(agentInfo);


        AgentInformation agentInformation = agentInfo.getAgentInformation();

        assertEquals(agentInformation.getHostIp(), pAgentInfo.getIp());
        assertEquals(agentInformation.getMachineName(), pAgentInfo.getHostname());
        assertEquals("", pAgentInfo.getPorts());
        assertEquals(agentInformation.isContainer(), pAgentInfo.getContainer());
        assertEquals(agentInformation.getPid(), pAgentInfo.getPid());
        assertEquals(agentInformation.getServerType().getCode(), pAgentInfo.getServiceType());
        assertEquals(agentInformation.getJvmVersion(), pAgentInfo.getVmVersion());
        assertEquals(Version.VERSION, pAgentInfo.getAgentVersion());


        ServerMetaData serverMetaData = agentInfo.getServerMetaData();
        PServerMetaData pServerMetaData = pAgentInfo.getServerMetaData();

        assertEquals(serverMetaData.getServerInfo(), pServerMetaData.getServerInfo());
        assertEquals(serverMetaData.getVmArgs(), pServerMetaData.getVmArgList());

        List<ServiceInfo> serviceInfoList = serverMetaData.getServiceInfos();
        List<PServiceInfo> pServiceInfoList = pServerMetaData.getServiceInfoList();

        assertEquals(serviceInfoList.size(), pServiceInfoList.size());
        for (int i = 0; i < serviceInfoList.size(); i++) {
            assertEquals(serviceInfoList.get(i).getServiceName(), pServiceInfoList.get(i).getServiceName());
            assertEquals(serviceInfoList.get(i).getServiceLibs(), pServiceInfoList.get(i).getServiceLibList());
        }

        JvmInformation jvmInformation = agentInfo.getJvmInfo();
        PJvmInfo pJvmInfo = pAgentInfo.getJvmInfo();

        assertEquals(jvmInformation.getJvmVersion(), pJvmInfo.getVmVersion());
        assertEquals("JVM_GC_TYPE_" + jvmInformation.getJvmGcType().toString(), pJvmInfo.getGcType().toString());
    }

    Map<Integer, String> randomIntegerStringMap(){
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            map.put(i, randomString());
        }
        return map;
    }

    List<String> randomStringList() {
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add(randomString());
        }
        return strings;
    }

    String randomString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString();
    }

    List<ServiceInfo> randomServiceInfoList() {
        List<ServiceInfo> infos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            infos.add(randomServiceInfo());
        }
        return infos;
    }

    ServiceInfo randomServiceInfo(){
        return new DefaultServiceInfo(
                randomString(),
                randomStringList()
        );
    }
}