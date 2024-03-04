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

import com.navercorp.pinpoint.collector.dao.MapSelfResponseTimeDao;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.BulkWriter;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ColumnName;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.MapLinkConfiguration;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.RowKey;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ServiceGroupRowKey;
import com.navercorp.pinpoint.collector.dao.hbase.statistics.ServiceResponseColumnName;
import com.navercorp.pinpoint.common.server.util.AcceptedTimeService;
import com.navercorp.pinpoint.common.server.util.ApplicationMapStatisticsUtils;
import com.navercorp.pinpoint.common.server.util.TimeSlot;
import com.navercorp.pinpoint.common.trace.HistogramSchema;
import com.navercorp.pinpoint.common.trace.ServiceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class HbaseMapSelfResponseTimeDao implements MapSelfResponseTimeDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final AcceptedTimeService acceptedTimeService;

    private final TimeSlot timeSlot;
    private final BulkWriter bulkWriter;
    private final MapLinkConfiguration mapLinkConfiguration;

    public HbaseMapSelfResponseTimeDao(MapLinkConfiguration mapLinkConfiguration,
                                       AcceptedTimeService acceptedTimeService, TimeSlot timeSlot,
                                       @Qualifier("selfBulkWriter") BulkWriter bulkWriter) {
        this.mapLinkConfiguration = Objects.requireNonNull(mapLinkConfiguration, "mapLinkConfiguration");
        this.acceptedTimeService = Objects.requireNonNull(acceptedTimeService, "acceptedTimeService");
        this.timeSlot = Objects.requireNonNull(timeSlot, "timeSlot");
        this.bulkWriter = Objects.requireNonNull(bulkWriter, "bulkWrtier");
    }


    @Override
    public void received(String serviceGroup, String applicationName, ServiceType applicationServiceType, int elapsed, boolean isError) {
        Objects.requireNonNull(serviceGroup, "serviceGroup");
        Objects.requireNonNull(applicationName, "applicationName");


        if (logger.isDebugEnabled()) {
            logger.debug("[Received] {} {} ({})", serviceGroup, applicationName, applicationServiceType);
        }

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);
        final RowKey selfRowKey = new ServiceGroupRowKey(serviceGroup, rowTimeSlot);

        final short slotNumber = ApplicationMapStatisticsUtils.getSlotNumber(applicationServiceType, elapsed, isError);
        final ColumnName selfColumnName = new ServiceResponseColumnName(serviceGroup, applicationName, applicationServiceType.getCode(), slotNumber);
        this.bulkWriter.increment(selfRowKey, selfColumnName);

        HistogramSchema histogramSchema = applicationServiceType.getHistogramSchema();
        if (mapLinkConfiguration.isEnableAvg()) {
            final ColumnName sumColumnName = new ServiceResponseColumnName(serviceGroup, applicationName, applicationServiceType.getCode(), histogramSchema.getSumStatSlot().getSlotTime());
            this.bulkWriter.increment(selfRowKey, sumColumnName, elapsed);
        }

        final ColumnName maxColumnName = new ServiceResponseColumnName(serviceGroup, applicationName, applicationServiceType.getCode(), histogramSchema.getMaxStatSlot().getSlotTime());
        if (mapLinkConfiguration.isEnableMax()) {
            this.bulkWriter.updateMax(selfRowKey, maxColumnName, elapsed);
        }
    }

    @Override
    public void updatePing(String serviceGroup, String applicationName, ServiceType applicationServiceType, int elapsed, boolean isError) {
        Objects.requireNonNull(serviceGroup, "serviceGroup");
        Objects.requireNonNull(applicationName, "applicationName");

        if (logger.isDebugEnabled()) {
            logger.debug("[Received] {} {} ({})", serviceGroup, applicationName, applicationServiceType);
        }

        // make row key. rowkey is me
        final long acceptedTime = acceptedTimeService.getAcceptedTime();
        final long rowTimeSlot = timeSlot.getTimeSlot(acceptedTime);
        final RowKey selfRowKey = new ServiceGroupRowKey(serviceGroup, rowTimeSlot);

        final short slotNumber = ApplicationMapStatisticsUtils.getPingSlotNumber(applicationServiceType, elapsed, isError);
        final ColumnName selfColumnName = new ServiceResponseColumnName(serviceGroup, applicationName, applicationServiceType.getCode(), slotNumber);
        this.bulkWriter.increment(selfRowKey, selfColumnName);
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
