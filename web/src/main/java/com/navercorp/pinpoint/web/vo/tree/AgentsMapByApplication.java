package com.navercorp.pinpoint.web.vo.tree;

import com.navercorp.pinpoint.web.hyperlink.HyperLink;
import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.hyperlink.LinkSources;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.AgentStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class AgentsMapByApplication<T> {

    private final InstancesListMap<T> instancesListMap;

    private AgentsMapByApplication(InstancesListMap<T> instancesListMap) {
        this.instancesListMap = Objects.requireNonNull(instancesListMap, "agentsListMap");
    }

    public List<InstancesList<T>> getAgentsListsList() {
        return new ArrayList<>(instancesListMap.getListMap());
    }

    public static AgentsMapByApplication<AgentStatusAndLink> newAgentsMapWithStatusAndLink(AgentInfoFilter filter,
                                                                                           HyperLinkFactory hyperLinkFactory,
                                                                                           Collection<AgentAndStatus> agentCollection) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(hyperLinkFactory, "hyperLinkFactory");
        Objects.requireNonNull(agentCollection, "agentCollection");

        InstancesListMapBuilder<AgentAndStatus, AgentStatusAndLink> instancesListMapBuilder =
                new InstancesListMapBuilder<>(
                        AgentsMapByApplication::byApplicationName,
                        Comparator.naturalOrder(),
                        SortByAgentInfo.agentIdAsc(AgentStatusAndLink::getAgentInfo).getComparator(),
                        agentCollection
                );

        instancesListMapBuilder.withFilter(filter::filter)
                .withFinisher(x -> newAgentStatusAndLink(x, hyperLinkFactory));
        return new AgentsMapByApplication<>(instancesListMapBuilder.build());
    }

    private static String byApplicationName(AgentStatusAndLink agentStatusAndLink) {
        return agentStatusAndLink.getAgentInfo().getApplicationName();
    }

    private static AgentStatusAndLink newAgentStatusAndLink(AgentAndStatus agentAndStatus, HyperLinkFactory hyperLinkFactory) {
        AgentInfo agentInfo = agentAndStatus.getAgentInfo();
        AgentStatus status = agentAndStatus.getStatus();
        List<HyperLink> hyperLinks = hyperLinkFactory.build(LinkSources.from(agentInfo));
        return new AgentStatusAndLink(agentInfo, status, hyperLinks);
    }

    public static AgentsMapByApplication<AgentInfo> newAgentsMap(Collection<AgentInfo> agentInfos) {
        Objects.requireNonNull(agentInfos, "agentInfos");
        InstancesListMapBuilder<AgentInfo, AgentInfo> instancesListMapBuilder =
                new InstancesListMapBuilder<>(
                        AgentInfo::getApplicationName,
                        Comparator.naturalOrder(),
                        SortByAgentInfo.AGENT_ID_ASC,
                        agentInfos
                );
        return new AgentsMapByApplication<>(instancesListMapBuilder.build());
    }


    @Override
    public String toString() {
        return "AgentsMapByApplication{" +
                "instancesListMap=" + instancesListMap +
                '}';
    }
}
