package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.server.util.AgentLifeCycleState;
import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.AgentStatus;
import com.navercorp.pinpoint.web.vo.agent.DetailedAgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.DetailedAgentInfo;
import com.navercorp.pinpoint.web.vo.tree.AgentsList;
import com.navercorp.pinpoint.web.vo.tree.AgentsMapByApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AgentsMapByApplicationTest {

    private final HyperLinkFactory hyperLinkFactory = HyperLinkFactory.empty();

    @Test
    public void groupByApplicationName() {
        DetailedAgentAndStatus app1Agent1 = createAgentInfo("APP_1", "app1-agent1", "Host11", true);
        DetailedAgentAndStatus app1Agent2 = createAgentInfo("APP_1", "app1-agent2", "Host12", false);
        DetailedAgentAndStatus app2Agent1 = createAgentInfo("APP_2", "app2-agent1", "Host21", false);
        DetailedAgentAndStatus app2Agent2 = createAgentInfo("APP_2", "app2-agent2", "Host22", true);
        List<DetailedAgentAndStatus> agentAndStatusList = shuffleAgentInfos(app1Agent1, app1Agent2, app2Agent1, app2Agent2);

        AgentsMapByApplication agentsMapByApplication = AgentsMapByApplication.newAgentsMapByApplication(AgentInfoFilter::accept, hyperLinkFactory, agentAndStatusList);
        List<AgentsList<DetailedAgentAndStatus>> agentsLists = agentsMapByApplication.getAgentsListsList();

        Assertions.assertEquals(2, agentsLists.size());

        AgentsList<DetailedAgentAndStatus> app1AgentsList = agentsLists.get(0);
        Assertions.assertEquals("APP_1", app1AgentsList.getGroupName());
        List<DetailedAgentAndStatus> app1AgentInfos = app1AgentsList.getAgentSuppliersList();
        Assertions.assertEquals(2, app1AgentInfos.size());
        Assertions.assertEquals(app1Agent1.getAgentInfo(), app1AgentInfos.get(0).getAgentInfo());
        Assertions.assertEquals(app1Agent2.getAgentInfo(), app1AgentInfos.get(1).getAgentInfo());

        AgentsList<DetailedAgentAndStatus> app2AgentsList = agentsLists.get(1);
        Assertions.assertEquals("APP_2", app2AgentsList.getGroupName());
        List<DetailedAgentAndStatus> app2AgentInfos = app2AgentsList.getAgentSuppliersList();
        Assertions.assertEquals(2, app2AgentInfos.size());
        Assertions.assertEquals(app2Agent1.getAgentInfo(), app2AgentInfos.get(0).getAgentInfo());
        Assertions.assertEquals(app2Agent2.getAgentInfo(), app2AgentInfos.get(1).getAgentInfo());
    }

    private static List<DetailedAgentAndStatus> shuffleAgentInfos(DetailedAgentAndStatus... agentInfos) {
        List<DetailedAgentAndStatus> agentInfoList = Arrays.asList(agentInfos);
        Collections.shuffle(agentInfoList);
        return agentInfoList;
    }

    private static DetailedAgentAndStatus createAgentInfo(String applicationName, String agentId, String hostname, boolean container) {
        return createAgentInfo(applicationName, agentId, hostname, container, System.currentTimeMillis());
    }

    private static DetailedAgentAndStatus createAgentInfo(String applicationName, String agentId, String hostname, boolean container, long startTimestamp) {
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setApplicationName(applicationName);
        agentInfo.setAgentId(agentId);
        agentInfo.setHostName(hostname);
        agentInfo.setContainer(container);
        agentInfo.setStartTimestamp(startTimestamp);

        AgentStatus agentStatus = new AgentStatus(agentId, AgentLifeCycleState.RUNNING, startTimestamp);
        return new DetailedAgentAndStatus(new DetailedAgentInfo(agentInfo, null, null), agentStatus);
    }
}
