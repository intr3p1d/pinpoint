/*
 * Copyright 2026 NAVER Corp.
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
package com.navercorp.pinpoint.web.vo.agent;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class AgentNameGroup {
    private final String agentName;
    private final List<AgentStatusAndLink> agents;

    public AgentNameGroup(String agentName, List<AgentStatusAndLink> agents) {
        this.agentName = Objects.requireNonNull(agentName, "agentName");
        this.agents = Objects.requireNonNull(agents, "agents");
    }

    public String getAgentName() {
        return agentName;
    }

    public List<AgentStatusAndLink> getAgents() {
        return agents;
    }

    @Override
    public String toString() {
        return "AgentNameGroup{" +
                "agentName='" + agentName + '\'' +
                ", agents=" + agents +
                '}';
    }
}
