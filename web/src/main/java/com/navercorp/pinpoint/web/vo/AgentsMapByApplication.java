package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.annotation.JsonValue;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentsMapByApplication {

    @JsonValue
    private final AgentsListMap<AgentStatusAndLink> maplist;

    public AgentsMapByApplication(AgentsListMap<AgentStatusAndLink> maplist) {
        this.maplist = Objects.requireNonNull(maplist, "maplist");
    }

    public List<AgentsList<AgentStatusAndLink>> getApplicationAgentLists() {
        return new ArrayList<>(maplist.getListmap().values());
    }

    public static AgentsMapByApplication.Builder newBuilder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
        return new AgentsMapByApplication.Builder(filter, hyperLinkFactory);
    }


    @Override
    public String toString() {
        return "AgentsLists{" +
                "list=" + maplist +
                '}';
    }


    public static class Builder {
        private final AgentInfoFilter filter;
        private final HyperLinkFactory hyperLinkFactory;

        private final List<AgentAndStatus> list = new ArrayList<>();


        Builder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
            this.filter = Objects.requireNonNull(filter, "filter");
            this.hyperLinkFactory = Objects.requireNonNull(hyperLinkFactory, "hyperLinkFactory");
        }

        public void add(AgentAndStatus agentInfoAndStatus) {
            Objects.requireNonNull(agentInfoAndStatus, "agentInfoAndStatus");
            this.list.add(agentInfoAndStatus);
        }

        public void addAll(Collection<AgentAndStatus> agentInfoAndStatusList) {
            Objects.requireNonNull(agentInfoAndStatusList, "agentInfoAndStatusList");
            for (AgentAndStatus agent : agentInfoAndStatusList) {
                Objects.requireNonNull(agent, "agent");
                add(agent);
            }
        }

        public void merge(AgentsMapByApplication applicationAgentList) {
            for (AgentsList<AgentStatusAndLink> agentsList : applicationAgentList.getApplicationAgentLists()) {
                for (AgentStatusAndLink agent : agentsList.getAgentSuppliersList()) {
                    add(new AgentAndStatus(agent.getAgentInfo(), agent.getStatus()));
                }
            }
        }

        public AgentsMapByApplication build() {
            if (list.isEmpty()) {
                return new AgentsMapByApplication(AgentsListMap.emptyMap());
            }
            return new AgentsMapByApplication(groupByApplicationName(list));
        }

        private AgentsListMap<AgentStatusAndLink> groupByApplicationName(List<AgentAndStatus> list) {
            Stream<AgentAndStatus> stream = openStream(list);
            List<AgentStatusAndLink> agentStatusAndLinks = stream.map(this::newAgentInfoAndLink).collect(Collectors.toList());

            return AgentsListMap.newAgentsListMap(agentStatusAndLinks, this::byApplicationName, Comparator.naturalOrder(), AgentsList.SortBy.AGENT_ID_ASCENDING);
        }

        private String byApplicationName(AgentStatusAndLink agentStatusAndLink) {
            return agentStatusAndLink.getAgentInfo().getApplicationName();
        }

        private Stream<AgentAndStatus> openStream(List<AgentAndStatus> list) {
            return list.stream().filter(filter::filter);
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
                    ", agentsMap=" + list +
                    '}';
        }
    }


}
