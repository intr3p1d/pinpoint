package com.navercorp.pinpoint.collector.monitor.dao.hbase;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.apache.hadoop.hbase.client.MetricsConnection;
import org.apache.hadoop.hbase.metrics.BaseSource;
import org.apache.hadoop.hbase.metrics.MetricRegistries;
import org.apache.hadoop.hbase.metrics.MetricRegistry;
import org.apache.hadoop.hbase.metrics.MetricRegistryInfo;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Counter;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Gauge;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Histogram;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;


public class HBaseMetricsAdapter {
    private final Logger logger = LogManager.getLogger(HBaseMetricsAdapter.class);
    private final MeterRegistry meterRegistry;
    private final org.apache.hadoop.hbase.shaded.com.codahale.metrics.MetricRegistry metricRegistry;

    public HBaseMetricsAdapter(MeterRegistry meterRegistry, org.apache.hadoop.hbase.shaded.com.codahale.metrics.MetricRegistry metricRegistry) {
        this.meterRegistry = meterRegistry;
        this.metricRegistry = metricRegistry;
        initialize();
    }

    private void initialize() {
        logger.info("initialize");
        logger.info(System.getProperty("hbase.client.metrics.enable"));
        logger.info(System.getProperty("hbase.client.table.metrics.enable"));
        logger.info(metricRegistry);
//        Collection<MetricRegistry> metricRegistries = MetricRegistries.global().getMetricRegistries();
        if (metricRegistry != null) {
            logger.info(metricRegistry);
            metricRegistry.getMetrics().forEach((name, metric) -> {
                if (metric instanceof Counter counter) {
                    registerCounterMetric(name, counter);
                } else if (metric instanceof Timer timer) {
                    registerTimerMetric(name, timer);
                } else if (metric instanceof Gauge<?> gauge) {
                    registerGaugeMetric(name, gauge);
                } else if (metric instanceof Histogram histogram) {
                    registerHistogramMetric(name, histogram);
                }
            });
        }
    }

    private void registerCounterMetric(String name, Counter counter) {
        io.micrometer.core.instrument.Gauge.builder(customName(name), counter, Counter::getCount)
                .tags(Tags.empty())
                .register(meterRegistry);
    }

    private void registerTimerMetric(String name, Timer timer) {
        io.micrometer.core.instrument.Gauge.builder(customName(name), timer, Timer::getCount)
                .register(meterRegistry);
    }

    private void registerGaugeMetric(String name, Gauge<?> gauge) {
        io.micrometer.core.instrument.Gauge.builder(customName(name), gauge, HBaseMetricsAdapter::doubleValue)
                .tags(Tags.empty())
                .register(meterRegistry);
    }

    private void registerHistogramMetric(String name, Histogram histogram) {
        DistributionSummary.builder(customName(name))
                .tags(Tags.empty())
                .register(meterRegistry);
    }

    public static double doubleValue(Gauge<?> gauge) {
        if (gauge == null || gauge.getValue() == null) {
            return Double.NaN;
        }
        Object value = gauge.getValue();
        return Double.parseDouble(value.toString());
    }

    private static String customName(String name) {
        return "hbase.metrics." + name;
    }

    @Override
    public String toString() {
        return "HBaseMetricsAdapter{" +
                "meterRegistry=" + meterRegistry +
                '}';
    }
}
