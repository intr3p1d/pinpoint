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
package com.navercorp.pinpoint.collector.dao.hbase.statistics;

import com.navercorp.pinpoint.common.server.util.ApplicationMapStatisticsUtils;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class InboundColumnName implements ColumnName{

    private final short thatServiceType;
    private final String thatApplicationName;

    private final String thisServiceGroup;
    private final short thisServiceType;
    private final String thisApplicationName;
    private final short columnSlotNumber;

    // WARNING - cached hash value should not be included for equals/hashCode
    private int hash;

    private long callCount;

    public InboundColumnName(
            short thatServiceType, String thatApplicationName,
            String thisServiceGroup, short thisServiceType,
            String thisApplicationName, short columnSlotNumber
    ) {
        this.thatServiceType = thatServiceType;
        this.thatApplicationName = Objects.requireNonNull(thatApplicationName, "thatApplicationName");
        this.thisServiceGroup = Objects.requireNonNull(thisServiceGroup, "thisServiceGroup");
        this.thisServiceType = thisServiceType;
        this.thisApplicationName = Objects.requireNonNull(thisApplicationName, "thisApplicationName");
        this.columnSlotNumber = columnSlotNumber;
    }

    public long getCallCount() {
        return callCount;
    }

    public void setCallCount(long callCount) {
        this.callCount = callCount;
    }

    public byte[] getColumnName() {
        return ApplicationMapStatisticsUtils.makeInboundColumnName(
                thatServiceType, thatApplicationName,
                thisServiceGroup, thisServiceType, thisApplicationName,
                columnSlotNumber
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InboundColumnName that = (InboundColumnName) o;

        if (thatServiceType != that.thatServiceType) return false;
        if (thisServiceType != that.thisServiceType) return false;
        if (columnSlotNumber != that.columnSlotNumber) return false;
        if (hash != that.hash) return false;
        if (callCount != that.callCount) return false;
        if (!thatApplicationName.equals(that.thatApplicationName)) return false;
        if (!thisServiceGroup.equals(that.thisServiceGroup)) return false;
        return thisApplicationName.equals(that.thisApplicationName);
    }

    @Override
    public int hashCode() {
        int result = thatServiceType;
        result = 31 * result + thatApplicationName.hashCode();
        result = 31 * result + thisServiceGroup.hashCode();
        result = 31 * result + (int) thisServiceType;
        result = 31 * result + thisApplicationName.hashCode();
        result = 31 * result + (int) columnSlotNumber;
        result = 31 * result + hash;
        result = 31 * result + (int) (callCount ^ (callCount >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "InboundColumnName{" +
                "thatServiceType=" + thatServiceType +
                ", thatApplicationName='" + thatApplicationName + '\'' +
                ", thisServiceGroup='" + thisServiceGroup + '\'' +
                ", thisServiceType=" + thisServiceType +
                ", thisApplicationName='" + thisApplicationName + '\'' +
                ", columnSlotNumber=" + columnSlotNumber +
                ", hash=" + hash +
                ", callCount=" + callCount +
                '}';
    }
}
