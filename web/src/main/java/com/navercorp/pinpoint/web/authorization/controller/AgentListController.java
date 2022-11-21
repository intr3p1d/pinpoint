package com.navercorp.pinpoint.web.authorization.controller;

import com.navercorp.pinpoint.web.service.AgentInfoService;
import com.navercorp.pinpoint.web.view.tree.StaticTreeView;
import com.navercorp.pinpoint.web.view.tree.TreeView;
import com.navercorp.pinpoint.web.vo.agent.AgentAndStatus;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilter;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFilterChain;
import com.navercorp.pinpoint.web.vo.agent.AgentStatusAndLink;
import com.navercorp.pinpoint.web.vo.agent.DefaultAgentInfoFilter;
import com.navercorp.pinpoint.web.vo.tree.InstancesList;
import com.navercorp.pinpoint.web.vo.tree.AgentsMapByApplication;
import com.navercorp.pinpoint.web.vo.tree.AgentsMapByHost;
import com.navercorp.pinpoint.web.vo.tree.SortByAgentInfo;
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
@RequestMapping(value = "/agents")
public class AgentListController {
    private final AgentInfoService agentInfoService;

    public AgentListController(AgentInfoService agentInfoService) {
        this.agentInfoService = Objects.requireNonNull(agentInfoService, "agentInfoService");
    }

    @GetMapping("/error50")
    public String error50Testing() {
        for (int i = 0; i < 50; i++) {
            try {
                throw new RuntimeException("top exception");
            } catch (Exception e) {
                // ignored
            }
        }
        throw new RuntimeException("top exception2");
    }


    @GetMapping("/error")
    public String errorTesting() {
        methodC();
        return "Response";
    }

    public void methodA() {
        throw new RuntimeException("Level 1 Error");
    }

    public void methodB() {
        try {
            methodA();
        } catch (Exception e) {
            throw new RuntimeException("Level 2 Error", e);
        }
    }

    public void methodC() {
        try {
            methodB();
            throw new RuntimeException("Middle Error");
        } catch (Exception e) {
            throw new RuntimeException("Level 3 Error", e);
        }
    }

    @GetMapping()
    public TreeView<InstancesList<AgentAndStatus>> getAllAgentsList() {
        long timestamp = System.currentTimeMillis();
        AgentsMapByApplication allAgentsList = this.agentInfoService.getAllAgentsList(AgentInfoFilter::accept, timestamp);
        return treeView(allAgentsList);
    }

    @GetMapping(params = {"from", "to"})
    public TreeView<InstancesList<AgentAndStatus>> getAllAgentsList(
            @RequestParam("from") long from,
            @RequestParam("to") long to) {
        AgentInfoFilter filter = new DefaultAgentInfoFilter(from);
        long timestamp = to;
        AgentsMapByApplication allAgentsList = this.agentInfoService.getAllAgentsList(filter, timestamp);
        return treeView(allAgentsList);
    }

    private static TreeView<InstancesList<AgentAndStatus>> treeView(AgentsMapByApplication agentsListsList) {
        List<InstancesList<AgentAndStatus>> list = agentsListsList.getAgentsListsList();
        return new StaticTreeView<>(list);
    }


    @GetMapping(params = {"application", "sortBy"})
    public TreeView<InstancesList<AgentStatusAndLink>> getAgentsList(
            @RequestParam("application") String applicationName,
            @RequestParam("sortBy") SortByAgentInfo.Rules sortBy) {
        long timestamp = System.currentTimeMillis();
        AgentInfoFilter runningAgentFilter = new AgentInfoFilterChain(
                AgentInfoFilter::filterRunning
        );
        AgentsMapByHost list = this.agentInfoService.getAgentsListByApplicationName(runningAgentFilter, applicationName, timestamp, sortBy);
        return treeView(list);
    }

    @GetMapping(params = {"application", "from", "to", "sortBy"})
    public TreeView<InstancesList<AgentStatusAndLink>> getAgentsList(
            @RequestParam("application") String applicationName,
            @RequestParam("from") long from,
            @RequestParam("to") long to,
            @RequestParam("sortBy") SortByAgentInfo.Rules sortBy) {
        AgentInfoFilter currentRunFilter = new AgentInfoFilterChain(
                new DefaultAgentInfoFilter(from)
        );
        long timestamp = to;
        AgentsMapByHost list = this.agentInfoService.getAgentsListByApplicationName(currentRunFilter, applicationName, timestamp, sortBy);
        return treeView(list);
    }

    private static TreeView<InstancesList<AgentStatusAndLink>> treeView(AgentsMapByHost agentsMapByHost) {
        List<InstancesList<AgentStatusAndLink>> list = agentsMapByHost.getAgentsListsList();
        return new StaticTreeView<>(list);
    }

}
