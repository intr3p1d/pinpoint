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

package com.navercorp.pinpoint.exceptiontrace.collector.dao;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.common.util.StringPrecondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final KafkaTemplate<String, SpanEventExceptionVo> kafkaSpanEventExceptionTemplate;

    private final String topic;

    public PinotExceptionTraceDao(@Qualifier("kafkaSpanEventExceptionTemplate") KafkaTemplate<String, SpanEventExceptionVo> kafkaSpanEventExceptionTemplate,
                                  @Value("${kafka.exceptiontrace.topic}") String topic) {
        this.kafkaSpanEventExceptionTemplate = Objects.requireNonNull(kafkaSpanEventExceptionTemplate, "kafkaSpanEventExceptionTemplate");
        this.topic = StringPrecondition.requireHasLength(topic, "topic");
    }

    @Override
    public void insert(List<SpanEventException> spanEventExceptions) {
        Objects.requireNonNull(spanEventExceptions);
        logger.info("Pinot data insert: {}", spanEventExceptions.toString());

        for (SpanEventException spanEventException : spanEventExceptions) {
            this.kafkaSpanEventExceptionTemplate.send(topic, SpanEventExceptionVo.valueOf(spanEventException));
        }
    }
}
