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
package com.navercorp.pinpoint.collector.monitor.config;

import com.navercorp.pinpoint.collector.monitor.dao.hbase.HBaseMetricsAdapter;
import com.navercorp.pinpoint.common.hbase.ConnectionFactoryBean;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.hadoop.hbase.client.MetricsConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author intr3p1d
 */
@Configuration
public class HbaseMetrics {

    private final Logger logger = LogManager.getLogger(HbaseMetrics.class);
    MetricsConnection metricsConnection;

    public HbaseMetrics() {
        logger.info("Install {}", HbaseMetrics.class.getSimpleName());
    }

    @Bean
    public HBaseMetricsAdapter collectHBaseMetrics(
            MeterRegistry meterRegistry
    ) {
        try {
            return new HBaseMetricsAdapter(meterRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
