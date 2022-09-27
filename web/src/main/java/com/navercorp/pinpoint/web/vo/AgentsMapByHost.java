package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.navercorp.pinpoint.web.hyperlink.HyperLink;
import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.hyperlink.LinkSources;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.agent.AgentStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentsMapByHost {

    @JsonValue
    private final AgentsListMap<AgentStatusAndLink> agentsListMap;

    public AgentsMapByHost(AgentsListMap<AgentStatusAndLink> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public List<AgentsList<AgentStatusAndLink>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap().values());
    }

    public static Builder newBuilder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
        return new Builder(filter, hyperLinkFactory);
    }

    @Override
    public String toString() {
        return "AgentsMapByHost{" +
                "agentsListMap=" + agentsListMap +
                '}';
    }

    public static class Builder {
        public static final String CONTAINER = "Container";

        private final AgentInfoFilter filter;
        private final HyperLinkFactory hyperLinkFactory;
        private final List<AgentAndStatus> list = new ArrayList<>();

        private AgentsList.SortBy sortByOptional = null;

        Builder(AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
            this.filter = Objects.requireNonNull(filter, "filter");
            this.hyperLinkFactory = Objects.requireNonNull(hyperLinkFactory, "hyperLinkFactory");
        }

        public void add(AgentAndStatus agentInfo) {
            Objects.requireNonNull(agentInfo, "agentInfoAndStatus");
            this.list.add(agentInfo);
        }

        public void addAll(Collection<AgentAndStatus> agentInfoList) {
            Objects.requireNonNull(agentInfoList, "agentInfoList");
            for (AgentAndStatus agent : agentInfoList) {
                add(agent);
            }
        }

        public void sortBy(AgentsList.SortBy sortBy) {
            this.sortByOptional = sortBy;
        }

        public AgentsMapByHost build() {
            if (list.isEmpty()) {
                return new AgentsMapByHost(AgentsListMap.emptyMap());
            }
            return new AgentsMapByHost(groupByHost(list));
        }

        private AgentsListMap<AgentStatusAndLink> groupByHost(List<AgentAndStatus> agentList) {
            AgentsListMap<AgentStatusAndLink> containerListMap = containerList(agentList);
            AgentsListMap<AgentStatusAndLink> nonContainerListMap = nonContainerList(agentList);
            return AgentsListMap.merge(containerListMap, nonContainerListMap, containerGoesUp());
        }

        private AgentsListMap<AgentStatusAndLink> containerList(List<AgentAndStatus> agentInfoList) {
            List<AgentAndStatus> containers = filter(agentInfoList, agentInfo -> agentInfo.getAgentInfo().isContainer());
            List<AgentStatusAndLink> containerList = toAgentsStatusAndLinksList(containers.stream());
            Function<AgentStatusAndLink, String> alwaysContainer = (agentInfoSupplier -> CONTAINER);

            return AgentsListMap.newAgentsListMap(
                    containerList,
                    alwaysContainer,
                    containerGoesUp(),
                    Optional.ofNullable(sortByOptional).orElse(AgentsList.SortBy.LAST_STARTED_TIME)
            );
        }

        private AgentsListMap<AgentStatusAndLink> nonContainerList(List<AgentAndStatus> agentAndStatusList) {
            List<AgentAndStatus> nonContainers = filter(agentAndStatusList, agentAndStatus -> !agentAndStatus.getAgentInfo().isContainer());
            List<AgentStatusAndLink> nonContainerList = toAgentsStatusAndLinksList(nonContainers.stream());
            Function<AgentStatusAndLink, String> byHostname = (agentInfoSupplier -> agentInfoSupplier.getAgentInfo().getHostName());

            return AgentsListMap.newAgentsListMap(
                    nonContainerList,
                    byHostname,
                    containerGoesUp(),
                    Optional.ofNullable(sortByOptional).orElse(AgentsList.SortBy.AGENT_ID_ASCENDING)
            );
        }

        private List<AgentStatusAndLink> toAgentsStatusAndLinksList(Stream<AgentAndStatus> stream) {
            return stream.map(this::newAgentInfoAndLink).collect(Collectors.toList());
        }

        private AgentStatusAndLink newAgentInfoAndLink(AgentAndStatus agentAndStatus) {
            AgentInfo agentInfo = agentAndStatus.getAgentInfo();
            AgentStatus status = agentAndStatus.getStatus();
            List<HyperLink> hyperLinks = hyperLinkFactory.build(LinkSources.from(agentInfo));
            return new AgentStatusAndLink(agentInfo, status, hyperLinks);
        }

        private Comparator<String> containerGoesUp() {
            return Comparator.comparing((String s) -> !s.equals(CONTAINER))
                    .thenComparing(Comparator.naturalOrder());
        }

        private List<AgentAndStatus> filter(List<AgentAndStatus> agentList, Predicate<AgentAndStatus> filter) {
            return openFilteredStream(agentList)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        private Stream<AgentAndStatus> openFilteredStream(List<AgentAndStatus> list) {
            return list.stream().filter(filter::filter);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    ", filter=" + filter +
                    ", agentsMap=" + list +
                    ", sortByOptional=" + sortByOptional +
                    '}';
        }
    }
}
