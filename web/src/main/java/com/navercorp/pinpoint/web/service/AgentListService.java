package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.tree.AgentsMapByApplication;

/**
 * @author intr3p1d
 */
public interface AgentListService {

    AgentsMapByApplication<AgentInfo> getAllAgentsList(long timestamp);
}
