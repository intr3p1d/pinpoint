/*
 * Copyright 2023 NAVER Corp.
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
package com.navercorp.pinpoint.exceptiontrace.web.model;

/**
 * @author intr3p1d
 */
public class ExceptionTraceSummary {

    private GroupedFieldName groupedFieldName;
    private String mostRecentErrorClass;
    private String mostRecentErrorMessage;
    private long count;
    private long firstOccurred;
    private long lastOccurred;

    public ExceptionTraceSummary() {
    }

    public String getMostRecentErrorClass() {
        return mostRecentErrorClass;
    }

    public String getMostRecentErrorMessage() {
        return mostRecentErrorMessage;
    }

    public long getCount() {
        return count;
    }

    public long getFirstOccurred() {
        return firstOccurred;
    }

    public long getLastOccurred() {
        return lastOccurred;
    }

    public GroupedFieldName getGroupedFieldName() {
        return groupedFieldName;
    }

    public void setGroupedFieldName(GroupedFieldName groupedFieldName) {
        this.groupedFieldName = groupedFieldName;
    }

    @Override
    public String toString() {
        return "ExceptionTraceSummary{" +
                "groupedFieldName=" + groupedFieldName +
                ", mostRecentErrorClass='" + mostRecentErrorClass + '\'' +
                ", mostRecentErrorMessage='" + mostRecentErrorMessage + '\'' +
                ", count=" + count +
                ", firstOccurred=" + firstOccurred +
                ", lastOccurred=" + lastOccurred +
                '}';
    }
}
