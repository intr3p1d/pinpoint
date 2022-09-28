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
import java.util.stream.Collectors;

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
        private static final Function<AgentAndStatus, String> CONTAINER_AND_PHYSICAL = (AgentAndStatus a) -> {
            if (a.getAgentInfo().isContainer()) {
                return CONTAINER;
            }
            return a.getAgentInfo().getHostName();
        };

        private final AgentInfoFilter filter;
        private final List<AgentAndStatus> list = new ArrayList<>();

        private final AgentsListMapBuilder<AgentAndStatus, AgentAndStatus> agentsListMapBuilder;

        private AgentsList.SortBy sortByOptional = null;

        Builder(AgentInfoFilter filter) {
            this.filter = Objects.requireNonNull(filter, "filter");

            this.agentsListMapBuilder = new AgentsListMapBuilder<>(this.filter::filter, (x) -> x);
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
            agentsListMapBuilder.withKeyExtractor(CONTAINER_AND_PHYSICAL)
                    .withKeyComparator(CONTAINER_GOES_UP)
                    .sortBy(Optional.ofNullable(sortByOptional).orElse(AgentsList.SortBy.AGENT_ID_ASCENDING))
                    .withCollection(list);
            return new AgentsMapByHost(agentsListMapBuilder.build());
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
