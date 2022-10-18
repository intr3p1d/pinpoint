package com.navercorp.pinpoint.web.vo.tree;

import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.SimpleAgentInfo;
import com.navercorp.pinpoint.web.vo.agent.SimpleAgentInfoWithVersion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SimpleAgentsMapByApplication {
    private final AgentsListMap<SimpleAgentInfoWithVersion> agentsListMap;

    private SimpleAgentsMapByApplication(AgentsListMap<SimpleAgentInfoWithVersion> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public List<AgentsList<SimpleAgentInfoWithVersion>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap());
    }

    public static SimpleAgentsMapByApplication newAgentsMapByApplication(AgentInfoFilter filter,
                                                                         Collection<SimpleAgentInfoWithVersion> agentCollection) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(agentCollection, "agentCollection");

        Comparator<SimpleAgentInfoWithVersion> AGENT_ID_ASC = Comparator.comparing(SimpleAgentInfoWithVersion::getAgentId);

        AgentsListMapBuilder<SimpleAgentInfoWithVersion, SimpleAgentInfoWithVersion> agentsListMapBuilder =
                new AgentsListMapBuilder<SimpleAgentInfoWithVersion, SimpleAgentInfoWithVersion>(
                        SimpleAgentsMapByApplication::byApplicationName,
                        Comparator.naturalOrder(),
                        new SortBy(AGENT_ID_ASC),
                        agentCollection
                );
        return new SimpleAgentsMapByApplication(agentsListMapBuilder.build());
    }

    private static String byApplicationName(SimpleAgentInfoWithVersion agentStatusAndLink) {
        return agentStatusAndLink.getApplicationName();
    }
}
