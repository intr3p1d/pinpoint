/*
 * Copyright 2025 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.servermap.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseOperations;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.servermap.bo.CallCount;
import com.navercorp.pinpoint.servermap.bo.DirectionalBo;
import com.navercorp.pinpoint.servermap.dao.hbase.statistics.ApplicationMapColumnName;
import com.navercorp.pinpoint.servermap.dao.hbase.statistics.ApplicationMapRowKey;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class HbaseApplicationMapDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final HbaseColumnFamily.InboundServiceMap DESCRIPTOR = HbaseColumnFamily.MAP_STATISTICS_INBOUND_SERVICE_GROUP_COUNTER;

    private final HbaseOperations hbaseTemplate;
    private final TableNameProvider tableNameProvider;


    public HbaseApplicationMapDao(
            HbaseOperations hbaseTemplate,
            TableNameProvider tableNameProvider
    ) {
        this.hbaseTemplate = Objects.requireNonNull(hbaseTemplate, "hbaseTemplate");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");
    }

    public void insert(DirectionalBo directionalBo) {
        Objects.requireNonNull(directionalBo, "directionalBo");
        if (logger.isDebugEnabled()) {
            logger.debug("insert application map data: {}", directionalBo);
        }

        ArrayList<Put> puts = new ArrayList<>();

        for (CallCount callCount : directionalBo.getCallCountList()) {

            ApplicationMapRowKey rowKey = new ApplicationMapRowKey(
                    directionalBo.getMainServiceId(),
                    directionalBo.getMainServiceType(),
                    directionalBo.getMainApplicationName(),
                    callCount.timestamp()
            );

            ApplicationMapColumnName columnName = new ApplicationMapColumnName(
                    directionalBo.getSubServiceId(),
                    directionalBo.getSubServiceType(),
                    directionalBo.getSubApplicationName(),
                    directionalBo.getSlotNumber()
            );

            Put put = new Put(rowKey.getRowKey());
            put.addColumn(
                    DESCRIPTOR.getName(),
                    columnName.getColumnName(),
                    Bytes.toBytes(callCount.callCount())
            );
            puts.add(put);
        }

        TableName applicationMapTableName = tableNameProvider.getTableName(DESCRIPTOR.getTable());
        this.hbaseTemplate.put(applicationMapTableName, puts);
    }
}
