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

    private static final String EMPTY_STRING = null;
    private final GroupedFieldName fieldName;
    private final String mostRecentErrorClassAndMessage;
    private final long count;
    private final long firstOccurred;
    private final long lastOccurred;

    public ExceptionTraceSummary(String fieldName, String mostRecentErrorClassAndMessage, long count, long firstOccurred, long lastOccured) {
        this.fieldName = new GroupedFieldName(fieldName);
        this.mostRecentErrorClassAndMessage = mostRecentErrorClassAndMessage;
        this.count = count;
        this.firstOccurred = firstOccurred;
        this.lastOccurred = lastOccured;
    }

    public GroupedFieldName getFieldName() {
        return fieldName;
    }

    public String getMostRecentErrorClassAndMessage() {
        return mostRecentErrorClassAndMessage;
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

    @Override
    public String toString() {
        return "ExceptionTraceSummary{" +
                "fieldName=" + fieldName +
                ", mostRecentErrorClassAndMessage='" + mostRecentErrorClassAndMessage + '\'' +
                ", count=" + count +
                ", firstOccurred=" + firstOccurred +
                ", lastOccurred=" + lastOccurred +
                '}';
    }
}
