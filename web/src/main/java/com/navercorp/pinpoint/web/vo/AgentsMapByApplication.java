package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.hyperlink.HyperLink;
import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.hyperlink.LinkSources;
import com.navercorp.pinpoint.web.view.AgentsMapByApplicationSerializer;
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
import java.util.function.Function;

@JsonSerialize(using = AgentsMapByApplicationSerializer.class)
public class AgentsMapByApplication {

    private final AgentsListMap<AgentStatusAndLink> agentsListMap;

    private static final Function<AgentStatusAndLink, String> BY_APPLICATION_NAME = (AgentStatusAndLink a) -> a.getAgentInfo().getApplicationName();

    public AgentsMapByApplication(AgentsListMap<AgentStatusAndLink> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public List<AgentsList<AgentStatusAndLink>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap());
    }

    public static AgentsMapByApplication newAgentsMapByApplication(AgentInfoFilter filter,
                                                                   HyperLinkFactory hyperLinkFactory,
                                                                   Collection<AgentAndStatus> agentCollection) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(hyperLinkFactory, "hyperLinkFactory");
        Objects.requireNonNull(agentCollection, "agentCollection");
        AgentsListMapBuilder<AgentAndStatus, AgentStatusAndLink> agentsListMapBuilder = new AgentsListMapBuilder<>(filter::filter, (x) -> newAgentInfoAndLink(x, hyperLinkFactory));

        agentsListMapBuilder.withKeyExtractor(BY_APPLICATION_NAME)
                .withKeyComparator(Comparator.naturalOrder())
                .sortBy(AgentsList.SortBy.AGENT_ID_ASCENDING)
                .withCollection(agentCollection);
        return new AgentsMapByApplication(agentsListMapBuilder.build());
    }

    private static AgentStatusAndLink newAgentInfoAndLink(AgentAndStatus agentAndStatus, HyperLinkFactory hyperLinkFactory) {
        AgentInfo agentInfo = agentAndStatus.getAgentInfo();
        AgentStatus status = agentAndStatus.getStatus();
        List<HyperLink> hyperLinks = hyperLinkFactory.build(LinkSources.from(agentInfo));
        return new AgentStatusAndLink(agentInfo, status, hyperLinks);
    }

    @Override
    public String toString() {
        return "AgentsMapByApplication{" +
                "agentsListMap=" + agentsListMap +
                '}';
    }

}
