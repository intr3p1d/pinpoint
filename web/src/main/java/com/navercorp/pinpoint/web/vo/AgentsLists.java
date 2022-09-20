/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.web.hyperlink.HyperLink;
import com.navercorp.pinpoint.web.hyperlink.HyperLinkFactory;
import com.navercorp.pinpoint.web.hyperlink.LinkSources;
import com.navercorp.pinpoint.web.view.AgentsListsSerializer;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.AgentStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author minwoo.jung
 * @author HyunGil Jeong
 */
@JsonSerialize(using = AgentsListsSerializer.class)
public class AgentsLists {
    public enum GroupBy {
        APPLICATION_NAME,
        HOST_NAME
    }


    private final List<AgentsList> list;

    public AgentsLists(List<AgentsList> list) {
        this.list = Objects.requireNonNull(list, "list");
    }

    public List<AgentsList> getApplicationAgentLists() {
        return list;
    }

    public static Builder newBuilder(GroupBy groupBy, AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
        return new Builder(groupBy, filter, hyperLinkFactory);
    }


    @Override
    public String toString() {
        return "AgentsLists{" +
                "list=" + list +
                '}';
    }


    public static class Builder {
        public static final String CONTAINER = "Container";

        private final GroupBy groupBy;
        private final AgentInfoFilter filter;
        private final HyperLinkFactory hyperLinkFactory;

        private final List<AgentAndStatus> list = new ArrayList<>();


        Builder(GroupBy groupBy, AgentInfoFilter filter, HyperLinkFactory hyperLinkFactory) {
            this.groupBy = Objects.requireNonNull(groupBy, "groupBy");
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
            for (AgentsList agentsList : applicationAgentList.getApplicationAgentLists()) {
                for (AgentStatusAndLink agent : agentsList.getAgentStatusAndLinks()) {
                    add(new AgentAndStatus(agent.getAgentInfo(), agent.getStatus()));
                }
            }
        }

        public AgentsLists build() {
            if (list.isEmpty()) {
                return new AgentsLists(List.of());
            }
            switch (groupBy) {
                case APPLICATION_NAME:
                    return new AgentsLists(groupByApplicationName(list));
                case HOST_NAME:
                    return new AgentsLists(groupByHostName(list));
                default:
                    throw new UnsupportedOperationException("Unknown groupBy :" + groupBy);
            }
        }

        private List<AgentsList> groupByApplicationName(List<AgentAndStatus> list) {
            return groupBy0(list, this::byApplicationName);
        }

        private String byApplicationName(AgentAndStatus agentAndStatus) {
            return agentAndStatus.getAgentInfo().getApplicationName();
        }

        private List<AgentsList> groupBy0(List<AgentAndStatus> list, Function<AgentAndStatus, String> groupBy) {
            Stream<AgentAndStatus> stream = openStream(list);
            // groupby
            Map<String, List<AgentAndStatus>> map = stream.collect(Collectors.groupingBy(groupBy));

            return toApplicationAgentList(map, AgentsList.SortBy.AGENT_ID_ASCENDING);
        }

        private Stream<AgentAndStatus> openStream(List<AgentAndStatus> list) {
            return list.stream().filter(filter::filter);
        }

        private List<AgentsList> groupByHostName(List<AgentAndStatus> agentList) {
            List<AgentAndStatus> filteredContainerList = filter(agentList, agentAndStatus -> agentAndStatus.getAgentInfo().isContainer());

            List<AgentStatusAndLink> containerList = filteredContainerList.stream()
                    .map(this::newAgentInfoAndLink)
                    .collect(Collectors.toList());
            AgentsList containerAppList = new AgentsList(CONTAINER, containerList, AgentsList.SortBy.LAST_STARTED_TIME);

            List<AgentAndStatus> nonContainerList = filter(agentList, agentAndStatus -> !agentAndStatus.getAgentInfo().isContainer());

            List<AgentsList> applicationGroup = groupBy0(nonContainerList, this::byHostName);

            if (containerAppList.getAgentStatusAndLinks().isEmpty()) {
                return applicationGroup;
            }
            return ListUtils.union(List.of(containerAppList), applicationGroup);
        }

        private String byHostName(AgentAndStatus agentAndStatus) {
            return agentAndStatus.getAgentInfo().getHostName();
        }

        private List<AgentAndStatus> filter(List<AgentAndStatus> agentList, Predicate<AgentAndStatus> filter) {
            return openStream(agentList)
                    .filter(filter)
                    .collect(Collectors.toList());
        }

        private List<AgentsList> toApplicationAgentList(Map<String, List<AgentAndStatus>> map, AgentsList.SortBy sortBy) {
            return map.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(ele -> this.newApplicationAgentList(ele, sortBy))
                    .collect(Collectors.toList());
        }


        private AgentsList newApplicationAgentList(Map.Entry<String, List<AgentAndStatus>> entry, AgentsList.SortBy sortBy) {
            String key = entry.getKey();
            List<AgentStatusAndLink> list = entry.getValue()
                    .stream()
                    .map(this::newAgentInfoAndLink)
                    .collect(Collectors.toList());
            return new AgentsList(key, list, sortBy);
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
                    "groupBy=" + groupBy +
                    ", filter=" + filter +
                    ", hyperLinkFactory=" + hyperLinkFactory +
                    ", agentsMap=" + list +
                    '}';
        }
    }

}
