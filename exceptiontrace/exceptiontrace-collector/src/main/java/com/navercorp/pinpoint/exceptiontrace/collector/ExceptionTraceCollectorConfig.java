package com.navercorp.pinpoint.exceptiontrace.collector;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author intr3p1d
 */
@Profile("metric")
@Configuration
@ImportResource({"classpath*:**/applicationContext-collector-metric-namespace.xml","classpath:/applicationContext-collector-exceptiontrace-pinot-kafka.xml",})
@ComponentScan({"com.navercorp.pinpoint.exceptiontrace.collector.service", "com.navercorp.pinpoint.exceptiontrace.collector.dao"})
@PropertySource({"classpath:kafka-topic.properties", "classpath:kafka-producer-factory.properties"})
public class ExceptionTraceCollectorConfig {
}
