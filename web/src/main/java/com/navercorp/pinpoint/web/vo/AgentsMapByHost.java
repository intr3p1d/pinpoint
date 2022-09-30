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
import java.util.function.Function;

@JsonSerialize(using = AgentsMapByHostSerializer.class)
public class AgentsMapByHost {
    private final AgentsListMap<AgentAndStatus> agentsListMap;

    public static final String CONTAINER = "Container";
    private static final Comparator<String> CONTAINER_GOES_UP = Comparator.comparing((String s) -> !s.equals(CONTAINER))
            .thenComparing(Comparator.naturalOrder());
    private static final Function<AgentAndStatus, String> CONTAINER_AND_PHYSICAL = (AgentAndStatus a) -> {
        if (a.getAgentInfo().isContainer()) {
            return CONTAINER;
        }
        return a.getAgentInfo().getHostName();
    };

    public AgentsMapByHost(AgentsListMap<AgentAndStatus> agentsListMap) {
        this.agentsListMap = Objects.requireNonNull(agentsListMap, "agentsListMap");
    }

    public static AgentsMapByHost newAgentsMapByHost(AgentInfoFilter filter,
                                                     AgentsList.SortBy sortBy,
                                                     Collection<AgentAndStatus> agentCollection) {
        Objects.requireNonNull(filter, "filter");
        Objects.requireNonNull(sortBy, "sortBy");
        Objects.requireNonNull(agentCollection, "agentCollection");
        AgentsListMapBuilder<AgentAndStatus, AgentAndStatus> agentsListMapBuilder = new AgentsListMapBuilder<>(filter::filter, (x) -> x);

        agentsListMapBuilder.withKeyExtractor(CONTAINER_AND_PHYSICAL)
                .withKeyComparator(CONTAINER_GOES_UP)
                .sortBy(sortBy)
                .withCollection(agentCollection);
        return new AgentsMapByHost(agentsListMapBuilder.build());

    }

    public List<AgentsList<AgentAndStatus>> getAgentsListsList() {
        return new ArrayList<>(agentsListMap.getListMap());
    }

    @Override
    public String toString() {
        return "AgentsMapByHost{" +
                "agentsListMap=" + agentsListMap +
                '}';
    }
}
