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
    private final long rowTimeSlot;

    // WARNING - cached hash value should not be included for equals/hashCode
    private int hash;

    public ServiceGroupRowKey(String callServiceGroup, long rowTimeSlot) {
        this.callServiceGroup = Objects.requireNonNull(callServiceGroup, "callServiceGroup");
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

        if (rowTimeSlot != that.rowTimeSlot) return false;
        if (hash != that.hash) return false;
        return callServiceGroup.equals(that.callServiceGroup);
    }

    @Override
    public int hashCode() {
        int result = callServiceGroup.hashCode();
        result = 31 * result + (int) (rowTimeSlot ^ (rowTimeSlot >>> 32));
        result = 31 * result + hash;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallRowKey{");
        sb.append("callServiceGroup='").append(callServiceGroup).append('\'');
        sb.append(", rowTimeSlot=").append(rowTimeSlot);
        sb.append('}');
        return sb.toString();
    }
}