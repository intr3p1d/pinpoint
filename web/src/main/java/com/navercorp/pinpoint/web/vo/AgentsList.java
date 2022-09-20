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

import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author HyunGil Jeong
 */
public class AgentsList {

    private final String groupName;
    private final List<AgentStatusAndLink> agentInfoAndLinkList;

    public AgentsList(String groupName, List<AgentStatusAndLink> agentInfoAndLinkList, SortBy sortBy) {
        this.groupName = Objects.requireNonNull(groupName, "groupName");
        Objects.requireNonNull(sortBy, "sortBy");
        Objects.requireNonNull(agentInfoAndLinkList, "agentInfoAndLinkList");
        this.agentInfoAndLinkList = sort(sortBy, agentInfoAndLinkList);
    }

    private static List<AgentStatusAndLink> sort(SortBy sortBy, List<AgentStatusAndLink> agentStatusAndLinks) {
        return agentStatusAndLinks.stream().sorted(sortBy.getComparator()).collect(Collectors.toList());
    }

    public AgentsList sorted(SortBy sortBy) {
        return new AgentsList(this.groupName, this.agentInfoAndLinkList, sortBy);
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
                Comparator.<AgentStatusAndLink>comparingLong(SortBy::getStartTimestampFrom)
                        .reversed()
                        .thenComparing(SortBy::getAgentIdFrom)
        );

        private final Comparator<AgentStatusAndLink> comparator;

        SortBy(Comparator<AgentStatusAndLink> comparator) {
            this.comparator = comparator;
        }

        public Comparator<AgentStatusAndLink> getComparator() {
            return comparator;
        }

        private static String getAgentIdFrom(AgentStatusAndLink agentStatusAndLink) {
            return agentStatusAndLink.getAgentInfo().getAgentId();
        }

        private static String getAgentNameFrom(AgentStatusAndLink agentStatusAndLink) {
            return agentStatusAndLink.getAgentInfo().getAgentName();
        }

        private static long getStartTimestampFrom(AgentStatusAndLink agentStatusAndLink) {
            return agentStatusAndLink.getAgentInfo().getStartTimestamp();
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public List<AgentStatusAndLink> getAgentStatusAndLinks() {
        return agentInfoAndLinkList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append('\'').append(groupName).append('\'');
        sb.append(":").append(agentInfoAndLinkList);
        sb.append('}');
        return sb.toString();
    }
}
