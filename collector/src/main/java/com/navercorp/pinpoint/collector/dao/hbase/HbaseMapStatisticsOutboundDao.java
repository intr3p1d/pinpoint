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

import com.navercorp.pinpoint.collector.dao.MapStatisticsOutboundDao;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.BulkWriter;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ColumnName;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.OutboudColumnName;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.MapLinkConfiguration;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.RowKey;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ServiceCallRowKey;
import com.navercorp.pinpoint.common.server.util.AcceptedTimeService;
import com.navercorp.pinpoint.common.server.util.ApplicationMapStatisticsUtils;
import com.navercorp.pinpoint.common.server.util.TimeSlot;
import com.navercorp.pinpoint.common.trace.HistogramSchema;
import com.navercorp.pinpoint.common.trace.ServiceType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class HbaseMapStatisticsOutboundDao implements MapStatisticsOutboundDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final AcceptedTimeService acceptedTimeService;

    private final TimeSlot timeSlot;

    private final IgnoreStatFilter ignoreStatFilter;
    private final BulkWriter bulkWriter;
    private final MapLinkConfiguration mapLinkConfiguration;

    public HbaseMapStatisticsOutboundDao(MapLinkConfiguration mapLinkConfiguration,
                                         IgnoreStatFilter ignoreStatFilter,
                                         AcceptedTimeService acceptedTimeService, TimeSlot timeSlot,
                                         @Qualifier("calleeBulkWriter") BulkWriter bulkWriter) {
        this.mapLinkConfiguration = Objects.requireNonNull(mapLinkConfiguration, "mapLinkConfiguration");
        this.ignoreStatFilter = Objects.requireNonNull(ignoreStatFilter, "ignoreStatFilter");
        this.acceptedTimeService = Objects.requireNonNull(acceptedTimeService, "acceptedTimeService");
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot");

        this.bulkWriter = Objects.requireNonNull(bulkWriter, "bulkWriter");
    }


    @Override
    public void update(
            String thatServiceGroup, String thatApplicationName, ServiceType thatServiceType,
            String thisServiceGroup, String thisApplicationName, ServiceType thisServiceType,
            String thisHost, int elapsed, boolean isError
    ) {
        Objects.requireNonNull(thatServiceGroup, "thatServiceGroup");
        Objects.requireNonNull(thisServiceGroup, "thisServiceGroup");

        if (logger.isDebugEnabled()) {
            logger.debug("[Outbound] {} {}({})[{}] -> {} {}({})",
                    thisServiceGroup, thisApplicationName, thisServiceType, thisHost,
                    thatServiceGroup, thatApplicationName, thatServiceType
            );
        }

        // there may be no endpoint in case of httpclient
        thisHost = StringUtils.defaultString(thisHost);

        // TODO callee, caller parameter normalization
        if (ignoreStatFilter.filter(thatServiceType, thisHost)) {
            logger.debug("[Ignore-Outbound] {} {}({})[{}] -> {} {}({})",
                    thisServiceGroup, thisApplicationName, thisServiceType, thisHost,
                    thatServiceGroup, thatApplicationName, thatServiceType
            );
            return;
        }

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);
        final RowKey calleeRowKey = new ServiceCallRowKey(thisServiceGroup, rowTimeSlot);

        final short callerSlotNumber = ApplicationMapStatisticsUtils.getSlotNumber(thatServiceType, elapsed, isError);

        HistogramSchema histogramSchema = thatServiceType.getHistogramSchema();

        final ColumnName callerColumnName = new OutboudColumnName(thisServiceGroup, thisServiceType.getCode(), thisApplicationName, callerSlotNumber);
        this.bulkWriter.increment(calleeRowKey, callerColumnName);

        if (mapLinkConfiguration.isEnableAvg()) {
            final ColumnName sumColumnName = new OutboudColumnName(thisServiceGroup, thisServiceType.getCode(), thisApplicationName, histogramSchema.getSumStatSlot().getSlotTime());
            this.bulkWriter.increment(calleeRowKey, sumColumnName, elapsed);
        }
        if (mapLinkConfiguration.isEnableMax()) {
            final ColumnName maxColumnName = new OutboudColumnName(thisServiceGroup, thisServiceType.getCode(), thisApplicationName, histogramSchema.getMaxStatSlot().getSlotTime());
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
