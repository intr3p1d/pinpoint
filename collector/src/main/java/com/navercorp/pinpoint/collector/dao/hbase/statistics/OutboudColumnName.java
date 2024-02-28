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
public class OutboudColumnName implements ColumnName {

    private final String serviceGroup;
    private final String applicationName;
    private final short serviceType;
    private final short columnSlotNumber;

    private int hash;
    private long callCount;

    public OutboudColumnName(String serviceGroup, short serviceType, String applicationName, short columnSlotNumber) {
        this.serviceGroup = Objects.requireNonNull(serviceGroup, "serviceGroup");
        this.serviceType = serviceType;
        this.applicationName = Objects.requireNonNull(applicationName, "applicationName");
        this.columnSlotNumber = columnSlotNumber;
    }


    @Override
    public byte[] getColumnName() {
        return ApplicationMapStatisticsUtils.makeColumnName(serviceGroup, applicationName, serviceType, columnSlotNumber);
    }

    @Override
    public long getCallCount() {
        return callCount;
    }

    @Override
    public void setCallCount(long callCount) {
        this.callCount = callCount;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutboudColumnName that = (OutboudColumnName) o;

        if (serviceType != that.serviceType) return false;
        if (columnSlotNumber != that.columnSlotNumber) return false;
        if (hash != that.hash) return false;
        if (callCount != that.callCount) return false;
        if (!serviceGroup.equals(that.serviceGroup)) return false;
        return applicationName.equals(that.applicationName);
    }

    @Override
    public int hashCode() {
        int result = serviceGroup.hashCode();
        result = 31 * result + applicationName.hashCode();
        result = 31 * result + (int) serviceType;
        result = 31 * result + (int) columnSlotNumber;
        result = 31 * result + hash;
        result = 31 * result + (int) (callCount ^ (callCount >>> 32));
        return result;
    }


    @Override
    public String toString() {
        return "InboudColumnName{" +
                "serviceGroup='" + serviceGroup + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", serviceType=" + serviceType +
                ", columnSlotNumber=" + columnSlotNumber +
                ", callCount=" + callCount +
                '}';
    }
}
