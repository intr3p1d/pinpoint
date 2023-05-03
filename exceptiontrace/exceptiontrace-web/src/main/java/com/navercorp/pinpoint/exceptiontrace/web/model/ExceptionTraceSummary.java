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
    private final GroupNames groupNames;

    private List<String> mostRecent;

    private static class GroupNames {
        private final List<String> criteria = new ArrayList<>();

        public GroupNames(){
        }

        public GroupNames(Collection<String> groups){
            this.criteria.addAll(groups);
        }

        public List<String> getCriteria() {
            return criteria;
        }

        public String getGroupName(){
            return String.join(", ", this.criteria);
        }
    }

    public ExceptionTraceSummary(long timestamp, long count, List<String> groupBy) {
        this.timestamp = timestamp;
        this.count = count;
        this.groupNames = new GroupNames(groupBy);
    }

    public ExceptionTraceSummary(long timestamp, long count) {
        this.timestamp = timestamp;
        this.count = count;
        this.groupNames = new GroupNames();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCount() {
        return count;
    }

    public String getGroupName() {
        return groupNames.getGroupName();
    }
}
