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

package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.web.vo.agent.InformableAgent;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HyunGil Jeong
 */
public class AgentsList<T extends InformableAgent> {

    private final String groupName;
    private final List<T> informableAgents;

    public AgentsList(String groupName, List<T> informableAgents, SortBy sortBy) {
        this.groupName = Objects.requireNonNull(groupName, "groupName");
        Objects.requireNonNull(sortBy, "sortBy");
        Objects.requireNonNull(informableAgents, "informableAgents");
        this.informableAgents = sort(sortBy, informableAgents);
    }

    private List<T> sort(SortBy sortBy, List<T> informableAgents) {
        return informableAgents.stream().sorted(sortBy.getComparator()).collect(Collectors.toList());
    }

    public AgentsList<T> sorted(SortBy sortBy) {
        return new AgentsList<>(this.groupName, this.informableAgents, sortBy);
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
                Comparator.<InformableAgent>comparingLong(SortBy::getStartTimestampFrom)
                        .reversed()
                        .thenComparing(SortBy::getAgentIdFrom)
        );

        private final Comparator<InformableAgent> comparator;

        SortBy(Comparator<InformableAgent> comparator) {
            this.comparator = comparator;
        }

        public Comparator<InformableAgent> getComparator() {
            return comparator;
        }

        private static String getAgentIdFrom(InformableAgent informableAgent) {
            return informableAgent.getAgentInfo().getAgentId();
        }

        private static String getAgentNameFrom(InformableAgent informableAgent) {
            return informableAgent.getAgentInfo().getAgentName();
        }

        private static long getStartTimestampFrom(InformableAgent informableAgent) {
            return informableAgent.getAgentInfo().getStartTimestamp();
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public List<T> getInformableAgents() {
        return informableAgents;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append('\'').append(groupName).append('\'');
        sb.append(":").append(informableAgents);
        sb.append('}');
        return sb.toString();
    }
}
