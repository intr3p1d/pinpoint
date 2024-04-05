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

import com.navercorp.pinpoint.common.buffer.AutomaticBuffer;
import com.navercorp.pinpoint.common.buffer.Buffer;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class ServiceGroupColumnName implements ColumnName {

    private final String thatServiceGroupName;
    private final short thatServiceType;
    private final String thatApplicationName;

    private final short thisServiceType;
    private final String thisApplicationName;

    private final short columnSlotNumber;

    // WARNING - cached hash value should not be included for equals/hashCode
    private int hash;
    private long callCount;

    public ServiceGroupColumnName(
            String thatServiceGroupName, short thatServiceType, String thatApplicationName,
            short thisServiceType, String thisApplicationName,
            short columnSlotNumber) {
//        this.thatServiceGroupName = Objects.requireNonNull(thatServiceGroupName, "thatServiceGroupName");
        this.thatServiceGroupName = thatServiceGroupName;
        this.thatServiceType = thatServiceType;
//        this.thatApplicationName = Objects.requireNonNull(thatApplicationName, "thatApplicationName");
        this.thatApplicationName = thatApplicationName;
        this.thisServiceType = thisServiceType;
        this.thisApplicationName = Objects.requireNonNull(thisApplicationName, "thisApplicationName");
        this.columnSlotNumber = columnSlotNumber;
    }

    @Override
    public byte[] getColumnName() {
        final Buffer buffer = new AutomaticBuffer(64);
        buffer.putPrefixedString(thatServiceGroupName);
        buffer.putShort(thatServiceType);
        buffer.putPrefixedString(thatApplicationName);
        buffer.putShort(columnSlotNumber);
        buffer.putShort(thisServiceType);
        buffer.putPrefixedString(thisApplicationName);
        return new byte[0];
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
        if (!(o instanceof ServiceGroupColumnName that)) return false;

        if (thatServiceType != that.thatServiceType) return false;
        if (thisServiceType != that.thisServiceType) return false;
        if (columnSlotNumber != that.columnSlotNumber) return false;
        if (callCount != that.callCount) return false;
        if (!thatServiceGroupName.equals(that.thatServiceGroupName)) return false;
        if (!thatApplicationName.equals(that.thatApplicationName)) return false;
        return thisApplicationName.equals(that.thisApplicationName);
    }

    @Override
    public int hashCode() {
        int result = thatServiceGroupName.hashCode();
        result = 31 * result + thatApplicationName.hashCode();
        result = 31 * result + (int) thatServiceType;
        result = 31 * result + thisApplicationName.hashCode();
        result = 31 * result + (int) thisServiceType;
        result = 31 * result + (int) columnSlotNumber;
        result = 31 * result + (int) (callCount ^ (callCount >>> 32));
        return result;
    }
}
