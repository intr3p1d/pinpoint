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
import com.navercorp.pinpoint.collector.dao.hbase.statistics.InboundColumnName;
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
public class HbaseMapStatisticsInboundDao implements MapStatisticsInboundDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final AcceptedTimeService acceptedTimeService;

    private final TimeSlot timeSlot;
    private final BulkWriter bulkWriter;
    private final MapLinkConfiguration mapLinkConfiguration;

    public HbaseMapStatisticsInboundDao(MapLinkConfiguration mapLinkConfiguration,
                                        AcceptedTimeService acceptedTimeService,
                                        TimeSlot timeSlot,
                                        @Qualifier("callerBulkWriter") BulkWriter bulkWriter) {
        this.mapLinkConfiguration = Objects.requireNonNull(mapLinkConfiguration, "mapLinkConfiguration");
        this.acceptedTimeService = Objects.requireNonNull(acceptedTimeService, "acceptedTimeService");
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot");

        this.bulkWriter = Objects.requireNonNull(bulkWriter, "bulkWrtier");
    }


    @Override
    public void update(
            String thatServiceGroup, String thatApplicationName, ServiceType thatServiceType,
            String thisServiceGroup, String thisApplicationName, ServiceType thisServiceType,
            String thisHost, int elapsed, boolean isError
    ) {
        Objects.requireNonNull(thatApplicationName, "thatApplicationName");
        Objects.requireNonNull(thatApplicationName, "thisApplicationName");


        if (logger.isDebugEnabled()) {
            logger.debug("[Inbound] {} {}({})[{}] <- {} {}({})",
                    thisServiceGroup, thisApplicationName, thisServiceType, thisHost,
                    thatServiceGroup, thatApplicationName, thatServiceType
            );
        }

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);
        final RowKey callerRowKey = new ServiceCallRowKey(thisServiceGroup, rowTimeSlot);

        final short calleeSlotNumber = ApplicationMapStatisticsUtils.getSlotNumber(thatServiceType, elapsed, isError);

        HistogramSchema histogramSchema = thatServiceType.getHistogramSchema();

        final ColumnName calleeColumnName = new InboundColumnName(thatServiceType.getCode(), thatApplicationName, thisServiceGroup, thisServiceType.getCode(), thisApplicationName, calleeSlotNumber);
        this.bulkWriter.increment(callerRowKey, calleeColumnName);

        if (mapLinkConfiguration.isEnableAvg()) {
            final ColumnName sumColumnName = new InboundColumnName(thatServiceType.getCode(), thatApplicationName, thisServiceGroup, thisServiceType.getCode(), thisApplicationName, histogramSchema.getSumStatSlot().getSlotTime());
            this.bulkWriter.increment(callerRowKey, sumColumnName, elapsed);
        }
        if (mapLinkConfiguration.isEnableMax()) {
            final ColumnName maxColumnName = new InboundColumnName(thatServiceType.getCode(), thatApplicationName, thisServiceGroup, thisServiceType.getCode(), thisApplicationName, histogramSchema.getMaxStatSlot().getSlotTime());
            this.bulkWriter.updateMax(callerRowKey, maxColumnName, elapsed);
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
