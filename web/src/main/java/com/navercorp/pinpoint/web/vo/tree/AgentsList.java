/*
 * Copyright 2018 NAVER Corp.
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

package com.navercorp.pinpoint.web.vo.tree;

import com.navercorp.pinpoint.web.vo.agent.AgentInfoSupplier;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HyunGil Jeong
 */
public class AgentsList<T extends AgentInfoSupplier> {

    private final String groupName;

    private final List<T> agentSuppliersList;

    public static <T extends AgentInfoSupplier> AgentsList<T> sort(String groupName, List<T> agentSuppliersList, SortBy sortBy) {
        Objects.requireNonNull(groupName, "groupName");
        Objects.requireNonNull(agentSuppliersList, "agentSuppliersList");
        Objects.requireNonNull(sortBy, "sortBy");

        List<T> list = Collections.unmodifiableList(sort(sortBy, agentSuppliersList));
        return new AgentsList<>(groupName, list);
    }

    private static <T extends AgentInfoSupplier> List<T> sort(SortBy sortBy, List<T> agentSuppliersList) {
        Comparator<AgentInfoSupplier> comparator = sortBy.getComparator();
        return agentSuppliersList.stream().sorted(comparator).collect(Collectors.toList());
    }

    public AgentsList(String groupName, List<T> agentSuppliersList) {
        this.groupName = Objects.requireNonNull(groupName, "groupName");
        this.agentSuppliersList = Objects.requireNonNull(agentSuppliersList, "agentSuppliersList");
    }

    public enum SortBy {
        AGENT_NAME_ASCENDING(
                Comparator.comparing(SortBy::getAgentNameFrom)
                        .thenComparing(SortBy::getAgentIdFrom)
        ),
        AGENT_NAME_DESCENDING(
                AGENT_NAME_ASCENDING.getComparator().reversed()
        ),
        AGENT_ID_ASCENDING(
                Comparator.comparing(SortBy::getAgentIdFrom)
                        .thenComparing(SortBy::getAgentNameFrom)
        ),
        AGENT_ID_DESCENDING(
                AGENT_ID_ASCENDING.getComparator().reversed()
        ),
        LAST_STARTED_TIME(
                Comparator.comparingLong(SortBy::getStartTimestampFrom)
                        .reversed()
                        .thenComparing(SortBy::getAgentIdFrom)
        );

        private final Comparator<AgentInfoSupplier> comparator;

        SortBy(Comparator<AgentInfoSupplier> comparator) {
            this.comparator = comparator;
        }

        public Comparator<AgentInfoSupplier> getComparator() {
            return comparator;
        }

        private static String getAgentIdFrom(AgentInfoSupplier agentInfoSupplier) {
            return agentInfoSupplier.getAgentInfo().getAgentId();
        }

        private static String getAgentNameFrom(AgentInfoSupplier agentInfoSupplier) {
            return agentInfoSupplier.getAgentInfo().getAgentName();
        }

        private static long getStartTimestampFrom(AgentInfoSupplier agentInfoSupplier) {
            return agentInfoSupplier.getAgentInfo().getStartTimestamp();
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public List<T> getAgentSuppliersList() {
        return agentSuppliersList;
    }

    @Override
    public String toString() {
        return "AgentsList{" +
                "groupName='" + groupName + '\'' +
                ", agentSuppliersList=" + agentSuppliersList +
                '}';
    }
}
