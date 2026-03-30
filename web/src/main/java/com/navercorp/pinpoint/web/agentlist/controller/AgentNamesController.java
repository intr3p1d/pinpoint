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
package com.navercorp.pinpoint.web.agentlist.controller;

import com.navercorp.pinpoint.common.timeseries.time.Range;
import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.web.agentlist.AgentsFactory;
import com.navercorp.pinpoint.web.agentlist.service.AgentsService;
import com.navercorp.pinpoint.web.component.ApplicationFactory;
import com.navercorp.pinpoint.web.service.ApplicationAgentListQueryRule;
import com.navercorp.pinpoint.web.vo.Application;
import com.navercorp.pinpoint.web.vo.Service;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilters;
import com.navercorp.pinpoint.web.vo.agent.AgentNameGroupView;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@RestController
@RequestMapping(value = "/api/agentNames")
@Validated
public class AgentNamesController {

    private final AgentsService agentsService;
    private final ApplicationFactory applicationFactory;

    public AgentNamesController(AgentsService agentsService, ApplicationFactory applicationFactory) {
        this.agentsService = Objects.requireNonNull(agentsService, "agentsService");
        this.applicationFactory = Objects.requireNonNull(applicationFactory, "applicationFactory");
    }

    @PreAuthorize("hasPermission(#applicationName, 'application', 'inspector')")
    @GetMapping(value = "/overview", params = {"application"})
    public List<AgentNameGroupView> getAgentsListGroupedByName(
            @RequestParam("application") @NotBlank String applicationName,
            @RequestParam(value = "serviceTypeCode", required = false) Short serviceTypeCode,
            @RequestParam(value = "serviceTypeName", required = false) String serviceTypeName,
            @RequestParam(value = "query", required = false) String query) {
        final ApplicationAgentListQueryRule rule = ApplicationAgentListQueryRule
                .getByValue(query, ApplicationAgentListQueryRule.ALL);
        final long timestamp = System.currentTimeMillis();
        final Application application = createApplication(Service.DEFAULT, applicationName, serviceTypeCode, serviceTypeName);
        Range between = Range.between(timestamp, timestamp);
        TimeWindow timeWindow = new TimeWindow(between);
        List<AgentStatusAndLink> agents = agentsService.getAgentsByApplicationName(
                application, timeWindow, rule, AgentInfoFilters.acceptAll());
        return AgentsFactory.groupByAgentName(agents);
    }

    private Application createApplication(Service service, String applicationName, Short serviceTypeCode, String serviceTypeName) {
        if (StringUtils.hasLength(applicationName)) {
            if (serviceTypeCode != null) {
                return applicationFactory.createApplication(service, applicationName, serviceTypeCode);
            } else if (serviceTypeName != null) {
                return applicationFactory.createApplicationByTypeName(service, applicationName, serviceTypeName);
            }
        }
        return new Application(applicationName, ServiceType.UNDEFINED);
    }
}
