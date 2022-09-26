package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentsListMap {

    @JsonValue
    private final Map<String, AgentsList<AgentAndStatus>> stringToAgentsListMap;

    public AgentsListMap(Map<String, AgentsList<AgentAndStatus>> stringToAgentsListMap) {
        this.stringToAgentsListMap = Objects.requireNonNull(stringToAgentsListMap, "stringToAgentsListMap");
    }

    public List<AgentsList<AgentAndStatus>> getApplicationAgentLists() {
        return new ArrayList<>(stringToAgentsListMap.values());
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

        public void merge(AgentsListMap applicationAgentList) {
            for (AgentsList<AgentAndStatus> agentsList : applicationAgentList.getApplicationAgentLists()) {
                for (AgentAndStatus agent : agentsList.getAgentSuppliersList()) {
                    add(agent);
                }
            }
        }

        public AgentsListMap build() {
            if (list.isEmpty()) {
                return new AgentsListMap(Map.of());
            }
            return new AgentsListMap(groupByHostName(list));
        }

        private Map<String, AgentsList<AgentAndStatus>> groupByHostName(List<AgentAndStatus> agentList) {
            Map<String, AgentsList<AgentAndStatus>> containerAppList = containerList(agentList);
            Map<String, AgentsList<AgentAndStatus>> applicationGroup = nonContainerList(agentList);
            applicationGroup.putAll(containerAppList);  // FIXME -> merge?
            return applicationGroup;
        }

        private Map<String, AgentsList<AgentAndStatus>> containerList(List<AgentAndStatus> agentInfoList) {
            List<AgentAndStatus> filteredContainerList = filter(agentInfoList, agentInfo -> agentInfo.getAgentInfo().isContainer());
            if (filteredContainerList.isEmpty()) {
                return Collections.emptyMap();
            }

            Map<String, AgentsList<AgentAndStatus>> containerMap = new HashMap<>();
            containerMap.put(
                    CONTAINER,
                    new AgentsList<>(CONTAINER, filteredContainerList, AgentsList.SortBy.LAST_STARTED_TIME)
            );
            return containerMap;
        }

        private Map<String, AgentsList<AgentAndStatus>> nonContainerList(List<AgentAndStatus> agentAndStatusList) {
            List<AgentAndStatus> nonContainerList = filter(agentAndStatusList, agentAndStatus -> !agentAndStatus.getAgentInfo().isContainer());

            Function<AgentAndStatus, String> hostnameClassifier = (agentInfoSupplier -> agentInfoSupplier.getAgentInfo().getHostName());
            Collector<AgentAndStatus, ?, Map<String, List<AgentAndStatus>>> collector = Collectors.groupingBy(hostnameClassifier);
            Map<String, List<AgentAndStatus>> mapByHostname = nonContainerList.stream().collect(collector);

            return mapByHostname.entrySet().stream().collect(
                    Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new AgentsList<AgentAndStatus>(e.getKey(), e.getValue(), AgentsList.SortBy.AGENT_ID_ASCENDING)
                    )
            );
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
