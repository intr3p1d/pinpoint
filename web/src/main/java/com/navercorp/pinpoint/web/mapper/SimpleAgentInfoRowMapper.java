package com.navercorp.pinpoint.web.mapper;

import com.navercorp.pinpoint.common.PinpointConstants;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.FixedBuffer;
import com.navercorp.pinpoint.common.hbase.HbaseTableConstants;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.common.server.bo.AgentInfoBo;
import com.navercorp.pinpoint.common.server.bo.JvmInfoBo;
import com.navercorp.pinpoint.common.server.bo.ServerMetaDataBo;
import com.navercorp.pinpoint.common.util.BytesUtils;
import com.navercorp.pinpoint.common.util.TimeUtils;
import com.navercorp.pinpoint.web.vo.agent.SimpleAgentInfoWithVersion;
import org.apache.hadoop.hbase.client.Result;
import org.springframework.stereotype.Component;

import static com.navercorp.pinpoint.common.hbase.HbaseColumnFamily.AGENTINFO_INFO;

@Component
public class SimpleAgentInfoRowMapper implements RowMapper<SimpleAgentInfoWithVersion> {
    @Override
    public SimpleAgentInfoWithVersion mapRow(Result result, int rowNum) throws Exception {
        byte[] rowKey = result.getRow();
        String agentId = BytesUtils.safeTrim(BytesUtils.toString(rowKey, 0, PinpointConstants.AGENT_ID_MAX_LEN));
        long reverseStartTime = BytesUtils.bytesToLong(rowKey, HbaseTableConstants.AGENT_ID_MAX_LEN);
        long startTime = TimeUtils.recoveryTimeMillis(reverseStartTime);

        final byte[] serializedAgentInfo = result.getValue(AGENTINFO_INFO.getName(), AGENTINFO_INFO.QUALIFIER_IDENTIFIER);
        final AgentInfoBo.Builder agentInfoBoBuilder = createBuilderFromValue(serializedAgentInfo);
        agentInfoBoBuilder.setAgentId(agentId);
        agentInfoBoBuilder.setStartTime(startTime);

        final byte[] serializedServerMetaData = result.getValue(AGENTINFO_INFO.getName(), AGENTINFO_INFO.QUALIFIER_SERVER_META_DATA);
        if (serializedServerMetaData != null) {
            agentInfoBoBuilder.setServerMetaData(new ServerMetaDataBo.Builder(serializedServerMetaData).build());
        }

        final byte[] serializedJvmInfo = result.getValue(AGENTINFO_INFO.getName(), AGENTINFO_INFO.QUALIFIER_JVM);
        if (serializedJvmInfo != null) {
            agentInfoBoBuilder.setJvmInfo(new JvmInfoBo(serializedJvmInfo));
        }
        AgentInfoBo agentInfoBo = agentInfoBoBuilder.build();
        SimpleAgentInfoWithVersion simpleAgentInfoWithVersion = new SimpleAgentInfoWithVersion();
        simpleAgentInfoWithVersion.setAgentId(agentInfoBo.getAgentId());
        simpleAgentInfoWithVersion.setAgentName(agentInfoBo.getAgentName());
        simpleAgentInfoWithVersion.setApplicationName(agentInfoBo.getApplicationName());
        simpleAgentInfoWithVersion.setAgentVersion(agentInfoBo.getAgentVersion());
        simpleAgentInfoWithVersion.setJvmInfo(agentInfoBo.getJvmInfo());

        return simpleAgentInfoWithVersion;
    }

    private AgentInfoBo.Builder createBuilderFromValue(byte[] serializedAgentInfo) {
        final Buffer buffer = new FixedBuffer(serializedAgentInfo);
        final AgentInfoBo.Builder builder = new AgentInfoBo.Builder();
        builder.setHostName(buffer.readPrefixedString());
        builder.setIp(buffer.readPrefixedString());
        builder.setPorts(buffer.readPrefixedString());
        builder.setApplicationName(buffer.readPrefixedString());
        builder.setServiceTypeCode(buffer.readShort());
        builder.setPid(buffer.readInt());
        builder.setAgentVersion(buffer.readPrefixedString());
        builder.setStartTime(buffer.readLong());
        builder.setEndTimeStamp(buffer.readLong());
        builder.setEndStatus(buffer.readInt());
        // FIXME - 2015.09 v1.5.0 added vmVersion (check for compatibility)
        if (buffer.hasRemaining()) {
            builder.setVmVersion(buffer.readPrefixedString());
        }
        // FIXME - 2018.06 v1.8.0 added container (check for compatibility)
        if (buffer.hasRemaining()) {
            builder.isContainer(buffer.readBoolean());
        }
        // 2021.03.24 added agent name
        if (buffer.hasRemaining()) {
            builder.setAgentName(buffer.readPrefixedString());
        }
        return builder;
    }
}
