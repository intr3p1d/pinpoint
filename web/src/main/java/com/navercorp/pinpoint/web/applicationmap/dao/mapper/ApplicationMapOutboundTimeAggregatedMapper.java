/*
 * Copyright 2024 NAVER Corp.
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
package com.navercorp.pinpoint.web.applicationmap.dao.mapper;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.buffer.FixedBuffer;
import com.navercorp.pinpoint.common.buffer.OffsetFixedBuffer;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.common.hbase.util.CellUtils;
import com.navercorp.pinpoint.common.util.TimeUtils;
import com.navercorp.pinpoint.web.applicationmap.link.LinkDirection;
import com.navercorp.pinpoint.web.applicationmap.rawdata.LinkDataMap;
import com.navercorp.pinpoint.web.component.ApplicationFactory;
import com.navercorp.pinpoint.web.vo.Application;
import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author intr3p1d
 */
@Component
public class ApplicationMapOutboundTimeAggregatedMapper implements RowMapper<LinkDataMap> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final LinkFilter filter;

    @Autowired
    private ApplicationFactory applicationFactory;

    @Autowired
    @Qualifier("applicationMapOutboundRowKeyDistributor")
    private RowKeyDistributorByHashPrefix rowKeyDistributorByHashPrefix;

    public ApplicationMapOutboundTimeAggregatedMapper() {
        this(LinkFilter::skip);
    }

    public ApplicationMapOutboundTimeAggregatedMapper(LinkFilter filter) {
        this.filter = filter;
    }

    @Override
    public LinkDataMap mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return new LinkDataMap();
        }

        logger.debug("mapRow: {}", rowNum);
        final byte[] rowKey = getOriginalKey(result.getRow());

        final Buffer row = new FixedBuffer(rowKey);
        final Application srcApplication = readSourceApplication(row);
        final long timestamp = 0; // aggregate timestamp

        // key is dest ApplicationName
        final LinkDataMap linkDataMap = new LinkDataMap();
        for (Cell cell : result.rawCells()) {
            final Buffer buffer = new OffsetFixedBuffer(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
            short histogramSlot = buffer.readShort();

            final Application destApplication = readDestApplication(buffer);
            if (filter.filter(destApplication)) {
                continue;
            }

            String srcAgentId = srcApplication.getName();
            String destHost = destApplication.getName();

            boolean isError = histogramSlot == (short) -1;

            long requestCount = CellUtils.valueToLong(cell);
            if (logger.isDebugEnabled()) {
                logger.debug("    Fetched {}.(New) {} {} -> {} (slot:{}/{}) destApplicationHost:{}", LinkDirection.OUT_LINK, srcApplication, srcAgentId, destApplication, histogramSlot, requestCount, destHost);
            }

            final short slotTime = (isError) ? (short) -1 : histogramSlot;

            linkDataMap.addLinkData(srcApplication, srcAgentId, destApplication, destHost, timestamp, slotTime, requestCount);
        }

        return linkDataMap;
    }

    private Application readSourceApplication(Buffer row) {
        String serviceName = row.read2PrefixedString();
        String applicationName = row.read2PrefixedString();
        short serviceType = row.readShort();
        return applicationFactory.createApplication(applicationName, serviceType);
    }

    private Application readDestApplication(Buffer buffer) {
        short serviceType = buffer.readShort();
        String applicationName = buffer.read2PrefixedString();
        String serviceName = buffer.read2PrefixedString();
        return applicationFactory.createApplication(applicationName, serviceType);
    }

    private byte[] getOriginalKey(byte[] rowKey) {
        return rowKeyDistributorByHashPrefix.getOriginalKey(rowKey);
    }


}
