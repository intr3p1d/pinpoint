package com.navercorp.pinpoint.web.dao;

import com.navercorp.pinpoint.web.vo.agent.AgentInfo;

import java.util.List;

/**
 * @author intr3p1d
 */
public interface AgentListDao {

    List<AgentInfo> getAllAgentInfos(long timestamp);

}
