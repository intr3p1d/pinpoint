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
    private GroupedFieldName fieldName;
    private final String mostRecentErrorClass;
    private final String mostRecentErrorMessage;
    private final long count;
    private final long firstOccurred;
    private final long lastOccurred;

    public ExceptionTraceSummary(String mostRecentErrorClass, String mostRecentErrorMessage, long count, long firstOccurred, long lastOccured) {
        this.mostRecentErrorClass = mostRecentErrorClass;
        this.mostRecentErrorMessage = mostRecentErrorMessage;
        this.count = count;
        this.firstOccurred = firstOccurred;
        this.lastOccurred = lastOccured;
    }

    public GroupedFieldName getFieldName() {
        return fieldName;
    }

    public void setFieldName(GroupedFieldName fieldName) {
        this.fieldName = fieldName;
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

    @Override
    public String toString() {
        return "ExceptionTraceSummary{" +
                "fieldName=" + fieldName +
                ", mostRecentErrorClass='" + mostRecentErrorClass + '\'' +
                ", mostRecentErrorMessage='" + mostRecentErrorMessage + '\'' +
                ", count=" + count +
                ", firstOccurred=" + firstOccurred +
                ", lastOccurred=" + lastOccurred +
                '}';
    }
}
