package com.navercorp.pinpoint.web.vo;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalysisAgentsList {

    private final List<AgentsList<AgentStatusAndLink>> list;

    public AnalysisAgentsList(List<AgentsList<AgentStatusAndLink>> list) {
        this.list = Objects.requireNonNull(list, "list");
    }

    public List<AgentsList<AgentStatusAndLink>> getApplicationAgentLists() {
        return list;
    }

    public static AnalysisAgentsList.Builder newBuilder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
        return new AnalysisAgentsList.Builder(filter, hyperLinkFactory);
    }


    @Override
    public String toString() {
        return "AgentsLists{" +
                "list=" + list +
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

        public void merge(AgentsLists applicationAgentList) {
            for (AgentsList<AgentStatusAndLink> agentsList : applicationAgentList.getApplicationAgentLists()) {
                for (AgentStatusAndLink agent : agentsList.getAgentSuppliersList()) {
                    add(new AgentAndStatus(agent.getAgentInfo(), agent.getStatus()));
                }
            }
        }

        public AgentsLists build() {
            if (list.isEmpty()) {
                return new AgentsLists(List.of());
            }
            return new AgentsLists(groupByApplicationName(list));
        }

        private List<AgentsList<AgentStatusAndLink>> groupByApplicationName(List<AgentAndStatus> list) {
            return groupBy0(list, this::byApplicationName);
        }

        private String byApplicationName(AgentAndStatus agentAndStatus) {
            return agentAndStatus.getAgentInfo().getApplicationName();
        }

        private List<AgentsList<AgentStatusAndLink>> groupBy0(List<AgentAndStatus> list, Function<AgentAndStatus, String> groupBy) {
            Stream<AgentAndStatus> stream = openStream(list);
            Map<String, List<AgentAndStatus>> map = stream.collect(Collectors.groupingBy(groupBy));

            return toApplicationAgentList(map, AgentsList.SortBy.AGENT_ID_ASCENDING);
        }

        private Stream<AgentAndStatus> openStream(List<AgentAndStatus> list) {
            return list.stream().filter(filter::filter);
        }

        private List<AgentsList<AgentStatusAndLink>> toApplicationAgentList(Map<String, List<AgentAndStatus>> map, AgentsList.SortBy sortBy) {
            return map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(ele -> this.newApplicationAgentList(ele, sortBy))
                    .collect(Collectors.toList());
        }


        private AgentsList<AgentStatusAndLink> newApplicationAgentList(Map.Entry<String, List<AgentAndStatus>> entry, AgentsList.SortBy sortBy) {
            String key = entry.getKey();
            List<AgentStatusAndLink> informableAgents = entry.getValue()
                    .stream()
                    .map(this::newAgentInfoAndLink)
                    .collect(Collectors.toList());
            return new AgentsList<>(key, informableAgents, sortBy);
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
