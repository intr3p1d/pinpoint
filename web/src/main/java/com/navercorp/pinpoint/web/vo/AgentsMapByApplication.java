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

    public AgentsMapByApplication(AgentsListMap<AgentStatusAndLink> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public List<AgentsList<AgentStatusAndLink>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap());
    }

    public static AgentsMapByApplication.Builder newBuilder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
        return new AgentsMapByApplication.Builder(filter, hyperLinkFactory);
    }

    @Override
    public String toString() {
        return "AgentsMapByApplication{" +
                "agentsListMap=" + agentsListMap +
                '}';
    }

    public static class Builder {
        private static final Function<AgentStatusAndLink, String> BY_APPLICATION_NAME = (AgentStatusAndLink a) -> a.getAgentInfo().getApplicationName();

        private final AgentInfoFilter filter;
        private final HyperLinkFactory hyperLinkFactory;
        private final List<AgentAndStatus> list = new ArrayList<>();


        private final AgentsListMapBuilder<AgentAndStatus, AgentStatusAndLink> agentsListMapBuilder;


        Builder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
            this.filter = Objects.requireNonNull(filter, "filter");
            this.hyperLinkFactory = Objects.requireNonNull(hyperLinkFactory, "hyperLinkFactory");

            this.agentsListMapBuilder = new AgentsListMapBuilder<>(this.filter::filter, this::newAgentInfoAndLink);
        }

        public void add(AgentAndStatus agentInfoAndStatus) {
            Objects.requireNonNull(agentInfoAndStatus, "agentInfoAndStatus");
            this.list.add(agentInfoAndStatus);
        }

        public void addAll(Collection<AgentAndStatus> agentCollection) {
            Objects.requireNonNull(agentCollection, "agentCollection");
            for (AgentAndStatus agent : agentCollection) {
                add(agent);
            }
        }

        public AgentsMapByApplication build() {
            agentsListMapBuilder.withKeyExtractor(BY_APPLICATION_NAME)
                    .withKeyComparator(Comparator.naturalOrder())
                    .sortBy(AgentsList.SortBy.AGENT_ID_ASCENDING)
                    .withCollection(list);
            return new AgentsMapByApplication(agentsListMapBuilder.build());
        }

        private AgentStatusAndLink newAgentInfoAndLink(AgentAndStatus agentAndStatus) {
            AgentInfo agentInfo = agentAndStatus.getAgentInfo();
            AgentStatus status = agentAndStatus.getStatus();
            List<HyperLink> hyperLinks = hyperLinkFactory.build(LinkSources.from(agentInfo));
            return new AgentStatusAndLink(agentInfo, status, hyperLinks);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    ", filter=" + filter +
                    ", hyperLinkFactory=" + hyperLinkFactory +
                    ", agentsMap=" + agentsListMapBuilder +
                    '}';
        }
    }
}
