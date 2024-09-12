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
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.AsyncConnectionImpl;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionImplementation;
import org.apache.hadoop.hbase.client.MetricsConnection;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.MetricRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;
import java.util.Optional;

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
            MeterRegistry meterRegistry,
            @Qualifier("hbaseConnection")
            FactoryBean<Connection> connectionFactoryBean,
            @Qualifier("hbaseAsyncConnection")
            FactoryBean<AsyncConnection> asyncConnectionFactoryBean
    ) {
        logger.info("collectHBaseMetrics {}", HbaseMetrics.class.getSimpleName());
        try {

            ConnectionImplementation connection = (ConnectionImplementation) connectionFactoryBean.getObject();
            logger.info(connection);
            MetricsConnection metricsConnection1 = getMetricsConnection(connection);
            logger.info(metricsConnection1);
            MetricRegistry metricRegistry1 = getMetricRegistry(metricsConnection1);
            logger.info(metricRegistry1);
            HBaseMetricsAdapter adapter1 = new HBaseMetricsAdapter(meterRegistry, metricRegistry1);
            logger.info(adapter1);

            AsyncConnectionImpl asyncConnection = (AsyncConnectionImpl) asyncConnectionFactoryBean.getObject();
            logger.info(asyncConnection);

            MetricsConnection metricsConnection = getMetricsConnection(asyncConnection)
                    .orElseThrow(() -> new NoSuchElementException("MetricsConnection not present"));
            logger.info(metricsConnection);

            MetricRegistry metricRegistry = getMetricRegistry(metricsConnection);
            logger.info(metricRegistry);

            HBaseMetricsAdapter adapter = new HBaseMetricsAdapter(meterRegistry, metricRegistry);
            logger.info(adapter);
            return adapter;
        } catch (Exception e) {
            logger.error("HbaseMetrics Error: ", e);
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public static Optional<MetricsConnection> getMetricsConnection(AsyncConnectionImpl asyncConnection) {
        try {
            Method method = asyncConnection.getClass().getDeclaredMethod("getConnectionMetrics");
            method.setAccessible(true);
            return (Optional<MetricsConnection>) method.invoke(asyncConnection);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static MetricsConnection getMetricsConnection(ClusterConnection connectionImplementation) {
        try {
            Method method = connectionImplementation.getClass().getDeclaredMethod("getConnectionMetrics");
            method.setAccessible(true);
            return (MetricsConnection) method.invoke(connectionImplementation);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static MetricRegistry getMetricRegistry(MetricsConnection metricsConnection) {
        try {
            Method method = metricsConnection.getClass().getDeclaredMethod("getMetricRegistry");
            method.setAccessible(true);
            return (MetricRegistry) method.invoke(metricsConnection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
