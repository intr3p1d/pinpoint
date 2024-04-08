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

import com.navercorp.pinpoint.common.server.util.ServiceGroupMapUtils;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class ServiceGroupRowKey implements RowKey {
    private final String serviceGroup;
    private final short serviceType;
    private final String applicationName;
    private final long rowTimeSlot;

    // WARNING - cached hash value should not be included for equals/hashCode
    private int hash;

    public ServiceGroupRowKey(
            String serviceGroup,
            short serviceType, String applicationName,
            long rowTimeSlot
    ) {
        this.serviceGroup = Objects.requireNonNull(serviceGroup, "serviceGroup");
        this.serviceType = serviceType;
        this.applicationName = Objects.requireNonNull(applicationName, "applicationName");
        this.rowTimeSlot = rowTimeSlot;
    }

    public byte[] getRowKey() {
        return ServiceGroupMapUtils.makeRowKey(serviceGroup, applicationName, serviceType, rowTimeSlot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceGroupRowKey that = (ServiceGroupRowKey) o;

        if (serviceType != that.serviceType) return false;
        if (rowTimeSlot != that.rowTimeSlot) return false;
        if (hash != that.hash) return false;
        if (!serviceGroup.equals(that.serviceGroup)) return false;
        return applicationName.equals(that.applicationName);
    }

    @Override
    public int hashCode() {
        int result = serviceGroup.hashCode();
        result = 31 * result + (int) serviceType;
        result = 31 * result + applicationName.hashCode();
        result = 31 * result + (int) (rowTimeSlot ^ (rowTimeSlot >>> 32));
        result = 31 * result + hash;
        return result;
    }

    @Override
    public String toString() {
        return "ServiceGroupRowKey{" +
                "callServiceGroup='" + serviceGroup + '\'' +
                ", thisServiceType=" + serviceType +
                ", thisApplicationName='" + applicationName + '\'' +
                ", rowTimeSlot=" + rowTimeSlot +
                ", hash=" + hash +
                '}';
    }
}