package com.navercorp.pinpoint.web.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.hbase.ResultsExtractor;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.web.dao.AgentListDao;
import com.navercorp.pinpoint.web.vo.agent.AgentInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */

@Repository
public class HbaseAgentListDao implements AgentListDao {

    private static final int SCANNER_CACHING = 1;

    private static final HbaseColumnFamily.AgentInfo DESCRIPTOR = HbaseColumnFamily.AGENTINFO_INFO;

    private final HbaseOperations2 hbaseOperations2;

    private final TableNameProvider tableNameProvider;

    private final ResultsExtractor<List<AgentInfo>> agentInfosResultsExtractor;

    public HbaseAgentListDao(HbaseOperations2 hbaseOperations2,
                             TableNameProvider tableNameProvider,
                             ResultsExtractor<List<AgentInfo>> agentInfosResultsExtractor) {
        this.hbaseOperations2 = Objects.requireNonNull(hbaseOperations2, "hbaseOperations2");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");
        this.agentInfosResultsExtractor = Objects.requireNonNull(agentInfosResultsExtractor, "agentInfosResultsExtractor");
    }

    @Override
    public List<AgentInfo> getAllAgentInfos(long timestamp) {
        Scan scan = createAllScan(AgentInfoColumn.simple());
        TableName agentInfoTableName = tableNameProvider.getTableName(DESCRIPTOR.getTable());
        return this.hbaseOperations2.find(agentInfoTableName, scan, agentInfosResultsExtractor);
    }

    private Scan createAllScan(AgentInfoColumn column) {
        Scan scan = new Scan();

        final byte[] family = DESCRIPTOR.getName();
        if (column.identifier) {
            scan.addColumn(family, DESCRIPTOR.QUALIFIER_IDENTIFIER);
        }
        if (column.serverMetaData) {
            scan.addColumn(family, DESCRIPTOR.QUALIFIER_SERVER_META_DATA);
        }
        if (column.jvm) {
            scan.addColumn(family, DESCRIPTOR.QUALIFIER_JVM);
        }

        scan.setMaxVersions(1);
        scan.setCaching(SCANNER_CACHING);

        return scan;
    }

    private static class AgentInfoColumn {
        private final boolean identifier;
        private final boolean serverMetaData;
        private final boolean jvm;

        public AgentInfoColumn(boolean identifier, boolean serverMetaData, boolean jvm) {
            this.identifier = identifier;
            this.serverMetaData = serverMetaData;
            this.jvm = jvm;
        }

        public static AgentInfoColumn all() {
            return new AgentInfoColumn(true, true, true);
        }

        public static AgentInfoColumn simple() {
            return new AgentInfoColumn(true, false, false);
        }

        public static AgentInfoColumn jvm() {
            return new AgentInfoColumn(true, false, true);
        }
    }
}
