package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InspectorAgentsListMap {

    @JsonValue
    private final AgentsListMap<AgentAndStatus> maplist;

    public InspectorAgentsListMap(AgentsListMap<AgentAndStatus> maplist) {
        this.maplist = Objects.requireNonNull(maplist, "maplist");
    }

    public List<AgentsList<AgentAndStatus>> getApplicationAgentLists() {
        return new ArrayList<>(maplist.getListmap().values());
    }

    public static Builder newBuilder(AgentInfoFilter filter) {
        return new Builder(filter);
    }

    public static class Builder {
        public static final String CONTAINER = "Container";

        private final AgentInfoFilter filter;
        private final List<AgentAndStatus> list = new ArrayList<>();

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
                Objects.requireNonNull(agent, "agent");
                add(agent);
            }
        }

        public void merge(InspectorAgentsListMap applicationAgentList) {
            for (AgentsList<AgentAndStatus> agentsList : applicationAgentList.getApplicationAgentLists()) {
                for (AgentAndStatus agent : agentsList.getAgentSuppliersList()) {
                    add(agent);
                }
            }
        }

        public InspectorAgentsListMap build() {
            if (list.isEmpty()) {
                return new InspectorAgentsListMap(AgentsListMap.emptyMap());
            }
            return new InspectorAgentsListMap(groupByHostName(list));
        }

        private AgentsListMap<AgentAndStatus> groupByHostName(List<AgentAndStatus> agentList) {

            AgentsListMap<AgentAndStatus> containerAppList = containerList(agentList);
            AgentsListMap<AgentAndStatus> applicationGroup = nonContainerList(agentList);
            applicationGroup.putAll(containerAppList);  // FIXME -> merge?
            return applicationGroup;
        }

        private AgentsListMap<AgentAndStatus> containerList(List<AgentAndStatus> agentInfoList) {
            List<AgentAndStatus> filteredContainerList = filter(agentInfoList, agentInfo -> agentInfo.getAgentInfo().isContainer());
            if (filteredContainerList.isEmpty()) {
                return AgentsListMap.emptyMap();
            }

            return AgentsListMap.<AgentAndStatus>newAgentsListMap(
                    filteredContainerList,
                    ele -> CONTAINER,
                    containerGoesUp(),
                    AgentsList.SortBy.LAST_STARTED_TIME
            );
        }

        private AgentsListMap<AgentAndStatus> nonContainerList(List<AgentAndStatus> agentAndStatusList) {
            List<AgentAndStatus> nonContainerList = filter(agentAndStatusList, agentAndStatus -> !agentAndStatus.getAgentInfo().isContainer());
            Function<AgentAndStatus, String> hostnameClassifier = (agentInfoSupplier -> agentInfoSupplier.getAgentInfo().getHostName());

            return AgentsListMap.newAgentsListMap(nonContainerList, hostnameClassifier, containerGoesUp(), AgentsList.SortBy.AGENT_ID_ASCENDING);
        }

        private Comparator<String> containerGoesUp() {
            return Comparator.comparing((String s) -> !s.equals(CONTAINER))
                    .thenComparing(Comparator.naturalOrder());
        }

        private List<AgentAndStatus> filter(List<AgentAndStatus> agentList, Predicate<AgentAndStatus> filter) {
            return openStream(agentList)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        private Stream<AgentAndStatus> openStream(List<AgentAndStatus> list) {
            return list.stream().filter(filter::filter);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    ", filter=" + filter +
                    ", agentsMap=" + list +
                    '}';
        }
    }
}
