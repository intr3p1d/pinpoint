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

    @GetMapping(value = "/all")
    public TreeView<AgentStatusAndLink> getAllAgentsList() {
        long timestamp = System.currentTimeMillis();
        return getAllAgentsList(timestamp);
    }

    @GetMapping(value = "/all", params = {"from", "to"})
    public TreeView<AgentStatusAndLink> getAllAgentsList(
            @RequestParam("from") long from,
            @RequestParam("to") long to) {
        AgentInfoFilter filter = new DefaultAgentInfoFilter(from);
        long timestamp = to;
        AgentsMapByApplication allAgentsList = this.agentInfoService.getAllAgentsList(filter, timestamp);
        return treeView(allAgentsList);
    }

    @GetMapping(value = "/all", params = {"timestamp"})
    public TreeView<AgentStatusAndLink> getAllAgentsList(
            @RequestParam("timestamp") long timestamp) {
        AgentsMapByApplication allAgentsList = this.agentInfoService.getAllAgentsList(AgentInfoFilter::accept, timestamp);
        return treeView(allAgentsList);
    }

    private static TreeView<AgentStatusAndLink> treeView(AgentsMapByApplication agentsListsList) {
        List<InstancesList<AgentStatusAndLink>> list = agentsListsList.getAgentsListsList();
        return new StaticTreeView<>(list, InstancesList::getGroupName, InstancesList::getInstancesList);
    }


    @GetMapping(params = {"application"})
    public TreeView<AgentAndStatus> getAgentsList(@RequestParam("application") String applicationName) {
        long timestamp = System.currentTimeMillis();
        return getAgentsList(applicationName, timestamp);
    }

    @GetMapping(params = {"application", "from", "to"})
    public TreeView<AgentAndStatus> getAgentsList(
            @RequestParam("application") String applicationName,
            @RequestParam("from") long from,
            @RequestParam("to") long to) {
        AgentInfoFilter currentRunFilter = new AgentInfoFilterChain(
                new DefaultAgentInfoFilter(from)
        );
        long timestamp = to;
        AgentsMapByHost list = this.agentInfoService.getAgentsListByApplicationName(currentRunFilter, applicationName, timestamp);
        return treeView(list);
    }

    @GetMapping(params = {"application", "timestamp"})
    public TreeView<AgentAndStatus> getAgentsList(
            @RequestParam("application") String applicationName,
            @RequestParam("timestamp") long timestamp) {
        AgentInfoFilter runningAgentFilter = new AgentInfoFilterChain(
                AgentInfoFilter::filterRunning
        );
        AgentsMapByHost list = this.agentInfoService.getAgentsListByApplicationName(runningAgentFilter, applicationName, timestamp);
        return treeView(list);
    }

    private static TreeView<AgentAndStatus> treeView(AgentsMapByHost agentsMapByHost) {
        List<InstancesList<AgentAndStatus>> list = agentsMapByHost.getAgentsListsList();
        return new StaticTreeView<>(list, InstancesList::getGroupName, InstancesList::getInstancesList);
    }

}
