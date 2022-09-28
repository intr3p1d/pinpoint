package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.view.AgentsMapByHostSerializer;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
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

@JsonSerialize(using = AgentsMapByHostSerializer.class)
public class AgentsMapByHost {

    private final AgentsListMap<AgentAndStatus> agentsListMap;

    public AgentsMapByHost(AgentsListMap<AgentAndStatus> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public List<AgentsList<AgentAndStatus>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap());
    }

    public static Builder newBuilder(AgentInfoFilter filter) {
        return new Builder(filter);
    }

    @Override
    public String toString() {
        return "AgentsMapByHost{" +
                "agentsListMap=" + agentsListMap +
                '}';
    }

    public static class Builder {
        public static final String CONTAINER = "Container";
        private static final Comparator<String> CONTAINER_GOES_UP = Comparator.comparing((String s) -> !s.equals(CONTAINER))
                .thenComparing(Comparator.naturalOrder());

        private final AgentInfoFilter filter;
        private final List<AgentAndStatus> list = new ArrayList<>();

        private AgentsList.SortBy sortByOptional = null;

        Builder(AgentInfoFilter filter) {
            this.filter = Objects.requireNonNull(filter, "filter");
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

        private AgentsListMap<AgentAndStatus> groupByHost(List<AgentAndStatus> agentList) {
            AgentsListMap<AgentAndStatus> containerListMap = containerList(agentList);
            AgentsListMap<AgentAndStatus> nonContainerListMap = nonContainerList(agentList);
            return AgentsListMap.concat(containerListMap, nonContainerListMap);
        }

        private AgentsListMap<AgentAndStatus> containerList(List<AgentAndStatus> agentInfoList) {
            List<AgentAndStatus> containerList = filter(agentInfoList, agentInfo -> agentInfo.getAgentInfo().isContainer());
            Function<AgentAndStatus, String> alwaysContainer = (agentInfoSupplier -> CONTAINER);

            return AgentsListMap.newAgentsListMap(
                    containerList,
                    alwaysContainer,
                    CONTAINER_GOES_UP,
                    Optional.ofNullable(sortByOptional).orElse(AgentsList.SortBy.LAST_STARTED_TIME)
            );
        }

        private AgentsListMap<AgentAndStatus> nonContainerList(List<AgentAndStatus> agentAndStatusList) {
            List<AgentAndStatus> nonContainerList = filter(agentAndStatusList, agentAndStatus -> !agentAndStatus.getAgentInfo().isContainer());
            Function<AgentAndStatus, String> byHostname = (agentInfoSupplier -> agentInfoSupplier.getAgentInfo().getHostName());

            return AgentsListMap.newAgentsListMap(
                    nonContainerList,
                    byHostname,
                    CONTAINER_GOES_UP,
                    Optional.ofNullable(sortByOptional).orElse(AgentsList.SortBy.AGENT_ID_ASCENDING)
            );
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
