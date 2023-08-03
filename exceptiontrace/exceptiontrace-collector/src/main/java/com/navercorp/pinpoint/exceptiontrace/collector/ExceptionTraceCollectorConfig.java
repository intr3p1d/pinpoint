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

package com.navercorp.pinpoint.exceptiontrace.collector;

import com.navercorp.pinpoint.exceptiontrace.collector.config.ExceptionMetricKafkaConfiguration;
import com.navercorp.pinpoint.pinot.config.PinotConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * @author intr3p1d
 */
@Profile("exception")
@Configuration
@Import({PinotConfiguration.class, ExceptionMetricKafkaConfiguration.class})
@ComponentScan({"com.navercorp.pinpoint.exceptiontrace.collector.service", "com.navercorp.pinpoint.exceptiontrace.collector.dao"})
@PropertySource({ExceptionTraceCollectorConfig.KAFKA_TOPIC_PROPERTIES})
public class ExceptionTraceCollectorConfig {
    public static final String KAFKA_TOPIC_PROPERTIES = "classpath:profiles/${pinpoint.profiles.active}/kafka-topic-exception.properties";
}
