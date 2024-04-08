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
package com.navercorp.pinpoint.collector.dao.hbase;

import com.navercorp.pinpoint.collector.dao.MapStatisticsInboundDao;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.BulkWriter;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ColumnName;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.MapLinkConfiguration;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.RowKey;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ServiceGroupColumnName;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ServiceGroupRowKey;
import com.navercorp.pinpoint.common.server.util.AcceptedTimeService;
import com.navercorp.pinpoint.common.server.util.ApplicationMapStatisticsUtils;
import com.navercorp.pinpoint.common.server.util.TimeSlot;
import com.navercorp.pinpoint.common.trace.HistogramSchema;
import com.navercorp.pinpoint.common.trace.ServiceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class HbaseMapStatisticsInboundDao implements MapStatisticsInboundDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final AcceptedTimeService acceptedTimeService;

    private final TimeSlot timeSlot;
    private final IgnoreStatFilter ignoreStatFilter;
    private final BulkWriter bulkWriter;
    private final MapLinkConfiguration mapLinkConfiguration;

    public HbaseMapStatisticsInboundDao(
            MapLinkConfiguration mapLinkConfiguration,
            IgnoreStatFilter ignoreStatFilter,
            AcceptedTimeService acceptedTimeService,
            TimeSlot timeSlot,
            @Qualifier("inboundBulkWriter") BulkWriter bulkWriter
    ) {
        this.mapLinkConfiguration = Objects.requireNonNull(mapLinkConfiguration, "mapLinkConfiguration");
        this.ignoreStatFilter = Objects.requireNonNull(ignoreStatFilter, "ignoreStatFilter");
        this.acceptedTimeService = Objects.requireNonNull(acceptedTimeService, "acceptedTimeService");
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot");

        this.bulkWriter = Objects.requireNonNull(bulkWriter, "inboundBulkWriter");
    }


    @Override
    public void update(
            String callerServiceGroupName, String callerApplicationName, ServiceType callerServiceType,
            String calleeServiceGroupName, String calleeApplicationName, ServiceType calleeServiceType,
            String calleeHost, int elapsed, boolean isError
    ) {
        Objects.requireNonNull(callerServiceGroupName, "callerServiceGroupName");
        Objects.requireNonNull(calleeServiceGroupName, "calleeServiceGroupName");
        Objects.requireNonNull(callerApplicationName, "callerApplicationName");
        Objects.requireNonNull(calleeServiceGroupName, "calleeApplicationName");

        if (logger.isDebugEnabled()) {
            logger.debug("[Inbound] {} {}({})[{}] <- {} {}({})",
                    calleeServiceGroupName, calleeApplicationName, calleeServiceType, calleeHost,
                    callerServiceGroupName, callerApplicationName, callerServiceType
            );
        }


        // TODO callee, caller parameter normalization
        if (ignoreStatFilter.filter(callerServiceType, calleeHost)) {
            logger.debug("[Ignore-Inbound] {} {}({})[{}] <- {} {}({})",
                    calleeServiceGroupName, calleeApplicationName, calleeServiceType, calleeHost,
                    callerServiceGroupName, callerApplicationName, callerServiceType
            );
            return;
        }

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);

        // this is callee in inbound
        final RowKey calleeRowKey = new ServiceGroupRowKey(calleeServiceGroupName, calleeServiceType.getCode(), calleeApplicationName, rowTimeSlot);

        // that is caller in outbound
        final short callerSlotNumber = ApplicationMapStatisticsUtils.getSlotNumber(callerServiceType, elapsed, isError);
        HistogramSchema histogramSchema = callerServiceType.getHistogramSchema();

        final ColumnName callerColumnName = new ServiceGroupColumnName(callerServiceGroupName, callerServiceType.getCode(), callerApplicationName, callerSlotNumber);
        this.bulkWriter.increment(calleeRowKey, callerColumnName);

        if (mapLinkConfiguration.isEnableAvg()) {
            final ColumnName sumColumnName = new ServiceGroupColumnName(callerServiceGroupName, callerServiceType.getCode(), callerApplicationName, histogramSchema.getSumStatSlot().getSlotTime());
            this.bulkWriter.increment(calleeRowKey, sumColumnName, elapsed);
        }
        if (mapLinkConfiguration.isEnableMax()) {
            final ColumnName maxColumnName = new ServiceGroupColumnName(callerServiceGroupName, callerServiceType.getCode(), callerApplicationName, histogramSchema.getMaxStatSlot().getSlotTime());
            this.bulkWriter.updateMax(calleeRowKey, maxColumnName, elapsed);
        }

    }

    @Override
    public void flushLink() {
        this.bulkWriter.flushLink();
    }

    @Override
    public void flushAvgMax() {
        this.bulkWriter.flushAvgMax();
    }

}
