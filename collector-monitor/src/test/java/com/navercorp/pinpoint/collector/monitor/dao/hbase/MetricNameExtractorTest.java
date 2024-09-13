package com.navercorp.pinpoint.collector.monitor.dao.hbase;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author intr3p1d
 */
class MetricNameExtractorTest {

    @Test
    public void testCustomName() {
        String example = "org.apache.hadoop.hbase.client.MetricsConnection.executorPoolActiveThreads.84fd554c-8030-4ac4-b997-2fc1f9ce86fd@54455c8d";
        String expected = "org.apache.hadoop.hbase.client.MetricsConnection.executorPoolActiveThreads";
        String actual = MetricNameExtractor.extractName(example);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testExtractTags() {
        String example = "org.apache.hadoop.hbase.client.MetricsConnection.executorPoolActiveThreads.84fd554c-8030-4ac4-b997-2fc1f9ce86fd@54455c8d";
        Tags expected = Tags.of(
                Tag.of("clusterId", "84fd554c-8030-4ac4-b997-2fc1f9ce86fd"),
                Tag.of("connectionHash", "54455c8d")
        );
        Tags actual = MetricNameExtractor.extractTags(example);

        Assertions.assertEquals(expected, actual);
    }

}