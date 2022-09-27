package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AgentsMapByHostTest {

    private final HyperLinkFactory hyperLinkFactory = HyperLinkFactory.empty();

    @Test
    public void groupByHostNameShouldHaveContainersFirstAndGroupedSeparatelyByAgentStartTimestampDescendingOrder() {
        AgentsMapByHost.Builder builder = AgentsMapByHost.newBuilder(AgentInfoFilter::accept, hyperLinkFactory);
        AgentAndStatus host1Agent1 = createAgentInfo("APP_1", "host1-agent1", "Host1", false);
        AgentAndStatus host2Agent1 = createAgentInfo("APP_1", "host2-agent1", "Host2", false);
        AgentAndStatus containerAgent1 = createAgentInfo("APP_1", "container-agent1", "Host3", true, 1);
        AgentAndStatus containerAgent2 = createAgentInfo("APP_1", "container-agent2", "Host4", true, 2);
        builder.addAll(shuffleAgentInfos(containerAgent1, host1Agent1, host2Agent1, containerAgent2));

        List<AgentsList<AgentStatusAndLink>> agentsLists = builder.build().getAgentsListsList();

        Assertions.assertEquals(3, agentsLists.size());

        AgentsList<AgentStatusAndLink> containerAgentsList = agentsLists.get(0);
        Assertions.assertEquals(AgentsMapByHost.Builder.CONTAINER, containerAgentsList.getGroupName());
        List<AgentStatusAndLink> containerAgents = containerAgentsList.getAgentSuppliersList();
        Assertions.assertEquals(2, containerAgents.size());
        Assertions.assertEquals(containerAgent2.getAgentInfo(), containerAgents.get(0).getAgentInfo());
        Assertions.assertEquals(containerAgent1.getAgentInfo(), containerAgents.get(1).getAgentInfo());

        AgentsList<AgentStatusAndLink> host1AgentsList = agentsLists.get(1);
        Assertions.assertEquals("Host1", host1AgentsList.getGroupName());
        List<AgentStatusAndLink> host1Agents = host1AgentsList.getAgentSuppliersList();
        Assertions.assertEquals(1, host1Agents.size());
        Assertions.assertEquals(host1Agent1.getAgentInfo(), host1Agents.get(0).getAgentInfo());

        AgentsList<AgentStatusAndLink> host2AgentsList = agentsLists.get(2);
        Assertions.assertEquals("Host2", host2AgentsList.getGroupName());
        List<AgentStatusAndLink> host2Agents = host2AgentsList.getAgentSuppliersList();
        Assertions.assertEquals(1, host2Agents.size());
        Assertions.assertEquals(host2Agent1.getAgentInfo(), host2Agents.get(0).getAgentInfo());
    }

    private static List<AgentAndStatus> shuffleAgentInfos(AgentAndStatus... agentInfos) {
        List<AgentAndStatus> agentInfoList = Arrays.asList(agentInfos);
        Collections.shuffle(agentInfoList);
        return agentInfoList;
    }

    private static AgentAndStatus createAgentInfo(String applicationName, String agentId, String hostname, boolean container) {
        return createAgentInfo(applicationName, agentId, hostname, container, System.currentTimeMillis());
    }

    private static AgentAndStatus createAgentInfo(String applicationName, String agentId, String hostname, boolean container, long startTimestamp) {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setApplicationName(applicationName);
        agentInfo.setAgentId(agentId);
        agentInfo.setHostName(hostname);
        agentInfo.setContainer(container);
        agentInfo.setStartTimestamp(startTimestamp);
        return new AgentAndStatus(agentInfo);
    }
}
