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
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
@Validated
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
            @NotBlank String thatServiceGroup, @NotBlank String thatApplicationName, ServiceType thatServiceType,
            @NotBlank String thisServiceGroup, @NotBlank String thisApplicationName, ServiceType thisServiceType,
            @NotBlank String thisHost, int elapsed, boolean isError
    ) {
        mapStatisticsInboundDao.update(
                thatServiceGroup, thatApplicationName, thatServiceType,
                thisServiceGroup, thisApplicationName, thisServiceType,
                thisHost, elapsed, isError
        );
    }

    public void updateOutbound(
            @NotBlank String thatServiceGroup, @NotBlank String thatApplicationName, ServiceType thatServiceType,
            @NotBlank String thisServiceGroup, @NotBlank String thisApplicationName, ServiceType thisServiceType,
            @NotBlank String thisHost, int elapsed, boolean isError
    ) {
        mapStatisticsOutboundDao.update(
                thatServiceGroup, thatApplicationName, thatServiceType,
                thisServiceGroup, thisApplicationName, thisServiceType,
                thisHost, elapsed, isError
        );
    }

    public void updateSelfResponseTime(
            @NotBlank String thisServiceGroup, @NotBlank String thisApplicationName, ServiceType thisServiceType,
            int elapsed, boolean isError
    ) {
        mapStatisticsSelfDao.received(
            thisServiceGroup, thisApplicationName, thisServiceType, elapsed, isError
        );
    }

    public void updateAgentState(
            @NotBlank String thisServiceGroup, @NotBlank String thisApplicationName, ServiceType thisServiceType
    ) {
        mapStatisticsSelfDao.updatePing(
                thisServiceGroup, thisApplicationName, thisServiceType, 0, false
        );
    }
}
