package com.navercorp.pinpoint.collector.monitor.dao.hbase;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.apache.hadoop.hbase.metrics.BaseSource;
import org.apache.hadoop.hbase.metrics.MetricRegistries;
import org.apache.hadoop.hbase.metrics.MetricRegistry;
import org.apache.hadoop.hbase.metrics.MetricRegistryInfo;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Counter;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Gauge;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Histogram;
import org.apache.hadoop.hbase.shaded.com.codahale.metrics.Timer;

import java.util.Collection;


public class HBaseMetricsAdapter {
    private final MeterRegistry meterRegistry;

    public HBaseMetricsAdapter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        initialize();
    }

    private void initialize() {
        Collection<MetricRegistry> metricRegistries = MetricRegistries.global().getMetricRegistries();
        for (MetricRegistry registry : metricRegistries) {
            if (registry != null) {
                registry.getMetrics().forEach((name, metric) -> {
                    if (metric instanceof Counter counter){
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
}
