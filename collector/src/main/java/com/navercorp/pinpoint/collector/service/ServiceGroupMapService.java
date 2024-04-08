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

    public void updateThisToThat(
            @NotBlank String thisServiceGroup,
            @NotBlank String thisApplicationName, ServiceType thisServiceType,
            @NotBlank String thisHost,
            @NotBlank String thatServiceGroup,
            @NotBlank String thatApplicationName, ServiceType thatServiceType,
            @NotBlank String thatHost,
            int elapsed, boolean isError
    ) {
        // this -> that
        // inbound (that for callee <- this for caller)
        // outbound (this for caller -> that for callee)

        updateInbound(
                thisServiceGroup, thisApplicationName, thisServiceType,
                thatServiceGroup, thatApplicationName, thatServiceType,
                thatHost, elapsed, isError
        );

        updateOutbound(
                thatServiceGroup, thatApplicationName, thatServiceType,
                thisServiceGroup, thisApplicationName, thisServiceType,
                thisHost, elapsed, isError
        );
    }

    public void updateThatToThis(
            @NotBlank String thisServiceGroup,
            @NotBlank String thisApplicationName, ServiceType thisServiceType,
            @NotBlank String thisHost,
            @NotBlank String thatServiceGroup,
            @NotBlank String thatApplicationName, ServiceType thatServiceType,
            @NotBlank String thatHost,
            int elapsed, boolean isError
    ) {
        // that -> this
        // inbound (this for callee <- that for caller)
        // outbound (that for caller -> this for callee)

        updateOutbound(
                thisServiceGroup, thisApplicationName, thisServiceType,
                thatServiceGroup, thatApplicationName, thatServiceType,
                thatHost, elapsed, isError
        );

        updateInbound(
                thatServiceGroup, thatApplicationName, thatServiceType,
                thisServiceGroup, thisApplicationName, thisServiceType,
                thisHost, elapsed, isError
        );

    }


    public void updateInbound(
            @NotBlank String callerServiceGroup, @NotBlank String callerApplicationName, ServiceType callerServiceType,
            @NotBlank String calleeServiceGroup, @NotBlank String calleeApplicationName, ServiceType calleeServiceType,
            @NotBlank String calleeHost, int elapsed, boolean isError
    ) {
        mapStatisticsInboundDao.update(
                callerServiceGroup, callerApplicationName, callerServiceType,
                calleeServiceGroup, calleeApplicationName, calleeServiceType,
                calleeHost, elapsed, isError
        );
    }

    public void updateOutbound(
            @NotBlank String calleeServiceGroup, @NotBlank String calleeApplicationName, ServiceType calleeServiceType,
            @NotBlank String callerServiceGroup, @NotBlank String callerApplicationName, ServiceType callerServiceType,
            @NotBlank String callerHost, int elapsed, boolean isError
    ) {
        mapStatisticsOutboundDao.update(
                calleeServiceGroup, calleeApplicationName, calleeServiceType,
                callerServiceGroup, callerApplicationName, callerServiceType,
                callerHost, elapsed, isError
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
