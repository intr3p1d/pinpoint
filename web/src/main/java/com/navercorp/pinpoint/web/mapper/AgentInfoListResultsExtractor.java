package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.hbase.ResultsExtractor;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.common.server.bo.AgentInfoBo;
import com.navercorp.pinpoint.loader.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import com.navercorp.pinpoint.web.vo.agent.AgentInfoFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Component
public class AgentInfoListResultsExtractor implements ResultsExtractor<List<AgentInfo>> {

    private final AgentInfoFactory factory;
    private final RowMapper<AgentInfoBo> agentInfoMapper;

    public AgentInfoListResultsExtractor(ServiceTypeRegistryService registryService,
                                         RowMapper<AgentInfoBo> agentInfoMapper) {
        Objects.requireNonNull(registryService, "registryService");

        this.factory = new AgentInfoFactory(registryService);
        this.agentInfoMapper = Objects.requireNonNull(agentInfoMapper, "agentInfoMapper");
    }

    @Override
    public List<AgentInfo> extractData(ResultScanner results) throws Exception {
        List<AgentInfo> agentInfos = new ArrayList<>();
        for (Result result : results) {
            AgentInfoBo agentInfoBo = agentInfoMapper.mapRow(result, 0);
            agentInfos.add(factory.build(agentInfoBo));
        }
        return agentInfos;
    }
}
