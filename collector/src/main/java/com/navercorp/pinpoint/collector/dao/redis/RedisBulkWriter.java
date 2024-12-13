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
package com.navercorp.pinpoint.collector.dao.redis;

import com.navercorp.pinpoint.collector.dao.hbase.statistics.BulkWriter;
import com.navercorp.pinpoint.collector.dao.redis.statistics.TimeSeriesKey;
import com.navercorp.pinpoint.collector.dao.redis.statistics.TimeSeriesValue;

/**
 * @author intr3p1d
 */
public class RedisBulkWriter implements BulkWriter<TimeSeriesKey, TimeSeriesValue> {
    @Override
    public void increment(TimeSeriesKey rowKey, TimeSeriesValue columnName) {

    }

    @Override
    public void increment(TimeSeriesKey rowKey, TimeSeriesValue columnName, long addition) {

    }

    @Override
    public void updateMax(TimeSeriesKey rowKey, TimeSeriesValue columnName, long value) {

    }

    @Override
    public void flushLink() {

    }

    @Override
    public void flushAvgMax() {

    }
}
