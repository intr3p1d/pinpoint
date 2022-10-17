package com.navercorp.pinpoint.web.vo.tree;

import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.DetailedAgentAndStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AgentsMapByApplication {

    private final AgentsListMap<DetailedAgentAndStatus> agentsListMap;

    private AgentsMapByApplication(AgentsListMap<DetailedAgentAndStatus> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public List<AgentsList<DetailedAgentAndStatus>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap());
    }

    public static AgentsMapByApplication newAgentsMapByApplication(AgentInfoFilter filter,
                                                                   HyperLinkFactory hyperLinkFactory,
                                                                   Collection<DetailedAgentAndStatus> agentCollection) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(hyperLinkFactory, "hyperLinkFactory");
        Objects.requireNonNull(agentCollection, "agentCollection");

        AgentsListMapBuilder<DetailedAgentAndStatus, DetailedAgentAndStatus> agentsListMapBuilder =
                new AgentsListMapBuilder<>(
                        AgentsMapByApplication::byApplicationName,
                        Comparator.naturalOrder(),
                        SortBy.agentIdAsc(DetailedAgentAndStatus::getAgentInfo),
                        agentCollection
                );

        agentsListMapBuilder.withFilter(x -> filter.filter(new AgentAndStatus(x.getAgentInfo(), x.getStatus())));
        return new AgentsMapByApplication(agentsListMapBuilder.build());
    }

    private static String byApplicationName(DetailedAgentAndStatus DetailedAgentAndStatus) {
        return DetailedAgentAndStatus.getAgentInfo().getApplicationName();
    }


    @Override
    public String toString() {
        return "AgentsMapByApplication{" +
                "agentsListMap=" + agentsListMap +
                '}';
    }

}
