package com.navercorp.pinpoint.web.service;

import com.navercorp.pinpoint.web.dao.AgentListDao;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.tree.AgentsMapByApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
public class AgentListServiceImpl implements AgentListService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final AgentListDao agentListDao;


    public AgentListServiceImpl(AgentListDao agentListDao) {
        this.agentListDao = Objects.requireNonNull(agentListDao, "agentListDao");
    }


    @Override
    public AgentsMapByApplication<AgentInfo> getAllAgentsList(long timestamp) {
        List<AgentInfo> agentInfoList = agentListDao.getAllAgentInfos(timestamp);
        return AgentsMapByApplication.newAgentsMap(agentInfoList);
    }
}
