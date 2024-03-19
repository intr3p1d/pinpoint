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
package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.collector.dao.MapStatisticsInboundDao;
import com.navercorp.pinpoint.collector.dao.MapStatisticsOutboundDao;
import com.navercorp.pinpoint.collector.dao.MapStatisticsSelfDao;
import com.navercorp.pinpoint.common.trace.ServiceType;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class ServiceGroupMapService {

    private final MapStatisticsInboundDao mapStatisticsInboundDao;
    private final MapStatisticsOutboundDao mapStatisticsOutboundDao;
    private final MapStatisticsSelfDao mapStatisticsSelfDao;

    public ServiceGroupMapService(
            MapStatisticsInboundDao mapStatisticsInboundDao,
            MapStatisticsOutboundDao mapStatisticsOutboundDao,
            MapStatisticsSelfDao mapStatisticsSelfDao
    ) {
        this.mapStatisticsInboundDao = Objects.requireNonNull(mapStatisticsInboundDao, "mapStatisticsInboundDao");
        this.mapStatisticsOutboundDao = Objects.requireNonNull(mapStatisticsOutboundDao, "mapStatisticsOutboundDao");
        this.mapStatisticsSelfDao = Objects.requireNonNull(mapStatisticsSelfDao, "mapStatisticsSelfDao");
    }


    public void updateInbound(
            String thatServiceGroup, String thatApplicationName, ServiceType thatServiceType,
            String thisServiceGroup, String thisApplicationName, ServiceType thisServiceType,
            String thisHost, int elapsed, boolean isError
    ) {
        mapStatisticsInboundDao.update(
                thatServiceGroup, thatApplicationName, thatServiceType,
                thisServiceGroup, thisApplicationName, thisServiceType,
                thisHost, elapsed, isError
        );
    }

    public void updateOutbound(
            String thatServiceGroup, String thatApplicationName, ServiceType thatServiceType,
            String thisServiceGroup, String thisApplicationName, ServiceType thisServiceType,
            String thisHost, int elapsed, boolean isError
    ) {
        mapStatisticsOutboundDao.update(
                thatServiceGroup, thatApplicationName, thatServiceType,
                thisServiceGroup, thisApplicationName, thisServiceType,
                thisHost, elapsed, isError
        );
    }

    public void updateSelfResponseTime(
            String thisServiceGroup, String thisApplicationName, ServiceType thisServiceType,
            int elapsed, boolean isError
    ) {
        mapStatisticsSelfDao.received(
            thisServiceGroup, thisApplicationName, thisServiceType, elapsed, isError
        );
    }

    public void updateAgentState(
            String thisServiceGroup, String thisApplicationName, ServiceType thisServiceType,
            int elapsed, boolean isError
    ) {
        mapStatisticsSelfDao.updatePing(
                thisServiceGroup, thisApplicationName, thisServiceType, elapsed, isError
        );
    }
}
