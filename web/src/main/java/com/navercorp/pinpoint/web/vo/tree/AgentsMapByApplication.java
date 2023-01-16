package com.navercorp.pinpoint.web.vo.tree;

import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.DetailedAgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.DetailedAgentInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AgentsMapByApplication {

    private final InstancesListMap<DetailedAgentInfo> instancesListMap;

    private AgentsMapByApplication(InstancesListMap<DetailedAgentInfo> instancesListMap) {
        this.instancesListMap = Objects.requireNonNull(instancesListMap, "agentsListMap");
    }

    public List<InstancesList<DetailedAgentInfo>> getAgentsListsList() {
        return new ArrayList<>(instancesListMap.getListMap());
    }

    public static AgentsMapByApplication newAgentsMapByApplication(AgentInfoFilter filter,
                                                                   Collection<DetailedAgentAndStatus> agentCollection) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(agentCollection, "agentCollection");

        InstancesListMapBuilder<DetailedAgentAndStatus, DetailedAgentInfo> instancesListMapBuilder =
                new InstancesListMapBuilder<>(
                        AgentsMapByApplication::byApplicationName,
                        Comparator.naturalOrder(),
                        SortByAgentInfo.agentIdAsc(DetailedAgentInfo::getAgentInfo).getComparator(),
                        agentCollection
                );

        instancesListMapBuilder.withFilter((DetailedAgentAndStatus a) -> filter.filter(new AgentAndStatus(a.getDetailedAgentInfo().getAgentInfo(), a.getStatus())));
        return new AgentsMapByApplication(instancesListMapBuilder.build());
    }

    private static String byApplicationName(DetailedAgentInfo agentAndStatus) {
        return agentAndStatus.getAgentInfo().getApplicationName();
    }

    @Override
    public String toString() {
        return "AgentsMapByApplication{" +
                "instancesListMap=" + instancesListMap +
                '}';
    }
}