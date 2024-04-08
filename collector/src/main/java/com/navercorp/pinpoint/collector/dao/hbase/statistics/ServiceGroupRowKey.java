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
public class ServiceGroupRowKey implements RowKey {
    private final String callServiceGroup;
    private final short thisServiceType;
    private final String thisApplicationName;
    private final long rowTimeSlot;

    // WARNING - cached hash value should not be included for equals/hashCode
    private int hash;

    public ServiceGroupRowKey(
            String thisServiceGroup,
            short thisServiceType, String thisApplicationName,
            long rowTimeSlot
    ) {
        this.callServiceGroup = Objects.requireNonNull(thisServiceGroup, "thisServiceGroup");
        this.thisServiceType = thisServiceType;
        this.thisApplicationName = Objects.requireNonNull(thisApplicationName, "thisApplicationName");
        this.rowTimeSlot = rowTimeSlot;
    }

    public byte[] getRowKey() {
        return ApplicationMapStatisticsUtils.makeRowKey(callServiceGroup, rowTimeSlot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceGroupRowKey that = (ServiceGroupRowKey) o;

        if (thisServiceType != that.thisServiceType) return false;
        if (rowTimeSlot != that.rowTimeSlot) return false;
        if (hash != that.hash) return false;
        if (!callServiceGroup.equals(that.callServiceGroup)) return false;
        return thisApplicationName.equals(that.thisApplicationName);
    }

    @Override
    public int hashCode() {
        int result = callServiceGroup.hashCode();
        result = 31 * result + (int) thisServiceType;
        result = 31 * result + thisApplicationName.hashCode();
        result = 31 * result + (int) (rowTimeSlot ^ (rowTimeSlot >>> 32));
        result = 31 * result + hash;
        return result;
    }

    @Override
    public String toString() {
        return "ServiceGroupRowKey{" +
                "callServiceGroup='" + callServiceGroup + '\'' +
                ", thisServiceType=" + thisServiceType +
                ", thisApplicationName='" + thisApplicationName + '\'' +
                ", rowTimeSlot=" + rowTimeSlot +
                ", hash=" + hash +
                '}';
    }
}