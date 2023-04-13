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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author intr3p1d
 */
public class ExceptionTraceSummary {

    private static final String EMPTY_STRING = null;
    private final long timestamp;
    private final long count;
    private final String groupName;
    private String classAndMessage;

    public ExceptionTraceSummary(long timestamp, long count, String groupName) {
        this.timestamp = timestamp;
        this.count = count;
        this.groupName = groupName;
    }

    public ExceptionTraceSummary(long timestamp, long count) {
        this.timestamp = timestamp;
        this.count = count;
        this.groupName = "";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCount() {
        return count;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getClassAndMessage() {
        return classAndMessage;
    }
}
