package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.hbase.ResultsExtractor;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.web.vo.agent.SimpleAgentInfoWithVersion;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SimpleAgentInfoResultsExtractor implements ResultsExtractor<SimpleAgentInfoWithVersion> {

    private final RowMapper<SimpleAgentInfoWithVersion> simpleAgentInfoRowMapperRowMapper;

    public SimpleAgentInfoResultsExtractor(RowMapper<SimpleAgentInfoWithVersion> simpleAgentInfoWithVersionRowMapper) {
        this.simpleAgentInfoRowMapperRowMapper = Objects.requireNonNull(simpleAgentInfoWithVersionRowMapper, "simpleAgentInfoWithVersionRowMapper");
    }

    @Override
    public SimpleAgentInfoWithVersion extractData(ResultScanner results) throws Exception {
        for (Result result : results) {
            return simpleAgentInfoRowMapperRowMapper.mapRow(result, 0);
        }
        return null;
    }

}
