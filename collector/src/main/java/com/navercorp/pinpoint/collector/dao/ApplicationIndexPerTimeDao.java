package com.navercorp.pinpoint.collector.dao;

import com.navercorp.pinpoint.common.server.bo.AgentInfoBo;

public interface ApplicationIndexPerTimeDao {
    void insert(final AgentInfoBo agentInfo);
}
